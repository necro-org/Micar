using Contracts.Repositories.Manutencoes;
using Domain.Manutencoes;
using Infrastructure.Database.Context;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Database.Repositories.Manutencoes;

public class ManutencaoRepository : IManutencaoRepository
{
    private readonly MicarDbContext _dbContext;

    public ManutencaoRepository(MicarDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<Manutencao?> GetByIdAsync(Guid id, CancellationToken ct)
    {
        return await _dbContext.Manutencoes
            .Include(m => m.Veiculo)
                .ThenInclude(v => v!.RegistrosOdometro.OrderByDescending(r => r.Data).ThenByDescending(r => r.Odometro).Take(1))
            .Include(m => m.RegistroOdometro)
            .FirstOrDefaultAsync(m => m.Id == id, ct);
    }

    public async Task<List<Manutencao>> GetAllByVeiculoIdAsync(Guid veiculoId, string? termo, CancellationToken ct)
    {
        var query = _dbContext.Manutencoes
            .Include(m => m.RegistroOdometro)
            .Where(m => m.VeiculoId == veiculoId);

        if (!string.IsNullOrWhiteSpace(termo))
            query = query.Where(m => EF.Functions.ILike(m.Nome, $"%{termo}%"));

        return await query
            .OrderByDescending(x => x.Data)
            .ToListAsync(ct);
    }

    public async Task<List<Manutencao>> GetAllPendentesAsync(CancellationToken ct)
    {
        return await _dbContext.Manutencoes
            .Include(m => m.Veiculo)
                .ThenInclude(v => v!.RegistrosOdometro.OrderByDescending(r => r.Data).ThenByDescending(r => r.Odometro).Take(1))
            .Include(m => m.RegistroOdometro)
            .Where(m => m.DataConclusao == null)
            .ToListAsync(ct);
    }

    public void Remove(Manutencao manutencao)
    {
        _dbContext.Manutencoes.Remove(manutencao);
    }
}
