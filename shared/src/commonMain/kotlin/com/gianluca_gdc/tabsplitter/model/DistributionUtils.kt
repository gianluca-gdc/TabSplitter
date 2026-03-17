package com.gianluca_gdc.tabsplitter.model

/**
 * Given a list of BillItems and a map of (item, person)→quantity,
 * returns a flat list of AssignedItems splitting each BillItem’s price
 * proportionally based on quantity.
 */
fun distribute(
    items: List<BillItem>,
    assignments: Map<Pair<BillItem, String>, Int>
): List<AssignedItem> = buildList {
    items.forEach { item ->
        val totalQty = assignments.entries
            .filter { it.key.first == item }
            .sumOf { it.value }
        if (totalQty > 0) {
            assignments.entries
                .filter { it.key.first == item && it.value > 0 }
                .forEach { (key, qty) ->
                    val share = item.price * qty.toDouble() / totalQty.toDouble()
                    add(AssignedItem(item.name, share, qty, key.second))
                }
        }
    }
}