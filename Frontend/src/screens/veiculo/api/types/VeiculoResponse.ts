import type { TipoVeiculoEnum } from "./TipoVeiculoEnum";

export interface VeiculoResponse {
  id: string;
  placa: string;
  apelido: string;
  tipoVeiculo: TipoVeiculoEnum;
  odometroAtual: number | null;
  diasNotificacaoOdometro: number;
  descricao: string | null;
}
