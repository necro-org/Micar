import { api } from "@/libs/api";

export async function marcarNotificacaoVisualizada(id: string) {
  await api.patch(`/Manutencoes/${id}/notificacao-visualizada`);
}
