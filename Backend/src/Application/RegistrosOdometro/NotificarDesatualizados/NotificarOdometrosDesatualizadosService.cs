using Application.PushNotifications.Enviar;
using Contracts.Repositories.Veiculos;
using Domain.Common;

namespace Application.RegistrosOdometro.NotificarDesatualizados;

public class NotificarOdometrosDesatualizadosService
{
    private readonly IVeiculoRepository _veiculoRepository;
    private readonly EnviarNotificacaoService _enviarNotificacaoService;

    public NotificarOdometrosDesatualizadosService(
        IVeiculoRepository veiculoRepository,
        EnviarNotificacaoService enviarNotificacaoService)
    {
        _veiculoRepository = veiculoRepository;
        _enviarNotificacaoService = enviarNotificacaoService;
    }

    public async Task<NotificarOdometrosDesatualizadosResponse> ExecuteAsync(CancellationToken ct)
    {
        var hoje = DateOnly.FromDateTime(DateTime.UtcNow);
        var veiculos = await _veiculoRepository.GetAllAsync(ct);

        var veiculosDesatualizados = veiculos
            .Where(v => v.UltimoRegistroOdometro is not null
                && v.UltimoRegistroOdometro.CalcularStatus(hoje, v.DiasNotificacaoOdometro) == NivelAlertaEnum.Critico)
            .ToList();

        var falhas = new List<NotificacaoFalhaResponse>();

        foreach (var veiculo in veiculosDesatualizados)
        {
            var dias = veiculo.UltimoRegistroOdometro!.CalcularDiasSemAtualizacao(hoje);

            var data = new Dictionary<string, string>
            {
                ["tipo"] = "odometro",
                ["veiculoId"] = veiculo.Id.ToString(),
            };

            var resultado = await _enviarNotificacaoService.ExecuteAsync(
                veiculo.UsuarioId,
                "Odômetro desatualizado",
                $"O odômetro de {veiculo.Apelido} está há {dias} dias sem atualização. Abra o app para atualizar.",
                ct,
                data);

            if (!resultado.Sucesso)
                falhas.Add(new NotificacaoFalhaResponse(veiculo.UsuarioId, resultado.Erro!));
        }

        var totalNotificados = veiculosDesatualizados.Count - falhas.Count;

        return new NotificarOdometrosDesatualizadosResponse(veiculos.Count, totalNotificados, falhas);
    }
}
