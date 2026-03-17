package com.gianluca_gdc.tabsplitter.util

import com.gianluca_gdc.tabsplitter.util.roundToTwo

// import kotlin.math.abs -- no longer needed
import kotlin.collections.ArrayDeque
import com.gianluca_gdc.tabsplitter.util.roundToTwo

data class ParsedReceipt(
    val items: List<Pair<String, Double>>,
    val subtotal: Double,
    val tax: Double,
    val total: Double
) {
    fun calculateShares(): List<Pair<String, Double>> {
        val itemTotals = items.groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }
        val totalItemSum = itemTotals.values.sum()
        val taxSharePerDollar = if (totalItemSum != 0.0) tax / totalItemSum else 0.0
        val tipSharePerDollar = if (totalItemSum != 0.0) (total - subtotal - tax) / totalItemSum else 0.0

        val preliminary = itemTotals.map { (name, baseTotal) ->
            val taxPortion = baseTotal * taxSharePerDollar
            val tipPortion = baseTotal * tipSharePerDollar
            name to (baseTotal + taxPortion + tipPortion)
        }

        // Normalize if needed
        val computedTotal = preliminary.sumOf { it.second }
        val adjustmentRatio = if (computedTotal != 0.0) total / computedTotal else 1.0

        return preliminary.map { (name, value) -> name to (value * adjustmentRatio) }
    }
}



fun parseReceiptText(rawText: String): ParsedReceipt {
    val priceRegex = Regex("""\$?(\d+\.\d{1,2})""")
    val summaryLabels = listOf("subtotal", "admin fee", "tax", "total")
    val qtyPrefix = Regex("""^[1-9]\d?[.)]?\s""")

    // 1. Split lines in visual order
    val rawLines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
    // Debug: print raw OCR lines before parsing
    println("=== Raw OCR Lines ===")
    rawLines.forEach { line ->
        println(line)
    }
    println("=====================")
    // Normalize lines where OCR duplicated the quantity (e.g., "1 1 Brunch..." -> "1 Brunch...")
    val normalizedLines = rawLines.map { line ->
        line.replaceFirst(
            Regex("""^([1-9]\d?)[.)]?\s+([1-9]\d?)[.)]?\s+"""),
            "$1 "
        )
    }

    // 2. Collect all prices in the order they appear
    val priceQueue = ArrayDeque<Double>()
    normalizedLines.forEach { line ->
        priceRegex.findAll(line).forEach { match ->
            priceQueue.add(match.groupValues[1].toDouble().roundToTwo())
        }
    }

    // 3. Iterate top-down, assigning each item its next price (summary handled after)
    val items = mutableListOf<Pair<String, Double>>()
    normalizedLines.forEach { line ->
        when {
            // Item lines: must start with a quantity prefix
            qtyPrefix.containsMatchIn(line) -> {
                val name = line.replace(priceRegex, "").trim().removeSuffix(":")
                val price = priceQueue.removeFirstOrNull() ?: 0.0
                items.add(name to price.roundToTwo())
            }
            // All other lines are ignored (summary handled separately)
            else -> { /* skip */ }
        }
    }

    // --- New summary extraction logic ---
    // Collect all numeric values from the raw OCR lines
    val allValues = normalizedLines.flatMap { line ->
        priceRegex.findAll(line).map { it.groupValues[1].toDouble().roundToTwo() }
    }
    // Determine subtotal and total
    val subtotal = items.sumOf { it.second }.roundToTwo()
    // Use the highest value seen as the total
    val total = allValues.maxOrNull() ?: subtotal
    // Compute tax as difference between highest value and subtotal
    val tax = (total - subtotal).roundToTwo()

    // Debug
    println("=== Parsed Receipt Items ===")
    items.forEach { (n, p) -> println("$n : $p") }
    println("Subtotal : $subtotal")
    println("Tax      : $tax")
    println("Total    : $total")
    println("===========================")

    return ParsedReceipt(items, subtotal, tax, total)
}

fun Double.roundToTwo(): Double = kotlin.math.round(this * 100) / 100
