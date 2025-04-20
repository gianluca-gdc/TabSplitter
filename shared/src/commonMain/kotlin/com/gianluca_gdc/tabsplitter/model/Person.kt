package com.gianluca_gdc.tabsplitter.model

data class Person(
    val name: String,
    val items: MutableList<AssignedItem> = mutableListOf(),
    var total: Double = 0.0, // New property to store the total price
    val phoneNumber: String
){
        val formattedTotal: String
        get() {
            val rounded = (total * 100).toInt()
            val dollars = rounded / 100
            val cents = rounded % 100
            return "$$dollars.${cents.toString().padStart(2, '0')}"

        }

}

