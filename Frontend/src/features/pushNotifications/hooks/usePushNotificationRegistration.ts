import Constants from "expo-constants";
import * as Device from "expo-device";
import * as Notifications from "expo-notifications";
import { useEffect } from "react";
import { Platform } from "react-native";

import { useRegistrarPushToken } from "@/features/pushNotifications/mutations/useRegistrarPushToken";

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowBanner: true,
    shouldShowList: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

export function usePushNotificationRegistration() {
  const { mutate: registrarPushToken } = useRegistrarPushToken();

  useEffect(() => {
    registrarParaNotificacoes();

    async function registrarParaNotificacoes() {
      try {
        if (!Device.isDevice) {
          console.log("[push] dispositivo não é físico, abortando");
          return;
        }

        if (Platform.OS === "android") {
          await Notifications.setNotificationChannelAsync("default", {
            name: "default",
            importance: Notifications.AndroidImportance.DEFAULT,
          });
        }

        const { status: statusAtual } =
          await Notifications.getPermissionsAsync();

        let status = statusAtual;

        if (status !== "granted") {
          const { status: novoStatus } =
            await Notifications.requestPermissionsAsync();
          status = novoStatus;
        }

        if (status !== "granted") {
          console.log("[push] permissão não concedida:", status);
          return;
        }

        const projectId = Constants.expoConfig?.extra?.eas?.projectId;

        console.log(
          "[push] solicitando expo push token, projectId:",
          projectId,
        );

        const { data: token } = await Notifications.getExpoPushTokenAsync({
          projectId,
        });

        console.log("[push] token obtido:", token);

        await registrarPushToken(
          {
            token,
            plataforma: Platform.OS === "ios" ? "Ios" : "Android",
          },
          {
            onSuccess: () => console.log("[push] token registrado no backend"),
            onError: (err) =>
              console.error("[push] falha ao registrar token no backend:", err),
          },
        );
      } catch (err) {
        console.error("[push] erro ao registrar para notificações:", err);
      }
    }
  }, [registrarPushToken]);
}
