import { Alert } from "react-native";

import { getApiErrorMessage, isSessionExpiredError } from "./api";

export function fireError(error: unknown) {
  if (isSessionExpiredError(error)) {
    return;
  }

  Alert.alert("Houston, temos um problema", getApiErrorMessage(error));
}

interface ConfirmDeleteParams {
  title: string;
  message: string;
  onConfirm: () => void;
}

export function confirmDelete({ title, message, onConfirm }: ConfirmDeleteParams) {
  Alert.alert(title, message, [
    { text: "Cancelar", style: "cancel" },
    { text: "Excluir", style: "destructive", onPress: onConfirm },
  ]);
}

interface ConfirmActionParams {
  title: string;
  message: string;
  confirmText: string;
  onConfirm: () => void;
}

export function confirmAction({
  title,
  message,
  confirmText,
  onConfirm,
}: ConfirmActionParams) {
  Alert.alert(title, message, [
    { text: "Cancelar", style: "cancel" },
    { text: confirmText, onPress: onConfirm },
  ]);
}
