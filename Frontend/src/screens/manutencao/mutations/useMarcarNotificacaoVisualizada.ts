import { useMutation, useQueryClient } from "@tanstack/react-query";

import { marcarNotificacaoVisualizada } from "../api/services/marcarNotificacaoVisualizada";
import { manutencoesKeys } from "../queries/keys";

export function useMarcarNotificacaoVisualizada() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: marcarNotificacaoVisualizada,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: manutencoesKeys.all });
    },
  });
}
