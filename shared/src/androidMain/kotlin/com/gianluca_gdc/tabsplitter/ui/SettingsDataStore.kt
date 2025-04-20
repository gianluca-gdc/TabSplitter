package com.gianluca_gdc.tabsplitter.ui

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gianluca_gdc.tabsplitter.ui.SettingsDataStore.VENMO
import com.gianluca_gdc.tabsplitter.ui.SettingsDataStore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserPaymentSettings(
    val payerName: String,
    val venmoHandle: String,
    val cashAppHandle: String,
    val zelleHandle: String
)

object SettingsDataStore {
    private val Context.dataStore by preferencesDataStore(name = "user_settings")

    private val PAYER_NAME = stringPreferencesKey("payer_name")
    private val VENMO = stringPreferencesKey("venmo")
    private val CASHAPP = stringPreferencesKey("cashapp")
    private val ZELLE = stringPreferencesKey("zelle")

    suspend fun SavePayerName(context: Context, payername: String) {
        context.dataStore.edit {
            it[PAYER_NAME] = payername
        }
    }

    suspend fun SaveVenmo(context: Context, venmoHandle: String) {
        context.dataStore.edit {
            it[VENMO] = venmoHandle
        }
    }

    suspend fun SaveZelle(context: Context, zelleHandle: String) {
        context.dataStore.edit {
            it[ZELLE] = zelleHandle
        }
    }

    suspend fun SaveCashapp(context: Context, cashappHandle: String) {
        context.dataStore.edit {
            it[CASHAPP] = cashappHandle
        }
    }

    fun getPayerNameFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[PAYER_NAME] ?: "" }

    fun getVenmoFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[VENMO] ?: "" }

    fun getZelleFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[ZELLE] ?: "" }

    fun getCashappFlow(context: Context): Flow<String> =
        context.dataStore.data.map {it[CASHAPP] ?: ""}

    fun getSettings(context: Context): Flow<UserPaymentSettings> =
        context.dataStore.data.map { prefs ->
            UserPaymentSettings(
                payerName = prefs[PAYER_NAME] ?: "",
                venmoHandle = prefs[VENMO] ?: "",
                cashAppHandle = prefs[CASHAPP] ?: "",
                zelleHandle = prefs[ZELLE] ?: ""
            )
        }

}

