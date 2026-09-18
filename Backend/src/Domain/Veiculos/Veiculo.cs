using Domain.Common;
using Domain.Exceptions;
using Domain.Manutencoes;
using Domain.RegistrosOdometro;
using Domain.Usuarios;

namespace Domain.Veiculos;

public class Veiculo : Entity
{
    public string Placa { get; private set; } = string.Empty;
    public string Apelido { get; private set; } = string.Empty;
    public TipoVeiculoEnum TipoVeiculo { get; private set; }
    public Guid UsuarioId { get; private set; }
    public Usuario? Usuario { get; private set; }
    public ICollection<RegistroOdometro> RegistrosOdometro { get; private set; } = new List<RegistroOdometro>();
    public ICollection<Manutencao> Manutencoes { get; private set; } = new List<Manutencao>();
    public int DiasNotificacaoOdometro { get; private set; }
    public string? Descricao { get; private set; }

    public RegistroOdometro? UltimoRegistroOdometro =>
        RegistrosOdometro.OrderByDescending(r => r.Data).ThenByDescending(r => r.Odometro).FirstOrDefault();

    public const int PlacaLength = 7;
    public const int DiasNotificacaoOdometroPadrao = 30;

    private Veiculo()
    {
    }

    public Veiculo(
        string placa,
        string apelido,
        TipoVeiculoEnum tipoVeiculo,
        Guid usuarioId,
        int diasNotificacaoOdometro,
        string? descricao)
    {
        if (usuarioId == Guid.Empty)
            throw new BadRequestException("Usuário é obrigatório.");

        Placa = ValidarPlaca(placa);
        Apelido = ValidarApelido(apelido);
        TipoVeiculo = tipoVeiculo;
        UsuarioId = usuarioId;
        DiasNotificacaoOdometro = ValidarDiasNotificacaoOdometro(diasNotificacaoOdometro);
        Descricao = descricao;
    }

    public void Atualizar(
        string placa,
        string apelido,
        TipoVeiculoEnum tipoVeiculo,
        int diasNotificacaoOdometro,
        string? descricao)
    {
        Placa = ValidarPlaca(placa);
        Apelido = ValidarApelido(apelido);
        TipoVeiculo = tipoVeiculo;
        DiasNotificacaoOdometro = ValidarDiasNotificacaoOdometro(diasNotificacaoOdometro);
        Descricao = descricao;
    }

    public void AtualizarOdometroAtual(int odometro, DateOnly data)
    {
        if (UltimoRegistroOdometro is null)
            RegistrosOdometro.Add(new RegistroOdometro(data, odometro, Id));
        else if (UltimoRegistroOdometro.Odometro != odometro)
            UltimoRegistroOdometro.Atualizar(data, odometro);
    }

    public void RegistrarOdometro(int odometro, DateOnly data)
    {
        GarantirOdometroValido(odometro);

        RegistrosOdometro.Add(new RegistroOdometro(data, odometro, Id));
    }

    public void RegistrarManutencao(
        DateOnly data,
        string nome,
        int odometro,
        int? odometroVencimento,
        DateOnly? dataVencimento,
        decimal? valor,
        string? descricao)
    {
        var manutencao = new Manutencao(data, nome, Id, odometroVencimento, dataVencimento, valor, descricao);
        var registroOdometro = new RegistroOdometro(data, odometro, Id, manutencao.Id);

        manutencao.VincularRegistroOdometro(registroOdometro);
        Manutencoes.Add(manutencao);
        RegistrosOdometro.Add(registroOdometro);
    }

    private void GarantirOdometroValido(int odometro)
    {
        if (UltimoRegistroOdometro is not null && odometro <= UltimoRegistroOdometro.Odometro)
            throw new BadRequestException(
                $"Odômetro deve ser maior que o último registrado ({UltimoRegistroOdometro.Odometro} km).");
    }

    private static string ValidarPlaca(string placa)
    {
        if (string.IsNullOrWhiteSpace(placa))
            throw new BadRequestException("Placa é obrigatória.");

        if (placa.Length != PlacaLength)
            throw new BadRequestException($"Placa deve ter exatamente {PlacaLength} caracteres.");

        return placa.ToUpper();
    }

    private static string ValidarApelido(string apelido)
    {
        if (string.IsNullOrWhiteSpace(apelido))
            throw new BadRequestException("Apelido é obrigatório.");

        return apelido;
    }

    private static int ValidarDiasNotificacaoOdometro(int dias)
    {
        if (dias <= 0)
            throw new BadRequestException("Dias para notificação do odômetro deve ser maior que zero.");

        return dias;
    }
}
