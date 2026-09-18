using Domain.Common;
using Domain.Exceptions;
using Domain.RegistrosOdometro;
using Domain.Veiculos;

namespace Domain.Manutencoes;

public class Manutencao : Entity
{
    public DateOnly Data { get; private set; }
    public string Nome { get; private set; } = string.Empty;
    public Guid VeiculoId { get; private set; }
    public Veiculo? Veiculo { get; private set; }
    public RegistroOdometro? RegistroOdometro { get; private set; }
    public int? OdometroVencimento { get; private set; }
    public DateOnly? DataVencimento { get; private set; }
    public decimal? Valor { get; private set; }
    public DateOnly? DataConclusao { get; private set; }
    public string? Descricao { get; private set; }
    private const double MargemProporcao = 0.15;
    private const int MargemMinimaDias = 7;
    private const int MargemMaximaDias = 45;
    private const int MargemMinimaKm = 200;
    private const int MargemMaximaKm = 1000;

    private Manutencao()
    {
    }

    internal Manutencao(DateOnly data, string nome, Guid veiculoId, int? odometroVencimento, DateOnly? dataVencimento, decimal? valor, string? descricao)
    {
        if (veiculoId == Guid.Empty)
            throw new BadRequestException("Veículo é obrigatório.");

        Data = ValidarData(data);
        Nome = ValidarNome(nome);
        VeiculoId = veiculoId;
        OdometroVencimento = ValidarOdometroVencimento(odometroVencimento);
        DataVencimento = dataVencimento;
        Valor = ValidarValor(valor);
        Descricao = descricao;
    }

    internal void VincularRegistroOdometro(RegistroOdometro registroOdometro)
    {
        RegistroOdometro = registroOdometro;
    }

    public NivelAlertaEnum CalcularStatus(DateOnly hoje, int? odometroAtual)
    {
        var statusPorData = CalcularStatusPorData(hoje);
        var statusPorOdometro = CalcularStatusPorOdometro(odometroAtual);

        return statusPorData > statusPorOdometro ? statusPorData : statusPorOdometro;
    }

    public int? CalcularDiasRestantes(DateOnly hoje)
    {
        if (DataConclusao is not null)
            return null;

        return DataVencimento is not null
            ? DataVencimento.Value.DayNumber - hoje.DayNumber
            : null;
    }

    public int? CalcularKmRestantes(int? odometroAtual)
    {
        if (DataConclusao is not null)
            return null;

        return OdometroVencimento is not null && odometroAtual is not null
            ? OdometroVencimento.Value - odometroAtual.Value
            : null;
    }

    private NivelAlertaEnum CalcularStatusPorData(DateOnly hoje)
    {
        if (DataVencimento is null)
            return NivelAlertaEnum.Normal;

        if (hoje > DataVencimento.Value)
            return NivelAlertaEnum.Critico;

        var diasRestantes = DataVencimento.Value.DayNumber - hoje.DayNumber;

        return diasRestantes <= CalcularMargemDias() ? NivelAlertaEnum.Atencao : NivelAlertaEnum.Normal;
    }

    private NivelAlertaEnum CalcularStatusPorOdometro(int? odometroAtual)
    {
        if (OdometroVencimento is null || odometroAtual is null)
            return NivelAlertaEnum.Normal;

        if (odometroAtual.Value >= OdometroVencimento.Value)
            return NivelAlertaEnum.Critico;

        var kmRestantes = OdometroVencimento.Value - odometroAtual.Value;

        return kmRestantes <= CalcularMargemKm() ? NivelAlertaEnum.Atencao : NivelAlertaEnum.Normal;
    }

    private int CalcularMargemDias()
    {
        var intervaloDias = DataVencimento!.Value.DayNumber - Data.DayNumber;
        if (intervaloDias <= 0)
            return MargemMinimaDias;

        return Math.Clamp((int)Math.Round(intervaloDias * MargemProporcao), MargemMinimaDias, MargemMaximaDias);
    }

    private int CalcularMargemKm()
    {
        var odometroBase = RegistroOdometro?.Odometro ?? 0;
        var intervaloKm = OdometroVencimento!.Value - odometroBase;
        if (intervaloKm <= 0)
            return MargemMinimaKm;

        return Math.Clamp((int)Math.Round(intervaloKm * MargemProporcao), MargemMinimaKm, MargemMaximaKm);
    }

    public void Atualizar(
        DateOnly data,
        string nome,
        int? odometroVencimento,
        DateOnly? dataVencimento,
        decimal? valor,
        int odometro,
        DateOnly? dataConclusao,
        string? descricao)
    {
        if (RegistroOdometro is null)
            throw new InvalidOperationException("Manutenção não possui registro de odômetro vinculado.");

        Data = ValidarData(data);
        Nome = ValidarNome(nome);
        OdometroVencimento = ValidarOdometroVencimento(odometroVencimento);
        DataVencimento = dataVencimento;
        Valor = ValidarValor(valor);
        DataConclusao = dataConclusao;
        Descricao = descricao;
        RegistroOdometro.Atualizar(data, odometro);
    }

    public void Concluir(DateOnly dataConclusao)
    {
        DataConclusao = dataConclusao;
    }

    private static DateOnly ValidarData(DateOnly data)
    {
        if (data == default)
            throw new BadRequestException("Data é obrigatória.");

        return data;
    }

    private static string ValidarNome(string nome)
    {
        if (string.IsNullOrWhiteSpace(nome))
            throw new BadRequestException("Nome é obrigatório.");

        return nome;
    }

    private static int? ValidarOdometroVencimento(int? odometroVencimento)
    {
        if (odometroVencimento is < 0)
            throw new BadRequestException("Odômetro de vencimento deve ser maior ou igual a zero.");

        return odometroVencimento;
    }

    private static decimal? ValidarValor(decimal? valor)
    {
        if (valor is <= 0)
            throw new BadRequestException("Valor da manutenção deve ser maior que zero.");

        return valor;
    }
}
