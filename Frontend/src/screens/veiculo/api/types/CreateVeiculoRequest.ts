import type { TipoVeiculoEnum } from "./TipoVeiculoEnum";

export interface CreateVeiculoRequest {
  placa: string;
  apelido: string;
  tipoVeiculo: TipoVeiculoEnum;
  odometro?: number;
  diasNotificacaoOdometro: number;
  descricao?: string;
}
