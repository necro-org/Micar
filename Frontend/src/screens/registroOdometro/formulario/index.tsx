import { zodResolver } from "@hookform/resolvers/zod";
import { useRoute, type RouteProp } from "@react-navigation/native";
import { useEffect, useMemo, useRef } from "react";
import { Controller, useForm } from "react-hook-form";
import { Pressable, Text, View } from "react-native";
import { z } from "zod";

import { Button } from "@/components/Button";
import { KeyboardAvoidingFormScreen } from "@/components/KeyboardAvoidingFormScreen";
import { TextInput } from "@/components/TextInput";
import { useAppGoTo } from "@/hooks/useAppGoTo";
import type { AppStackParamList } from "@/navigation/types";
import { useVeiculo } from "@/screens/veiculo/queries/useVeiculo";
import { useCreateRegistroOdometro } from "../mutations/useCreateRegistroOdometro";

function createSchema(odometroAnterior: number | null | undefined) {
  return z.object({
    odometro: z
      .string()
      .trim()
      .min(1, "Informe o odômetro.")
      .refine((value) => !Number.isNaN(Number(value)), {
        message: "Informe um número válido.",
      })
      .refine((value) => Number(value) > (odometroAnterior ?? -1), {
        message:
          odometroAnterior != null
            ? `Odômetro deve ser maior que ${odometroAnterior} km.`
            : "Odômetro deve ser maior que zero.",
      }),
  });
}

type RegistroOdometroFormValues = z.infer<ReturnType<typeof createSchema>>;

type RegistroOdometroFormRouteProp = RouteProp<
  AppStackParamList,
  "RegistroOdometroForm"
>;

const INCREMENTOS_RAPIDOS = [1000, 4000, 6000];
const PASSO_AJUSTE_FINO = 10;

export function RegistroOdometroFormScreen() {
  const { goBack, goToHome } = useAppGoTo();
  const route = useRoute<RegistroOdometroFormRouteProp>();
  const { veiculoId } = route.params;

  const { data: veiculo } = useVeiculo(veiculoId);
  const { mutateAsync: createRegistroOdometro, isPending } =
    useCreateRegistroOdometro();

  const schema = useMemo(
    () => createSchema(veiculo?.odometroAtual),
    [veiculo?.odometroAtual],
  );

  const { control, handleSubmit, getValues, setValue, reset } =
    useForm<RegistroOdometroFormValues>({
      resolver: zodResolver(schema),
      defaultValues: { odometro: "" },
    });

  const odometroInicializado = useRef(false);

  useEffect(() => {
    if (veiculo?.odometroAtual != null && !odometroInicializado.current) {
      odometroInicializado.current = true;
      reset({ odometro: String(veiculo.odometroAtual) });
    }
  }, [veiculo?.odometroAtual, reset]);

  function handleAjustar(passo: number) {
    const atual = Number(getValues("odometro")) || 0;
    const proximo = Math.max(0, atual + passo);
    setValue("odometro", String(proximo), {
      shouldValidate: true,
      shouldDirty: true,
    });
  }

  async function onSubmit(values: RegistroOdometroFormValues) {
    await createRegistroOdometro({
      veiculoId,
      odometro: Number(values.odometro),
    });

    goToHome();
  }

  return (
    <KeyboardAvoidingFormScreen edges={["top", "left", "right"]}>
      <View className="mb-6 items-center">
        <Text className="text-3xl font-bold text-brand-900">
          Registrar odômetro
        </Text>
        {veiculo && (
          <Text className="mt-1 text-base text-brand-500">
            {veiculo.apelido} · {veiculo.placa}
          </Text>
        )}
      </View>

      {veiculo?.odometroAtual != null && (
        <View className="mb-4 rounded-2xl bg-white p-4 shadow-sm">
          <Text className="text-sm text-brand-500">
            Último odômetro registrado
          </Text>
          <Text className="text-lg font-semibold text-brand-900">
            {veiculo.odometroAtual} km
          </Text>
        </View>
      )}

      <View className="rounded-2xl bg-white p-6 shadow-sm">
        <Controller
          control={control}
          name="odometro"
          render={({ field, fieldState }) => (
            <TextInput
              label="Odômetro (km)"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              keyboardType="numeric"
              placeholder="Ex: 12345"
              autoFocus
              returnKeyType="done"
              onSubmitEditing={handleSubmit(onSubmit)}
            />
          )}
        />

        <Text className="mb-2 text-sm font-medium text-brand-900">
          Adicionar quilometragem
        </Text>
        <View className="mb-4 flex-row gap-3">
          {INCREMENTOS_RAPIDOS.map((incremento) => (
            <Pressable
              key={incremento}
              onPress={() => handleAjustar(incremento)}
              className="flex-1 items-center rounded-xl bg-accent-500 py-4 active:bg-accent-600"
            >
              <Text className="text-base font-bold text-white">
                +{incremento / 1000}mil
              </Text>
            </Pressable>
          ))}
        </View>

        <Text className="mb-2 text-sm font-medium text-brand-900">
          Ajuste fino
        </Text>
        <View className="mb-4 flex-row items-center justify-center gap-6">
          <Pressable
            onPress={() => handleAjustar(-PASSO_AJUSTE_FINO)}
            className="h-16 w-16 items-center justify-center rounded-full bg-brand-100 active:bg-brand-200"
          >
            <Text className="text-3xl font-bold text-brand-600">−</Text>
          </Pressable>

          <Text className="min-w-[64px] text-center text-sm text-brand-500">
            {PASSO_AJUSTE_FINO} km
          </Text>

          <Pressable
            onPress={() => handleAjustar(PASSO_AJUSTE_FINO)}
            className="h-16 w-16 items-center justify-center rounded-full bg-brand-100 active:bg-brand-200"
          >
            <Text className="text-3xl font-bold text-brand-600">+</Text>
          </Pressable>
        </View>

        <Button
          label="Salvar registro"
          onPress={handleSubmit(onSubmit)}
          loading={isPending}
        />
      </View>

      <Button label="Cancelar" onPress={goBack} variant="ghost" />
    </KeyboardAvoidingFormScreen>
  );
}
