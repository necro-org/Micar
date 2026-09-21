export interface UpdateManutencaoRequest {
  nome: string;
  data: string;
  odometro: number;
  odometroVencimento?: number;
  dataVencimento?: string;
  valor?: number;
  dataConclusao?: string;
  descricao?: string;
  notificacaoVisualizada: boolean;
}
