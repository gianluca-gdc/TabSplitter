package com.gianluca_gdc.tabsplitter.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageBatch(
    val payer: String,
    val people: List<PersonMessage>
)
