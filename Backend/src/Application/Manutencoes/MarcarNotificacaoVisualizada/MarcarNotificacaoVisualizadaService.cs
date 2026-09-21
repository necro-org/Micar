using Contracts.Authentications;
using Contracts.Repositories;
using Contracts.Repositories.Manutencoes;
using Domain.Exceptions;

namespace Application.Manutencoes.MarcarNotificacaoVisualizada;

public class MarcarNotificacaoVisualizadaService
{
    private readonly IManutencaoRepository _manutencaoRepository;
    private readonly IUnitOfWork _unitOfWork;
    private readonly ICurrentUsuarioService _currentUsuarioService;

    public MarcarNotificacaoVisualizadaService(
        IManutencaoRepository manutencaoRepository,
        IUnitOfWork unitOfWork,
        ICurrentUsuarioService currentUsuarioService)
    {
        _manutencaoRepository = manutencaoRepository;
        _unitOfWork = unitOfWork;
        _currentUsuarioService = currentUsuarioService;
    }

    public async Task ExecuteAsync(Guid id, CancellationToken ct)
    {
        var usuarioId = _currentUsuarioService.GetUsuarioId();

        var manutencao = await _manutencaoRepository.GetByIdAsync(id, ct);

        if (manutencao is null || manutencao.Veiculo?.UsuarioId != usuarioId)
            throw new NotFoundException("Manutenção não encontrada.");

        var hoje = DateOnly.FromDateTime(DateTime.UtcNow);
        var odometroAtual = manutencao.Veiculo?.UltimoRegistroOdometro?.Odometro;

        manutencao.DefinirNotificacaoVisualizada(true, hoje, odometroAtual);

        await _unitOfWork.SaveChangesAsync(ct);
    }
}
