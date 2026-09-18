using Contracts.Authentications;
using Contracts.Repositories;
using Contracts.Repositories.Veiculos;
using Domain.Veiculos;

namespace Application.Veiculos.Create;

public class CreateVeiculoService
{
    private readonly IVeiculoRepository _veiculoRepository;
    private readonly IUnitOfWork _unitOfWork;
    private readonly ICurrentUsuarioService _currentUsuarioService;

    public CreateVeiculoService(
        IVeiculoRepository veiculoRepository,
        IUnitOfWork unitOfWork,
        ICurrentUsuarioService currentUsuarioService)
    {
        _veiculoRepository = veiculoRepository;
        _unitOfWork = unitOfWork;
        _currentUsuarioService = currentUsuarioService;
    }

    public async Task ExecuteAsync(CreateVeiculoRequest request, CancellationToken ct)
    {
        var usuarioId = _currentUsuarioService.GetUsuarioId();

        var veiculo = new Veiculo(
            request.Placa,
            request.Apelido,
            request.TipoVeiculo,
            usuarioId,
            request.DiasNotificacaoOdometro,
            request.Descricao);

        await _veiculoRepository.AddAsync(veiculo, ct);

        if (request.Odometro.HasValue)
            veiculo.AtualizarOdometroAtual(request.Odometro.Value, DateOnly.FromDateTime(DateTime.UtcNow));

        await _unitOfWork.SaveChangesAsync(ct);
    }
}
