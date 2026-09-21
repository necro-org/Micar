import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { ActivityIndicator } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

import { useNotificationResponseHandler } from "@/features/pushNotifications/hooks/useNotificationResponseHandler";
import { usePushNotificationRegistration } from "@/features/pushNotifications/hooks/usePushNotificationRegistration";
import { useSelectedVeiculo } from "@/hooks/useSelectedVeiculo";
import { HomeScreen } from "@/screens/home";
import { ManutencaoFormScreen } from "@/screens/manutencao/formulario";
import { ManutencaoListScreen } from "@/screens/manutencao/listagem";
import { RegistroOdometroFormScreen } from "@/screens/registroOdometro/formulario";
import { PerfilScreen } from "@/screens/usuario/perfil";
import { VeiculoFormScreen } from "@/screens/veiculo/formulario";
import { VeiculoListScreen } from "@/screens/veiculo/listagem";
import type { AppStackParamList } from "./types";

const Stack = createNativeStackNavigator<AppStackParamList>();

export function AppNavigator() {
  const { selectedVeiculoId, isLoading } = useSelectedVeiculo();

  usePushNotificationRegistration();
  useNotificationResponseHandler();

  if (isLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-brand-50">
        <ActivityIndicator color="#2E6E8E" />
      </SafeAreaView>
    );
  }

  return (
    <Stack.Navigator
      screenOptions={{ headerShown: false }}
      initialRouteName={selectedVeiculoId ? "Home" : "VeiculoList"}
    >
      <Stack.Screen name="VeiculoList" component={VeiculoListScreen} />
      <Stack.Screen name="VeiculoForm" component={VeiculoFormScreen} />
      <Stack.Screen name="Home" component={HomeScreen} />
      <Stack.Screen name="Perfil" component={PerfilScreen} />
      <Stack.Screen
        name="RegistroOdometroForm"
        component={RegistroOdometroFormScreen}
      />
      <Stack.Screen name="ManutencaoList" component={ManutencaoListScreen} />
      <Stack.Screen name="ManutencaoForm" component={ManutencaoFormScreen} />
    </Stack.Navigator>
  );
}
