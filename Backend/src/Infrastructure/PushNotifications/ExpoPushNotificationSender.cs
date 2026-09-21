using Contracts.PushNotifications;
using Domain.PushNotifications;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Infrastructure.PushNotifications;

public class ExpoPushNotificationSender : IPushNotificationSender
{
    private const string ExpoPushUrl = "https://exp.host/--/api/v2/push/send";
    private const string DeviceNotRegisteredError = "DeviceNotRegistered";

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNameCaseInsensitive = true,
    };

    private static readonly JsonSerializerOptions SendJsonOptions = new()
    {
        DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull,
    };

    private readonly HttpClient _httpClient;

    public ExpoPushNotificationSender(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<IReadOnlyList<PushNotificationResultado>> EnviarAsync(
        IEnumerable<string> tokens,
        string titulo,
        string corpo,
        IReadOnlyDictionary<string, string>? data,
        CancellationToken ct)
    {
        var tokensList = tokens.ToList();

        if (tokensList.Count == 0)
            return [];

        var mensagens = tokensList.Select(token => new
        {
            to = token,
            title = titulo,
            body = corpo,
            data,
        });

        HttpResponseMessage response;

        try
        {
            response = await _httpClient.PostAsJsonAsync(ExpoPushUrl, mensagens, SendJsonOptions, ct);
        }
        catch (HttpRequestException)
        {
            return CriarResultadosDeFalha(tokensList);
        }

        if (!response.IsSuccessStatusCode)
            return CriarResultadosDeFalha(tokensList);

        var resultado = await response.Content.ReadFromJsonAsync<ExpoPushResponse>(JsonOptions, ct);
        var tickets = resultado?.Data ?? [];

        return tokensList
            .Select((token, index) =>
            {
                var ticket = index < tickets.Count ? tickets[index] : null;
                var sucesso = ticket?.Status == "ok";

                return new PushNotificationResultado
                {
                    Token = token,
                    Sucesso = sucesso,
                    TokenInvalido = ticket?.Details?.Error == DeviceNotRegisteredError,
                    Mensagem = sucesso ? null : ticket?.Message,
                };
            })
            .ToList();
    }

    private static List<PushNotificationResultado> CriarResultadosDeFalha(List<string> tokens)
    {
        return tokens
            .Select(token => new PushNotificationResultado
            {
                Token = token,
                Sucesso = false,
                Mensagem = "Falha ao comunicar com o serviço de notificações.",
            })
            .ToList();
    }
}
