using Domain.Manutencoes;

namespace Contracts.Repositories.Manutencoes;

public interface IManutencaoRepository
{
    Task<Manutencao?> GetByIdAsync(Guid id, CancellationToken ct);
    Task<List<Manutencao>> GetAllByVeiculoIdAsync(Guid veiculoId, string? termo, CancellationToken ct);
    Task<List<Manutencao>> GetAllPendentesAsync(CancellationToken ct);
    void Remove(Manutencao manutencao);
}
