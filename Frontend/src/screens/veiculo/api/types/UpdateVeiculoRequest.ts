import type { TipoVeiculoEnum } from "./TipoVeiculoEnum";

export interface UpdateVeiculoRequest {
  placa: string;
  apelido: string;
  tipoVeiculo: TipoVeiculoEnum;
  odometro?: number;
  diasNotificacaoOdometro: number;
  descricao?: string;
}
