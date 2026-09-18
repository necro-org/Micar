import { cssInterop } from "nativewind";
import type { ReactNode } from "react";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { SafeAreaView, type Edge } from "react-native-safe-area-context";

cssInterop(KeyboardAwareScrollView, {
  className: "style",
  contentContainerClassName: "contentContainerStyle",
});

export type KeyboardAvoidingFormScreenProps = {
  children: ReactNode;
  edges?: Edge[];
  contentContainerClassName?: string;
};

export function KeyboardAvoidingFormScreen({
  children,
  edges,
  contentContainerClassName = "flex-grow justify-center px-6 py-8",
}: KeyboardAvoidingFormScreenProps) {
  return (
    <SafeAreaView edges={edges} className="flex-1 bg-brand-50">
      <KeyboardAwareScrollView
        className="flex-1"
        contentContainerClassName={contentContainerClassName}
        bottomOffset={24}
        keyboardShouldPersistTaps="handled"
      >
        {children}
      </KeyboardAwareScrollView>
    </SafeAreaView>
  );
}
