package com.gianluca_gdc.tabsplitter.model

import android.content.Context
import com.gianluca_gdc.tabsplitter.ui.SettingsDataStore
import com.gianluca_gdc.tabsplitter.ui.UserPaymentSettings
import kotlinx.coroutines.flow.first

object PaymentHandlesAndroid {
    suspend fun fromSettings(context: Context): PaymentHandles {
        val settingsFlow = SettingsDataStore.getSettings(context)
        val settings: UserPaymentSettings = settingsFlow.first()

        return PaymentHandles(
            venmo = settings.venmoHandle.takeIf { it.isNotBlank() },
            cashapp = settings.cashAppHandle.takeIf { it.isNotBlank() },
            zelle = settings.zelleHandle.takeIf { it.isNotBlank() }
        )
    }
}