package com.gianluca_gdc.tabsplitter.android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gianluca_gdc.tabsplitter.android.R
import com.gianluca_gdc.tabsplitter.model.MessageBatch
import com.gianluca_gdc.tabsplitter.model.PaymentHandles
import com.gianluca_gdc.tabsplitter.model.Person
import com.gianluca_gdc.tabsplitter.model.PersonMessage
import com.gianluca_gdc.tabsplitter.model.PaymentHandlesAndroid
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.delay
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.client.request.headers



@Composable
fun CheckMark(){
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Success",
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
            tint = Color(0xFF4CAF50) // green
        )
    }
}
fun buildPaymentLinks(
    handles: PaymentHandles,
    amount: String
): List<String> {
    val links = mutableListOf<String>()

    handles.venmo?.let {
        links.add("Venmo: https://venmo.com/$it?txn=request&amount=$amount&note=TabSplit")
    }
    handles.cashapp?.let {
        links.add("CashApp: https://cash.app/\$$it/$amount")
    }
    handles.zelle?.let {
        links.add("Zelle: Send payment to $it via your bank app")
    }

    return links
}
fun sendSms(context:Context, phone: String, message: String){
    val uri = Uri.parse("smsto:$phone") // smsto: triggers SMS-only apps
    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra("sms_body", message)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}
fun buildMessage(
    person: Person,
    amount: String,
    paymentHandles: List<String>,
    payerName:String
):String{
    val itemList = person.items.joinToString("\n") { "${it.name}    $${"%.2f".format(it.price)}" }

    val paymentText = if (paymentHandles.isEmpty()) {
        "$payerName only accepts Apple Pay."
    } else {
        "Please pay using one of the links below:\n" +
                paymentHandles.joinToString("\n") { it + amount }
    }

    return """
        Hey! You just ate with $payerName.

        Here's what you had:
        $itemList

        You owe $payerName a total of $amount.

        $paymentText
    """.trimIndent()
}


suspend fun sendMessages(batch: MessageBatch) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    val response = client.post("http://10.0.2.2:8080/send-messages") {
        headers {
            append("Content-Type", "application/json")
        }
        setBody(batch)
    }

    println("Status: ${response.status}")
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SuccessScreen(onNext: () -> Unit, people: List<Person>, payerName:String) {
    val cleanOffsetY = remember { Animatable(-900f) }
    val brokenOffsetY = remember { Animatable(130f) } // start inside printer, not above
    var showClean by remember { mutableStateOf(true) }
    var showBroken by remember { mutableStateOf(false) }
    var showColor by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var showCheckmark by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            for(i in 0 until 3) {
                cleanOffsetY.snapTo(-900f)
                brokenOffsetY.snapTo(130f)
                showClean = true
                showBroken = false

                cleanOffsetY.animateTo(
                    targetValue = -50f,
                    animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
                )
                showColor = false
                delay(200) // simulate internal printing delay

                showClean = false
                showBroken = true

                brokenOffsetY.animateTo(
                    targetValue = 200f,
                    animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
                )

                delay(1000)
                showColor= true
            }

            val paymentHandles = PaymentHandlesAndroid.fromSettings(context)

            sendMessages(
                MessageBatch(
                    payer = payerName,
                    people = people.map {
                        PersonMessage(
                            name = it.name,
                            phone = it.phoneNumber,
                            amount = "$%.2f".format(it.total),
                            message = buildMessage(
                                it,
                                amount = "$%.2f".format(it.total),
                                paymentHandles = buildPaymentLinks(paymentHandles, "$%.2f".format(it.total)),
                                payerName = payerName
                            )
                        )
                    }
                )
            )

            showCheckmark = true
            delay(1500)
            onNext()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (showCheckmark) {
        CheckMark()
    } else {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Clean receipt feeding in
            if (showClean) {
                Image(
                    painter = painterResource(id = R.drawable.clean_receipt),
                    contentDescription = "Clean Receipt",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = cleanOffsetY.value.dp)
                        .size(260.dp)
                        .zIndex(2F)
                )
            }

            // Broken receipt sliding out
            if(showBroken) {
                Image(
                    painter = painterResource(id = R.drawable.broken_receipt),
                    contentDescription = "Broken Receipt",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = brokenOffsetY.value.dp)
                        .size(250.dp)
                        .zIndex(2F)
                )
            }
            // Bottom printer
            Image(
                painter = painterResource(id = R.drawable.printer_empty_1),
                contentDescription = "Printer Base",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(220.dp)
                    .zIndex(1F)
            )

            // Top printer (visually above all)
            Image(
                painter = painterResource(id = R.drawable.printer_body),
                contentDescription = "Printer Top",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(180.dp)
                    .padding(top = 63.dp)
                    .zIndex(4F)
            )
            if(showColor) {
                Image(
                    painter = painterResource(id = R.drawable.printer_body_green),
                    contentDescription = "Printer Top",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(180.dp)
                        .padding(top = 63.dp)
                        .zIndex(4F)
                )
            }
        }
    }
}