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
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import kotlinx.serialization.Serializable

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

fun Route.messageRoutes() {
    post("/send-messages") {
        val batch = call.receive<MessageBatch>()
        val twilioSid = System.getenv("TWILIO_ACCOUNT_SID")
        val twilioAuthToken = System.getenv("TWILIO_AUTH_TOKEN")
        val messagingServiceSid = System.getenv("TWILIO_PHONE_NUMBER")

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
            try {
                val response: HttpResponse = client.post("https://api.twilio.com/2010-04-01/Accounts/$twilioSid/Messages.json") {
                    parameter("To", person.phone)
                    parameter("MessagingServiceSid", messagingServiceSid)
                    parameter("Body", person.message)
                }
                println("✅ Sent to ${person.phone}: ${response.status}")
            } catch (e: Exception) {
                println("❌ Failed to send to ${person.phone}: ${e.message}")
            }
        }

        call.respond(HttpStatusCode.OK, "Messages sent successfully.")
    }
}