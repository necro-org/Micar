import { requireOptionalNativeModule } from "expo";

type MicarCarModule = {
  setSession(token: string, apiUrl: string): void;
  clearSession(): void;
};

const MicarCar = requireOptionalNativeModule<MicarCarModule>("MicarCar");

export function syncCarSession(token: string | null) {
  const apiUrl = process.env.EXPO_PUBLIC_API_URL;

  if (token && apiUrl) {
    MicarCar?.setSession(token, apiUrl);
  } else {
    MicarCar?.clearSession();
  }
}
