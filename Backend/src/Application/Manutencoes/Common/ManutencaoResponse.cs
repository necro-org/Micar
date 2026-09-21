using Domain.Common;
using Domain.Manutencoes;

namespace Application.Manutencoes.Common;

public class ManutencaoResponse
{
    public Guid Id { get; set; }
    public DateOnly Data { get; set; }
    public string Nome { get; set; }
    public Guid VeiculoId { get; set; }
    public Guid? RegistroOdometroId { get; set; }
    public int? Odometro { get; set; }
    public int? OdometroVencimento { get; set; }
    public DateOnly? DataVencimento { get; set; }
    public decimal? Valor { get; set; }
    public DateOnly? DataConclusao { get; set; }
    public int? DiasRestantes { get; set; }
    public int? KmRestantes { get; set; }
    public string? Descricao { get; set; }
    public NivelAlertaEnum Status { get; set; }
    public bool NotificacaoVisualizada { get; set; }

    public ManutencaoResponse(Manutencao manutencao, DateOnly hoje, int? odometroAtual)
    {
        Id = manutencao.Id;
        Data = manutencao.Data;
        Nome = manutencao.Nome;
        VeiculoId = manutencao.VeiculoId;
        RegistroOdometroId = manutencao.RegistroOdometro?.Id;
        Odometro = manutencao.RegistroOdometro?.Odometro;
        OdometroVencimento = manutencao.OdometroVencimento;
        DataVencimento = manutencao.DataVencimento;
        Valor = manutencao.Valor;
        DataConclusao = manutencao.DataConclusao;
        DiasRestantes = manutencao.CalcularDiasRestantes(hoje);
        KmRestantes = manutencao.CalcularKmRestantes(odometroAtual);
        Descricao = manutencao.Descricao;
        Status = manutencao.CalcularStatus(hoje, odometroAtual);
        NotificacaoVisualizada = Status switch
        {
            NivelAlertaEnum.Critico => manutencao.DataVisualizacaoNotificacaoVencida is not null,
            NivelAlertaEnum.Atencao => manutencao.DataVisualizacaoNotificacaoAVencer is not null,
            _ => false,
        };
    }
}
