import { useRef } from "react";
import { Text, TextInput as TextInputNative, View } from "react-native";

import { Button } from "@/components/Button";
import { KeyboardAvoidingFormScreen } from "@/components/KeyboardAvoidingFormScreen";
import { TextInput } from "@/components/TextInput";

import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

import { useAuth } from "@/hooks/useAuth";
import { useAuthGoTo } from "@/hooks/useAuthGoTo";
import { useCreateUsuario } from "../mutations/useCreateUsuario";

const PASSWORD_MIN_LENGTH = 6;

const schema = z.object({
  nome: z.string().trim().min(1, "Informe o nome."),
  email: z.email("Informe um email válido"),
  senha: z
    .string()
    .min(
      PASSWORD_MIN_LENGTH,
      `A senha deve ter no mínimo ${PASSWORD_MIN_LENGTH} caracteres.`,
    ),
});

type CadastroFormValues = z.infer<typeof schema>;

export function CadastroScreen() {
  const { control, handleSubmit } = useForm<CadastroFormValues>({
    resolver: zodResolver(schema),
    defaultValues: { nome: "", email: "", senha: "" },
  });
  const { mutateAsync, isPending } = useCreateUsuario();
  const { setToken } = useAuth();
  const { goToLogin } = useAuthGoTo();
  const emailRef = useRef<TextInputNative>(null);
  const senhaRef = useRef<TextInputNative>(null);

  async function onSubmit(values: CadastroFormValues) {
    const response = await mutateAsync(values);
    setToken(response.token);
  }

  return (
    <KeyboardAvoidingFormScreen>
      <View className="mb-10 items-center">
        <Text className="text-4xl font-bold text-brand-900">Micar</Text>
      </View>

      <View className="rounded-2xl bg-white p-6 shadow-sm">
        <Text className="mb-4 text-xl font-bold text-brand-900">
          Criar conta
        </Text>

        <Controller
          control={control}
          name="nome"
          render={({ field, fieldState }) => (
            <TextInput
              label="Nome"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              placeholder="Seu nome"
              returnKeyType="next"
              onSubmitEditing={() => emailRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="email"
          render={({ field, fieldState }) => (
            <TextInput
              ref={emailRef}
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
              placeholder={`Mínimo ${PASSWORD_MIN_LENGTH} caracteres`}
              returnKeyType="done"
              onSubmitEditing={handleSubmit(onSubmit)}
            />
          )}
        />

        <Button
          label="Criar conta"
          onPress={handleSubmit(onSubmit)}
          loading={isPending}
        />
      </View>

      <Button
        label="Já tem uma conta? Entrar"
        onPress={goToLogin}
        variant="ghost"
      />
    </KeyboardAvoidingFormScreen>
  );
}
