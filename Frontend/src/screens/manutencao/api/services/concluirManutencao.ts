import { api } from "@/libs/api";

export async function concluirManutencao(id: string) {
  await api.patch(`/Manutencoes/${id}/concluir`);
}
