import * as Notifications from "expo-notifications";
import { useEffect, useRef } from "react";

import { useAppGoTo } from "@/hooks/useAppGoTo";
import { useSelectedVeiculo } from "@/hooks/useSelectedVeiculo";
import { useMarcarNotificacaoVisualizada } from "@/screens/manutencao/mutations/useMarcarNotificacaoVisualizada";

interface PushNotificationData {
  tipo?: string;
  veiculoId?: string;
  manutencaoId?: string;
}

export function useNotificationResponseHandler() {
  const response = Notifications.useLastNotificationResponse();
  const { goToHome, goToRegistroOdometroForm } = useAppGoTo();
  const { setSelectedVeiculoId } = useSelectedVeiculo();
  const { mutate: marcarNotificacaoVisualizada } =
    useMarcarNotificacaoVisualizada();
  const processedIdRef = useRef<string | null>(null);

  useEffect(() => {
    if (!response) return;

    const notificationId = response.notification.request.identifier;

    if (processedIdRef.current === notificationId) return;
    processedIdRef.current = notificationId;

    Notifications.clearLastNotificationResponseAsync();

    const data = response.notification.request.content
      .data as PushNotificationData;

    if (data?.tipo === "manutencao" && data.manutencaoId) {
      marcarNotificacaoVisualizada(data.manutencaoId);

      if (data.veiculoId) {
        setSelectedVeiculoId(data.veiculoId);
      }

      goToHome();
      return;
    }

    if (data?.tipo === "odometro" && data.veiculoId) {
      setSelectedVeiculoId(data.veiculoId);
      goToRegistroOdometroForm(data.veiculoId);
    }
  }, [
    response,
    goToHome,
    goToRegistroOdometroForm,
    setSelectedVeiculoId,
    marcarNotificacaoVisualizada,
  ]);
}
