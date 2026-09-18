import { useRef } from "react";
import { Text, TextInput as TextInputNative, View } from "react-native";
import { Button } from "@/components/Button";
import { KeyboardAvoidingFormScreen } from "@/components/KeyboardAvoidingFormScreen";
import { TextInput } from "@/components/TextInput";

import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";

import { useAuth } from "@/hooks/useAuth";
import { useAuthGoTo } from "@/hooks/useAuthGoTo";
import { useLoginUsuario } from "../mutations/useLoginUsuario";

const schema = z.object({
  email: z.email("Informe um email válido."),
  senha: z.string().min(1, "Informe a senha."),
});

type LoginFormValues = z.infer<typeof schema>;

export function LoginScreen() {
  const { control, handleSubmit } = useForm<LoginFormValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: "", senha: "" },
  });
  const { mutateAsync, isPending } = useLoginUsuario();
  const { setToken } = useAuth();
  const { goToCadastro } = useAuthGoTo();
  const senhaRef = useRef<TextInputNative>(null);

  async function onSubmit(values: LoginFormValues) {
    const response = await mutateAsync(values);
    setToken(response.token);
  }

  return (
    <KeyboardAvoidingFormScreen>
      <View className="mb-10 items-center">
        <Text className="text-4xl font-bold text-brand-900">Micar</Text>
      </View>

      <View className="rounded-2xl bg-white p-6 shadow-sm">
        <Text className="mb-4 text-xl font-bold text-brand-900">Entrar</Text>

        <Controller
          control={control}
          name="email"
          render={({ field, fieldState }) => (
            <TextInput
              label="Email"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              autoCapitalize="none"
              keyboardType="email-address"
              placeholder="email@email.com"
              returnKeyType="next"
              onSubmitEditing={() => senhaRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="senha"
          render={({ field, fieldState }) => (
            <TextInput
              ref={senhaRef}
              label="Senha"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              isPassword
              placeholder="Sua senha"
              returnKeyType="done"
              onSubmitEditing={handleSubmit(onSubmit)}
            />
          )}
        />

        <Button
          label="Entrar"
          onPress={handleSubmit(onSubmit)}
          loading={isPending}
        />
      </View>

      <Button
        label="Não tem uma conta? Cadastre-se"
        onPress={goToCadastro}
        variant="ghost"
      />
    </KeyboardAvoidingFormScreen>
  );
}
