using Domain.Veiculos;

namespace Application.Veiculos.Common;

public class VeiculoResponse
{
    public Guid Id { get; set; }
    public string Placa { get; set; }
    public string Apelido { get; set; }
    public TipoVeiculoEnum TipoVeiculo { get; set; }
    public int? OdometroAtual { get; set; }
    public int DiasNotificacaoOdometro { get; set; }
    public string? Descricao { get; set; }

    public VeiculoResponse(Veiculo veiculo)
    {
        Id = veiculo.Id;
        Placa = veiculo.Placa;
        Apelido = veiculo.Apelido;
        TipoVeiculo = veiculo.TipoVeiculo;
        OdometroAtual = veiculo.UltimoRegistroOdometro?.Odometro;
        DiasNotificacaoOdometro = veiculo.DiasNotificacaoOdometro;
        Descricao = veiculo.Descricao;
    }
}
