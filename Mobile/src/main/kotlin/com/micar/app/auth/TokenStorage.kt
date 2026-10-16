package com.micar.app.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.sessaoDataStore by preferencesDataStore(name = "sessao")

class TokenStorage(private val context: Context) {

    suspend fun ler(): String? =
        context.sessaoDataStore.data.first()[CHAVE_TOKEN]?.let(::descriptografar)

    suspend fun salvar(token: String) {
        context.sessaoDataStore.edit { it[CHAVE_TOKEN] = criptografar(token) }
    }

    suspend fun remover() {
        context.sessaoDataStore.edit { it.remove(CHAVE_TOKEN) }
    }

    private fun criptografar(texto: String): String {
        val cipher = Cipher.getInstance(TRANSFORMACAO)
        cipher.init(Cipher.ENCRYPT_MODE, obterChave())
        val dados = cipher.iv + cipher.doFinal(texto.toByteArray())
        return Base64.encodeToString(dados, Base64.NO_WRAP)
    }

    private fun descriptografar(valor: String): String? = runCatching {
        val dados = Base64.decode(valor, Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMACAO)
        cipher.init(Cipher.DECRYPT_MODE, obterChave(), GCMParameterSpec(TAG_BITS, dados, 0, IV_BYTES))
        String(cipher.doFinal(dados, IV_BYTES, dados.size - IV_BYTES))
    }.getOrNull()

    private fun obterChave(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(ALIAS_CHAVE, null) as? SecretKey)?.let { return it }

        val especificacao = KeyGenParameterSpec.Builder(
            ALIAS_CHAVE,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            .apply { init(especificacao) }
            .generateKey()
    }

    private companion object {
        val CHAVE_TOKEN = stringPreferencesKey("token")
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val ALIAS_CHAVE = "micar_token"
        const val TRANSFORMACAO = "AES/GCM/NoPadding"
        const val IV_BYTES = 12
        const val TAG_BITS = 128
    }
}
