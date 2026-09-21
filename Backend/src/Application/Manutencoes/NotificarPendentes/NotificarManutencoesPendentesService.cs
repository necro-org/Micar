using Application.PushNotifications.Enviar;
using Contracts.Repositories.Manutencoes;
using Domain.Common;

namespace Application.Manutencoes.NotificarPendentes;

public class NotificarManutencoesPendentesService
{
    private readonly IManutencaoRepository _manutencaoRepository;
    private readonly EnviarNotificacaoService _enviarNotificacaoService;

    public NotificarManutencoesPendentesService(
        IManutencaoRepository manutencaoRepository,
        EnviarNotificacaoService enviarNotificacaoService)
    {
        _manutencaoRepository = manutencaoRepository;
        _enviarNotificacaoService = enviarNotificacaoService;
    }

    public async Task<NotificarManutencoesPendentesResponse> ExecuteAsync(CancellationToken ct)
    {
        var hoje = DateOnly.FromDateTime(DateTime.UtcNow);
        var pendentes = await _manutencaoRepository.GetAllPendentesAsync(ct);

        var falhas = new List<NotificacaoFalhaResponse>();
        var totalNotificadas = 0;

        foreach (var manutencao in pendentes)
        {
            var veiculo = manutencao.Veiculo;
            if (veiculo is null)
                continue;

            var odometroAtual = veiculo.UltimoRegistroOdometro?.Odometro;
            var status = manutencao.CalcularStatus(hoje, odometroAtual);

            string titulo;
            string corpo;

            if (status == NivelAlertaEnum.Critico && manutencao.DataVisualizacaoNotificacaoVencida is null)
            {
                titulo = "Manutenção vencida";
                corpo = $"A manutenção \"{manutencao.Nome}\" de {veiculo.Apelido} está vencida.";
            }
            else if (status == NivelAlertaEnum.Atencao && manutencao.DataVisualizacaoNotificacaoAVencer is null)
            {
                titulo = "Manutenção próxima do vencimento";
                corpo = $"A manutenção \"{manutencao.Nome}\" de {veiculo.Apelido} está próxima do vencimento.";
            }
            else
            {
                continue;
            }

            var data = new Dictionary<string, string>
            {
                ["tipo"] = "manutencao",
                ["veiculoId"] = veiculo.Id.ToString(),
                ["manutencaoId"] = manutencao.Id.ToString(),
            };

            var resultado = await _enviarNotificacaoService.ExecuteAsync(veiculo.UsuarioId, titulo, corpo, ct, data);

            if (resultado.Sucesso)
                totalNotificadas++;
            else
                falhas.Add(new NotificacaoFalhaResponse(manutencao.Id, resultado.Erro!));
        }

        return new NotificarManutencoesPendentesResponse(pendentes.Count, totalNotificadas, falhas);
    }
}
