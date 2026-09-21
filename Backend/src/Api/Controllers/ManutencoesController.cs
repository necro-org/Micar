using Api.Filters;
using Application.Manutencoes.Common;
using Application.Manutencoes.Concluir;
using Application.Manutencoes.Create;
using Application.Manutencoes.Delete;
using Application.Manutencoes.GetAll;
using Application.Manutencoes.GetById;
using Application.Manutencoes.GetStatus;
using Application.Manutencoes.MarcarNotificacaoVisualizada;
using Application.Manutencoes.NotificarPendentes;
using Application.Manutencoes.Update;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers;

[ApiController]
[Route("[controller]")]
public class ManutencoesController : ControllerBase
{
    [HttpPost]
    public async Task<IActionResult> Create(
        CreateManutencaoService createManutencaoService,
        CreateManutencaoRequest request,
        CancellationToken ct)
    {
        await createManutencaoService.ExecuteAsync(request, ct);
        return NoContent();
    }

    [HttpGet("veiculo/{veiculoId:guid}")]
    public async Task<ActionResult<List<ManutencaoResponse>>> GetAll(
        GetAllManutencoesService getAllManutencoesService,
        Guid veiculoId,
        [FromQuery] string? termo,
        CancellationToken ct)
    {
        var manutencoes = await getAllManutencoesService.ExecuteAsync(veiculoId, termo, ct);
        return Ok(manutencoes);
    }

    [HttpGet("veiculo/{veiculoId:guid}/status")]
    public async Task<ActionResult<VeiculoStatusManutencaoResponse>> GetStatus(
        GetStatusManutencoesVeiculoService getStatusManutencoesVeiculoService,
        Guid veiculoId,
        CancellationToken ct)
    {
        var status = await getStatusManutencoesVeiculoService.ExecuteAsync(veiculoId, ct);
        return Ok(status);
    }

    [HttpGet("{id:guid}")]
    public async Task<ActionResult<ManutencaoResponse>> GetById(
        GetManutencaoByIdService getManutencaoByIdService,
        Guid id,
        CancellationToken ct)
    {
        var manutencao = await getManutencaoByIdService.ExecuteAsync(id, ct);
        return Ok(manutencao);
    }

    [HttpPut("{id:guid}")]
    public async Task<IActionResult> Update(
        UpdateManutencaoService updateManutencaoService,
        Guid id,
        UpdateManutencaoRequest request,
        CancellationToken ct)
    {
        await updateManutencaoService.ExecuteAsync(id, request, ct);
        return NoContent();
    }

    [HttpPatch("{id:guid}/concluir")]
    public async Task<IActionResult> Concluir(
        ConcluirManutencaoService concluirManutencaoService,
        Guid id,
        CancellationToken ct)
    {
        await concluirManutencaoService.ExecuteAsync(id, ct);
        return NoContent();
    }

    [HttpPatch("{id:guid}/notificacao-visualizada")]
    public async Task<IActionResult> MarcarNotificacaoVisualizada(
        MarcarNotificacaoVisualizadaService marcarNotificacaoVisualizadaService,
        Guid id,
        CancellationToken ct)
    {
        await marcarNotificacaoVisualizadaService.ExecuteAsync(id, ct);
        return NoContent();
    }

    [HttpDelete("{id:guid}")]
    public async Task<IActionResult> Delete(
        DeleteManutencaoService deleteManutencaoService,
        Guid id,
        CancellationToken ct)
    {
        await deleteManutencaoService.ExecuteAsync(id, ct);
        return NoContent();
    }

    [HttpPost("notificar-pendentes")]
    [AllowAnonymous]
    [ServiceFilter(typeof(SchedulerApiKeyFilter))]
    public async Task<ActionResult<NotificarManutencoesPendentesResponse>> NotificarPendentes(
        NotificarManutencoesPendentesService notificarManutencoesPendentesService,
        CancellationToken ct)
    {
        var resultado = await notificarManutencoesPendentesService.ExecuteAsync(ct);
        return Ok(resultado);
    }
}
