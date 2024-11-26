package com.example.ontime.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.ontime.data.model.account.AccountData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedSharedPrefs = EncryptedSharedPreferences.create(
        context,
        "secret_account_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAccountInfo(accountData: AccountData) {
        encryptedSharedPrefs.edit()
            .putString("bank_name", accountData.bankName)
            .putString("account_number", accountData.accountNumber)
            .putString("account_holder", accountData.accountHolder)
            .apply()
    }

    fun getAccountInfo(): AccountData? {
        val bankName = encryptedSharedPrefs.getString("bank_name", null) ?: return null
        val accountNumber = encryptedSharedPrefs.getString("account_number", null) ?: return null
        val accountHolder = encryptedSharedPrefs.getString("account_holder", null) ?: return null

        return AccountData(bankName, accountNumber, accountHolder)
    }
}