namespace Application.Manutencoes.NotificarPendentes;

public class NotificarManutencoesPendentesResponse
{
    public int TotalVerificadas { get; set; }
    public int TotalNotificadas { get; set; }
    public IReadOnlyList<NotificacaoFalhaResponse> Falhas { get; set; }

    public NotificarManutencoesPendentesResponse(int totalVerificadas, int totalNotificadas, IReadOnlyList<NotificacaoFalhaResponse> falhas)
    {
        TotalVerificadas = totalVerificadas;
        TotalNotificadas = totalNotificadas;
        Falhas = falhas;
    }
}
