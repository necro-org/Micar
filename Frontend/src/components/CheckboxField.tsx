import { Ionicons } from "@expo/vector-icons";
import { Pressable, Text } from "react-native";

type CheckboxFieldProps = {
  label: string;
  value: boolean;
  onChange: (value: boolean) => void;
};

export function CheckboxField({ label, value, onChange }: CheckboxFieldProps) {
  return (
    <Pressable
      onPress={() => onChange(!value)}
      className="mb-4 flex-row items-start gap-2"
    >
      <Ionicons
        name={value ? "checkmark-circle" : "ellipse-outline"}
        size={22}
        color={value ? "#1FA35A" : "#82B4CC"}
      />
      <Text className="flex-1 text-sm font-medium text-brand-900">
        {label}
      </Text>
    </Pressable>
  );
}
