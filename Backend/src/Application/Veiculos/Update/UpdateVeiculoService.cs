using Contracts.Authentications;
using Contracts.Repositories;
using Contracts.Repositories.Veiculos;
using Domain.Exceptions;

namespace Application.Veiculos.Update;

public class UpdateVeiculoService
{
    private readonly IVeiculoRepository _veiculoRepository;
    private readonly IUnitOfWork _unitOfWork;
    private readonly ICurrentUsuarioService _currentUsuarioService;

    public UpdateVeiculoService(
        IVeiculoRepository veiculoRepository,
        IUnitOfWork unitOfWork,
        ICurrentUsuarioService currentUsuarioService)
    {
        _veiculoRepository = veiculoRepository;
        _unitOfWork = unitOfWork;
        _currentUsuarioService = currentUsuarioService;
    }

    public async Task ExecuteAsync(Guid id, UpdateVeiculoRequest request, CancellationToken ct)
    {
        var usuarioId = _currentUsuarioService.GetUsuarioId();

        var veiculo = await _veiculoRepository.GetByIdAsync(id, ct);

        if (veiculo is null || veiculo.UsuarioId != usuarioId)
            throw new NotFoundException("Veículo não encontrado.");

        veiculo.Atualizar(
            request.Placa,
            request.Apelido,
            request.TipoVeiculo,
            request.DiasNotificacaoOdometro,
            request.Descricao);

        if (request.Odometro.HasValue)
            veiculo.AtualizarOdometroAtual(request.Odometro.Value, DateOnly.FromDateTime(DateTime.UtcNow));

        await _unitOfWork.SaveChangesAsync(ct);
    }
}
