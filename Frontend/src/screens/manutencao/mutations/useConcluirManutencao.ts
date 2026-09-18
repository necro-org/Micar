import { useMutation, useQueryClient } from "@tanstack/react-query";

import { concluirManutencao } from "../api/services/concluirManutencao";
import { manutencoesKeys } from "../queries/keys";

export function useConcluirManutencao() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: concluirManutencao,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: manutencoesKeys.all });
    },
  });
}
