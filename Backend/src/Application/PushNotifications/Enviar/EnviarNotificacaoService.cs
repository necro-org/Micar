using Contracts.PushNotifications;
using Contracts.Repositories;
using Contracts.Repositories.PushTokens;

namespace Application.PushNotifications.Enviar;

public class EnviarNotificacaoService
{
    private readonly IPushTokenRepository _pushTokenRepository;
    private readonly IPushNotificationSender _pushNotificationSender;
    private readonly IUnitOfWork _unitOfWork;

    public EnviarNotificacaoService(
        IPushTokenRepository pushTokenRepository,
        IPushNotificationSender pushNotificationSender,
        IUnitOfWork unitOfWork)
    {
        _pushTokenRepository = pushTokenRepository;
        _pushNotificationSender = pushNotificationSender;
        _unitOfWork = unitOfWork;
    }

    public async Task<EnviarNotificacaoResultado> ExecuteAsync(
        Guid usuarioId,
        string titulo,
        string corpo,
        CancellationToken ct,
        IReadOnlyDictionary<string, string>? data = null)
    {
        var pushTokens = await _pushTokenRepository.GetAtivosByUsuarioIdAsync(usuarioId, ct);

        if (pushTokens.Count == 0)
            return new EnviarNotificacaoResultado { Sucesso = false, Erro = "Usuário não possui dispositivo com notificações registradas." };

        var resultados = await _pushNotificationSender.EnviarAsync(
            pushTokens.Select(t => t.Token),
            titulo,
            corpo,
            data,
            ct);

        var tokensInvalidos = resultados
            .Where(r => r.TokenInvalido)
            .Select(r => r.Token)
            .ToHashSet();

        if (tokensInvalidos.Count > 0)
        {
            foreach (var pushToken in pushTokens.Where(t => tokensInvalidos.Contains(t.Token)))
                pushToken.Desativar();

            await _unitOfWork.SaveChangesAsync(ct);
        }

        if (resultados.Any(r => r.Sucesso))
            return new EnviarNotificacaoResultado { Sucesso = true };

        var erro = resultados.FirstOrDefault(r => !string.IsNullOrWhiteSpace(r.Mensagem))?.Mensagem
            ?? "Falha ao enviar notificação.";

        return new EnviarNotificacaoResultado { Sucesso = false, Erro = erro };
    }
}
