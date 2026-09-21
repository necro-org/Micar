namespace Application.Manutencoes.Update;

public class UpdateManutencaoRequest
{
    public required string Nome { get; set; }
    public required DateOnly Data { get; set; }
    public required int Odometro { get; set; }
    public int? OdometroVencimento { get; set; }
    public DateOnly? DataVencimento { get; set; }
    public decimal? Valor { get; set; }
    public DateOnly? DataConclusao { get; set; }
    public string? Descricao { get; set; }
    public bool NotificacaoVisualizada { get; set; }
}
