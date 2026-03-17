package com.gianluca_gdc.tabsplitter.backend.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.request.forms.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.util.InternalAPI
import kotlinx.serialization.Serializable
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.Phonenumber

@Serializable
data class PersonMessage(
    val name: String,
    val phone: String,
    val amount: String,
    val message: String
)

@Serializable
data class MessageBatch(
    val payer: String,
    val people: List<PersonMessage>
)
fun formatToE164(rawNumber: String, region: String = "US"): String? {
    val cleaned = rawNumber.replace("[^\\d+]".toRegex(), "") // remove all but digits and +
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(cleaned, region)
        if (phoneUtil.isValidNumber(numberProto)) {
            phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164)
        } else null
    } catch (e: NumberParseException) {
        null
    }
}
@OptIn(InternalAPI::class)
fun Route.messageRoutes() {
    post("/send-messages") {
        val batch = call.receive<MessageBatch>()
        val twilioSid = System.getenv("TWILIO_ACCOUNT_SID")
        val twilioAuthToken = System.getenv("TWILIO_AUTH_TOKEN")
        val messagingServiceSid = System.getenv("TWILIO_MESSAGING_SERVICE_SID")

        val client = HttpClient(CIO) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = twilioSid.orEmpty(),
                            password = twilioAuthToken.orEmpty()
                        )
                    }
                    sendWithoutRequest { true }
                }
            }
        }

        batch.people.forEach { person ->
            val formattedPhone = formatToE164(person.phone)
            if (formattedPhone == null) {
                println("❌ Skipping invalid phone for ${person.name}: ${person.phone}")
                return@forEach
            }
            try {
                val response: HttpResponse = client.post("https://api.twilio.com/2010-04-01/Accounts/$twilioSid/Messages.json") {
                    setBody(
                        FormDataContent(Parameters.build {
                            append("To", formattedPhone)
                            append("MessagingServiceSid", messagingServiceSid ?: "")
                            append("Body", person.message)
                        })
                    )
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    }
                }
                val responseText = response.bodyAsText()
                if ("\"error_code\": null" in responseText) {
                    println("✅ Sent to $formattedPhone: $responseText")
                } else {
                    println("❌ Failed to send to $formattedPhone: $responseText")
                    println("❌ ${response.headers}")
                }
            } catch (e: Exception) {
                println("❌ Failed to send to ${formattedPhone}: ${e.message}")
            }
        }

        call.respond(HttpStatusCode.OK, "Messages sent successfully.")
    }
}