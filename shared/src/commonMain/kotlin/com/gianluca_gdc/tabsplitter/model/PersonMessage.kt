package com.gianluca_gdc.tabsplitter.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonMessage(
    val name:String,
    val phone:String,
    val amount: String,
    val message: String
)
