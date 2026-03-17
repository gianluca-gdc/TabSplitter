package com.gianluca_gdc.tabsplitter.model
import com.gianluca_gdc.tabsplitter.model.AssignedItem



data class BillItem(
    val name: String,
    val price: Double,
    var assignedTo: String? = null  // starts null
)

/**
 * Converts a BillItem into an AssignedItem with default quantity = 1.
 */
fun BillItem.toAssignedItem(): AssignedItem =
    AssignedItem(
        name = this.name,
        price = this.price,
        quantity = 1,
        assignedTo = this.assignedTo ?: ""
    )