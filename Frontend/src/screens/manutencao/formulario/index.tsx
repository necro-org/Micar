import { zodResolver } from "@hookform/resolvers/zod";
import { useRoute, type RouteProp } from "@react-navigation/native";
import { useEffect, useRef } from "react";
import { Controller, useForm } from "react-hook-form";
import {
  ActivityIndicator,
  Text,
  TextInput as TextInputNative,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { z } from "zod";

import { Button } from "@/components/Button";
import { CheckboxField } from "@/components/CheckboxField";
import { DateField } from "@/components/DateField";
import { KeyboardAvoidingFormScreen } from "@/components/KeyboardAvoidingFormScreen";
import { TextInput } from "@/components/TextInput";
import { useAppGoTo } from "@/hooks/useAppGoTo";
import { toDateInput, todayDateInput, zDateBR } from "@/libs/date";
import type { AppStackParamList } from "@/navigation/types";
import { NivelAlertaEnum } from "@/screens/common/api/types/NivelAlertaEnum";
import { useVeiculo } from "@/screens/veiculo/queries/useVeiculo";
import { useCreateManutencao } from "../mutations/useCreateManutencao";
import { useUpdateManutencao } from "../mutations/useUpdateManutencao";
import { useManutencao } from "../queries/useManutencao";

const schema = z.object({
  nome: z.string().trim().min(1, "Informe o nome."),
  data: zDateBR(),
  odometro: z
    .string()
    .trim()
    .min(1, "Informe o odômetro.")
    .refine(
      (value) => !Number.isNaN(Number(value)),
      "Informe um número válido.",
    ),
  odometroVencimento: z
    .string()
    .trim()
    .refine(
      (value) => !value || !Number.isNaN(Number(value)),
      "Informe um número válido.",
    )
    .optional(),
  dataVencimento: zDateBR({ required: false }),
  valor: z
    .string()
    .trim()
    .refine(
      (value) => !value || (!Number.isNaN(Number(value)) && Number(value) > 0),
      "Informe um valor válido.",
    )
    .optional(),
  dataConclusao: zDateBR({ required: false }),
  descricao: z.string().trim().optional(),
  notificacaoVisualizada: z.boolean().default(false),
});

type ManutencaoFormInput = z.input<typeof schema>;
type ManutencaoFormValues = z.output<typeof schema>;

type ManutencaoFormRouteProp = RouteProp<AppStackParamList, "ManutencaoForm">;

export function ManutencaoFormScreen() {
  const { goBack, goToManutencaoList } = useAppGoTo();
  const route = useRoute<ManutencaoFormRouteProp>();
  const { veiculoId, id } = route.params;
  const isEditing = !!id;

  const { data: veiculo } = useVeiculo(veiculoId);
  const { data: manutencao, isLoading } = useManutencao(id);
  const { mutateAsync: createManutencao, isPending: isCreating } =
    useCreateManutencao();
  const { mutateAsync: updateManutencao, isPending: isUpdating } =
    useUpdateManutencao();

  const { control, handleSubmit, reset, setValue } =
    useForm<ManutencaoFormInput, unknown, ManutencaoFormValues>({
      resolver: zodResolver(schema),
      defaultValues: {
        nome: "",
        data: todayDateInput(),
        odometro: "",
        odometroVencimento: "",
        dataVencimento: "",
        valor: "",
        dataConclusao: "",
        descricao: "",
        notificacaoVisualizada: false,
      },
    });

  const dataRef = useRef<TextInputNative>(null);
  const odometroRef = useRef<TextInputNative>(null);
  const odometroVencimentoRef = useRef<TextInputNative>(null);
  const dataVencimentoRef = useRef<TextInputNative>(null);
  const valorRef = useRef<TextInputNative>(null);
  const descricaoRef = useRef<TextInputNative>(null);
  const dataConclusaoRef = useRef<TextInputNative>(null);
  const odometroInicializado = useRef(false);

  useEffect(() => {
    if (manutencao) {
      reset({
        nome: manutencao.nome,
        data: toDateInput(manutencao.data),
        odometro:
          manutencao.odometro != null ? String(manutencao.odometro) : "",
        odometroVencimento:
          manutencao.odometroVencimento != null
            ? String(manutencao.odometroVencimento)
            : "",
        dataVencimento: manutencao.dataVencimento
          ? toDateInput(manutencao.dataVencimento)
          : "",
        valor: manutencao.valor != null ? String(manutencao.valor) : "",
        dataConclusao: manutencao.dataConclusao
          ? toDateInput(manutencao.dataConclusao)
          : "",
        descricao: manutencao.descricao ?? "",
        notificacaoVisualizada: manutencao.notificacaoVisualizada,
      });
    }
  }, [manutencao, reset]);

  useEffect(() => {
    if (
      !isEditing &&
      veiculo?.odometroAtual != null &&
      !odometroInicializado.current
    ) {
      odometroInicializado.current = true;
      setValue("odometro", String(veiculo.odometroAtual));
    }
  }, [isEditing, veiculo?.odometroAtual, setValue]);

  async function onSubmit(values: ManutencaoFormValues) {
    if (isEditing) {
      await updateManutencao({
        id,
        data: {
          nome: values.nome,
          data: values.data,
          odometro: Number(values.odometro),
          odometroVencimento: values.odometroVencimento
            ? Number(values.odometroVencimento)
            : undefined,
          dataVencimento: values.dataVencimento,
          valor: values.valor ? Number(values.valor) : undefined,
          dataConclusao: values.dataConclusao,
          descricao: values.descricao || undefined,
          notificacaoVisualizada: values.notificacaoVisualizada,
        },
      });
    } else {
      await createManutencao({
        nome: values.nome,
        data: values.data,
        veiculoId,
        odometro: Number(values.odometro),
        odometroVencimento: values.odometroVencimento
          ? Number(values.odometroVencimento)
          : undefined,
        dataVencimento: values.dataVencimento,
        valor: values.valor ? Number(values.valor) : undefined,
        descricao: values.descricao || undefined,
      });
    }

    goToManutencaoList(veiculoId);
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
          {isEditing ? "Editar manutenção" : "Nova manutenção"}
        </Text>
        {veiculo && (
          <Text className="mt-1 text-base text-brand-500">
            {veiculo.apelido} · {veiculo.placa}
          </Text>
        )}
      </View>

      <View className="rounded-2xl bg-white p-6 shadow-sm">
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
              placeholder="Ex: Troca de óleo"
              autoFocus
              returnKeyType="next"
              onSubmitEditing={() => dataRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="data"
          render={({ field, fieldState }) => (
            <DateField
              ref={dataRef}
              label="Data"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              returnKeyType="next"
              onSubmitEditing={() => odometroRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="odometro"
          render={({ field, fieldState }) => (
            <TextInput
              ref={odometroRef}
              label="Odômetro (km)"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              keyboardType="numeric"
              placeholder="Ex: 12345"
              returnKeyType="next"
              onSubmitEditing={() => odometroVencimentoRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="odometroVencimento"
          render={({ field, fieldState }) => (
            <TextInput
              ref={odometroVencimentoRef}
              label="Odômetro do próximo vencimento"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              keyboardType="numeric"
              placeholder="Opcional"
              returnKeyType="next"
              onSubmitEditing={() => dataVencimentoRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="dataVencimento"
          render={({ field, fieldState }) => (
            <DateField
              ref={dataVencimentoRef}
              label="Data do próximo vencimento"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              placeholder="Opcional (DD/MM/AAAA)"
              returnKeyType="next"
              onSubmitEditing={() => valorRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="valor"
          render={({ field, fieldState }) => (
            <TextInput
              ref={valorRef}
              label="Valor (R$)"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              keyboardType="numeric"
              placeholder="Opcional"
              returnKeyType="next"
              onSubmitEditing={() => descricaoRef.current?.focus()}
            />
          )}
        />

        <Controller
          control={control}
          name="descricao"
          render={({ field, fieldState }) => (
            <TextInput
              ref={descricaoRef}
              label="Descrição"
              value={field.value}
              onChangeText={field.onChange}
              onBlur={field.onBlur}
              error={fieldState.error?.message}
              placeholder="Opcional · detalhes sobre a manutenção"
              multiline
              numberOfLines={4}
            />
          )}
        />

        {isEditing && (
          <Controller
            control={control}
            name="dataConclusao"
            render={({ field, fieldState }) => (
              <DateField
                ref={dataConclusaoRef}
                label="Data de conclusão"
                value={field.value}
                onChangeText={field.onChange}
                onBlur={field.onBlur}
                error={fieldState.error?.message}
                placeholder="Opcional (DD/MM/AAAA) · preencha para concluir"
                returnKeyType="done"
                onSubmitEditing={handleSubmit(onSubmit)}
              />
            )}
          />
        )}

        {isEditing && manutencao && manutencao.status !== NivelAlertaEnum.Normal && (
          <Controller
            control={control}
            name="notificacaoVisualizada"
            render={({ field }) => (
              <CheckboxField
                label={
                  manutencao.status === NivelAlertaEnum.Critico
                    ? "Notificação de manutenção vencida visualizada"
                    : "Notificação de manutenção próxima do vencimento visualizada"
                }
                value={field.value ?? false}
                onChange={field.onChange}
              />
            )}
          />
        )}

        <Button
          label={isEditing ? "Salvar alterações" : "Cadastrar manutenção"}
          onPress={handleSubmit(onSubmit)}
          loading={isCreating || isUpdating}
        />
      </View>

      <Button label="Cancelar" onPress={goBack} variant="ghost" />
    </KeyboardAvoidingFormScreen>
  );
}
