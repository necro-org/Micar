import { zodResolver } from "@hookform/resolvers/zod";
import { useRoute, type RouteProp } from "@react-navigation/native";
import clsx from "clsx";
import { useEffect, useRef } from "react";
import { Controller, useForm } from "react-hook-form";
import {
  ActivityIndicator,
  Pressable,
  Text,
  TextInput as TextInputNative,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { z } from "zod";

import { Button } from "@/components/Button";
import { KeyboardAvoidingFormScreen } from "@/components/KeyboardAvoidingFormScreen";
import { TextInput } from "@/components/TextInput";
import { useAppGoTo } from "@/hooks/useAppGoTo";
import type { AppStackParamList } from "@/navigation/types";
import { TipoVeiculoEnum } from "../api/types/TipoVeiculoEnum";
import { useCreateVeiculo } from "../mutations/useCreateVeiculo";
import { useUpdateVeiculo } from "../mutations/useUpdateVeiculo";
import { useVeiculo } from "../queries/useVeiculo";

const PLACA_LENGTH = 7;
const TIPOS_VEICULO = Object.values(TipoVeiculoEnum);
const DIAS_NOTIFICACAO_ODOMETRO_PADRAO = 30;

const schema = z.object({
  placa: z
    .string()
    .trim()
    .length(
      PLACA_LENGTH,
      `Placa deve ter exatamente ${PLACA_LENGTH} caracteres.`,
    ),
  apelido: z.string().trim().min(1, "Informe o apelido."),
  tipoVeiculo: z.enum(TipoVeiculoEnum),
  odometro: z.string().optional(),
  diasNotificacaoOdometro: z
    .string()
    .trim()
    .min(1, "Informe os dias para notificação."),
});

type VeiculoFormValues = z.infer<typeof schema>;

type VeiculoFormRouteProp = RouteProp<AppStackParamList, "VeiculoForm">;

export function VeiculoFormScreen() {
  const { goBack, goToVeiculoList } = useAppGoTo();
  const route = useRoute<VeiculoFormRouteProp>();
  const id = route.params?.id;
  const isEditing = !!id;

  const { data: veiculo, isLoading } = useVeiculo(id);
  const { mutateAsync: createVeiculo, isPending: isCreating } =
    useCreateVeiculo();
  const { mutateAsync: updateVeiculo, isPending: isUpdating } =
    useUpdateVeiculo();

  const { control, handleSubmit, reset, watch, setValue } =
    useForm<VeiculoFormValues>({
      resolver: zodResolver(schema),
      defaultValues: {
        placa: "",
        apelido: "",
        tipoVeiculo: TipoVeiculoEnum.Carro,
        odometro: undefined,
        diasNotificacaoOdometro: String(DIAS_NOTIFICACAO_ODOMETRO_PADRAO),
      },
    });

  const apelidoRef = useRef<TextInputNative>(null);
  const odometroRef = useRef<TextInputNative>(null);
  const diasNotificacaoOdometroRef = useRef<TextInputNative>(null);
  const tipoVeiculo = watch("tipoVeiculo");

  useEffect(() => {
    if (veiculo) {
      reset({
        placa: veiculo.placa,
        apelido: veiculo.apelido,
        tipoVeiculo: veiculo.tipoVeiculo,
        odometro: veiculo.odometroAtual
          ? String(veiculo.odometroAtual)
          : undefined,
        diasNotificacaoOdometro: String(veiculo.diasNotificacaoOdometro),
      });
    }
  }, [veiculo, reset]);

  async function onSubmit(values: VeiculoFormValues) {
    const payload = {
      placa: values.placa.toUpperCase(),
      apelido: values.apelido,
      tipoVeiculo: values.tipoVeiculo,
      odometro: values.odometro ? Number(values.odometro) : undefined,
      diasNotificacaoOdometro: Number(values.diasNotificacaoOdometro),
    };

    if (isEditing) {
      await updateVeiculo({ id, data: payload });
    } else {
      await createVeiculo(payload);
    }

    goToVeiculoList();
  }

  if (isEditing && isLoading) {
    return (
      <SafeAreaView
        edges={["top", "left", "right"]}
        className="flex-1 items-center justify-center bg-brand-50"
      >
        <ActivityIndicator color="#2E6E8E" />
      </SafeAreaView>
    );
  }

  return (
    <KeyboardAvoidingFormScreen edges={["top", "left", "right"]}>
      <View className="mb-6 items-center">
        <Text className="text-3xl font-bold text-brand-900">
          {isEditing ? "Editar veículo" : "Novo veículo"}
        </Text>
      </View>

      <View className="rounded-2xl bg-white p-6 shadow-sm">
        <Controller
          control={control}
          name="placa"
          render={({ field, fieldState }) => (
            <TextInput
              label="Placa"
              value={field.value}
              onChangeText={(value) => field.onChange(value.toUpperCase())}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              autoCapitalize="characters"
              maxLength={PLACA_LENGTH}
              placeholder="ABC1D23"
              returnKeyType="next"
              onSubmitEditing={() => apelidoRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="apelido"
          render={({ field, fieldState }) => (
            <TextInput
              ref={apelidoRef}
              label="Apelido"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              placeholder="Ex: Carro do trabalho"
              returnKeyType="next"
              onSubmitEditing={() => odometroRef.current?.focus()}
            />
          )}
        />

        <View className="mb-4">
          <Text className="mb-1 text-sm font-medium text-brand-900">
            Tipo
          </Text>
          <View className="flex-row gap-3">
            {TIPOS_VEICULO.map((option) => (
              <Pressable
                key={option}
                onPress={() => setValue("tipoVeiculo", option)}
                className={clsx("flex-1 items-center rounded-xl border py-3", {
                  "border-accent-500 bg-accent-50": tipoVeiculo === option,
                  "border-brand-200 bg-white": tipoVeiculo !== option,
                })}
              >
                <Text
                  className={clsx("font-semibold", {
                    "text-accent-600": tipoVeiculo === option,
                    "text-brand-500": tipoVeiculo !== option,
                  })}
                >
                  {option}
                </Text>
              </Pressable>
            ))}
          </View>
        </View>

        <Controller
          control={control}
          name="odometro"
          render={({ field, fieldState }) => (
            <TextInput
              ref={odometroRef}
              label="Odômetro (km)"
              value={field.value ?? ""}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              keyboardType="numeric"
              placeholder="Opcional"
              returnKeyType="next"
              onSubmitEditing={() => diasNotificacaoOdometroRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="diasNotificacaoOdometro"
          render={({ field, fieldState }) => (
            <TextInput
              ref={diasNotificacaoOdometroRef}
              label="Alertar odômetro desatualizado após (dias)"
              value={field.value ?? ""}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              keyboardType="numeric"
              placeholder="30"
              returnKeyType="done"
              onSubmitEditing={handleSubmit(onSubmit)}
            />
          )}
        />

        <Button
          label={isEditing ? "Salvar alterações" : "Cadastrar veículo"}
          onPress={handleSubmit(onSubmit)}
          loading={isCreating || isUpdating}
        />
      </View>

      <Button label="Cancelar" onPress={goBack} variant="ghost" />
    </KeyboardAvoidingFormScreen>
  );
}
