namespace Application.Manutencoes.NotificarPendentes;

public class NotificacaoFalhaResponse
{
    public Guid ManutencaoId { get; set; }
    public string Erro { get; set; }

    public NotificacaoFalhaResponse(Guid manutencaoId, string erro)
    {
        ManutencaoId = manutencaoId;
        Erro = erro;
    }
}
