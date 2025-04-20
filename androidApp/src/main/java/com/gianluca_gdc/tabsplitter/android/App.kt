package com.gianluca_gdc.tabsplitter.android


import SettingsScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import com.gianluca_gdc.tabsplitter.ui.BillEntryScreen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.gianluca_gdc.tabsplitter.android.ui.ItemEntryScreen
import com.gianluca_gdc.tabsplitter.android.ui.PeopleEntryScreen
import com.gianluca_gdc.tabsplitter.android.ui.SuccessScreen
import com.gianluca_gdc.tabsplitter.android.ui.Summary
import com.gianluca_gdc.tabsplitter.model.Person
import com.gianluca_gdc.tabsplitter.model.BillDetails
import com.gianluca_gdc.tabsplitter.ui.SettingsDataStore
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf

@Composable
fun App(logoPainter: Painter) {
    var currentStep by remember { mutableStateOf("bill") }
    val people = remember { mutableStateListOf<Person>() }
    var billDetails by remember { mutableStateOf<BillDetails?>(null) }
    val context = LocalContext.current
    val payerName by SettingsDataStore.getPayerNameFlow(context).collectAsState(initial = "")
    val venmoHandle by SettingsDataStore.getVenmoFlow(context).collectAsState(initial = "")
    val cashAppHandle by SettingsDataStore.getCashappFlow(context).collectAsState(initial = "")
    val zelleInfo by SettingsDataStore.getZelleFlow(context).collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()

    var payerNameInput by rememberSaveable { mutableStateOf(payerName) }
    var venmoHandleInput by rememberSaveable { mutableStateOf(venmoHandle) }
    var cashAppHandleInput by rememberSaveable { mutableStateOf(cashAppHandle) }
    var zelleInfoInput by rememberSaveable { mutableStateOf(zelleInfo) }

    LaunchedEffect(payerName) {
        payerNameInput = payerName
    }

    LaunchedEffect(venmoHandle) {
        venmoHandleInput = venmoHandle
    }

    LaunchedEffect(cashAppHandle) {
        cashAppHandleInput = cashAppHandle
    }

    LaunchedEffect(zelleInfo) {
        zelleInfoInput = zelleInfo
    }

    MaterialTheme {
        when (currentStep) {
            "bill" -> BillEntryScreen(
                logoPainter = logoPainter,
                onNext = { details ->
                    billDetails = details
                    currentStep = "people"
                },
                onSettings = {
                    currentStep = "settings"
                }
            )
            "settings" -> {
                SettingsScreen(
                    onBack = { currentStep = "bill" },
                    payerName = payerNameInput,
                    onPayerNameChange = { newName ->
                        payerNameInput = newName
                        coroutineScope.launch {
                            SettingsDataStore.SavePayerName(context, newName)
                        }
                    },
                    venmoHandle = venmoHandleInput,
                    onVenmoChange = { newVenmo ->
                        venmoHandleInput = newVenmo
                        coroutineScope.launch {
                            SettingsDataStore.SaveVenmo(context, newVenmo)
                        }
                    },
                    cashAppHandle = cashAppHandleInput,
                    onCashAppChange = { newCashApp ->
                        cashAppHandleInput = newCashApp
                        coroutineScope.launch {
                            SettingsDataStore.SaveCashapp(context, newCashApp)
                        }
                    },
                    zelleInfo = zelleInfoInput,
                    onZelleChange = { newZelle ->
                        zelleInfoInput = newZelle
                        coroutineScope.launch {
                            SettingsDataStore.SaveZelle(context, newZelle)
                        }
                    }
                )
            }

            "people" -> PeopleEntryScreen(
                onBack = {
                    currentStep = "bill"
                },
                payerName = payerNameInput,
                onNext = { nameToNumberMap ->
                    people.clear()
                    nameToNumberMap.forEach { (name, phoneNumber) ->
                        people.add(Person(name = name, phoneNumber = phoneNumber))
                    }
                    currentStep = "items"
                }
            )
            "items" -> ItemEntryScreen(
                onBack = {
                    currentStep = "people"
                },
                payerName = payerNameInput,
                onNext = { updatedPeople ->

                    people.clear()
                    people.addAll(updatedPeople)
                    currentStep = "summary"
                },
                people = people
            )
            "summary" -> Summary(
                onNext = {
                    currentStep = "success"
                },
                onBack = {
                    currentStep = "items"
                },
                people = people,
                subtotal = billDetails?.subtotal ?: 0.0,
                tip = billDetails?.tip ?: 0.0,
                tax = billDetails?.tax ?: 0.0,
                payerName = payerNameInput
            )
            "success" -> SuccessScreen(
                people = people,
                payerName = payerName,
                onNext = {
                    currentStep = "bill"
                    people.clear()
                    billDetails = null
                }
            ) // Replace with a real composable later
        }
    }
}
