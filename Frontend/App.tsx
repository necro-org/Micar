import { KeyboardProvider } from "react-native-keyboard-controller";
import { SafeAreaProvider } from "react-native-safe-area-context";

import { RootNavigator } from "./src/navigation/RootNavigator";
import { AuthProvider } from "./src/providers/AuthProvider";
import { QueryClientProvider } from "./src/providers/QueryClientProvider";
import { SelectedVeiculoProvider } from "./src/providers/SelectedVeiculoProvider";

export function App() {
  return (
    <KeyboardProvider>
      <SafeAreaProvider>
        <QueryClientProvider>
          <AuthProvider>
            <SelectedVeiculoProvider>
              <RootNavigator />
            </SelectedVeiculoProvider>
          </AuthProvider>
        </QueryClientProvider>
      </SafeAreaProvider>
    </KeyboardProvider>
  );
}
