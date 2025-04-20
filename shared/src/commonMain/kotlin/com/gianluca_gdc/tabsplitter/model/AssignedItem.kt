package com.gianluca_gdc.tabsplitter.model

data class AssignedItem(
    val name: String,
    val price: Double, // Changed from unitPrice to price
    val quantity: Int
)
