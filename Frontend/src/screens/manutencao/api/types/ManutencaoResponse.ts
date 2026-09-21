import type { NivelAlertaEnum } from "@/screens/common/api/types/NivelAlertaEnum";

export interface ManutencaoResponse {
  id: string;
  data: string;
  nome: string;
  veiculoId: string;
  registroOdometroId: string | null;
  odometro: number | null;
  odometroVencimento: number | null;
  dataVencimento: string | null;
  valor: number | null;
  dataConclusao: string | null;
  diasRestantes: number | null;
  kmRestantes: number | null;
  descricao: string | null;
  status: NivelAlertaEnum;
  notificacaoVisualizada: boolean;
}
