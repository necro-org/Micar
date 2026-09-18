using Domain.Veiculos;

namespace Application.Veiculos.Create;

public class CreateVeiculoRequest
{
    public required string Placa { get; set; }
    public required string Apelido { get; set; }
    public required TipoVeiculoEnum TipoVeiculo { get; set; }
    public int? Odometro { get; set; }
    public required int DiasNotificacaoOdometro { get; set; }
    public string? Descricao { get; set; }
}
