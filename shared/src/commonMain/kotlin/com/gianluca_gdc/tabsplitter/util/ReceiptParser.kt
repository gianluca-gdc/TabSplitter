package com.gianluca_gdc.tabsplitter.util

import com.gianluca_gdc.tabsplitter.util.roundToTwo

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

        
        val computedTotal = preliminary.sumOf { it.second }
        val adjustmentRatio = if (computedTotal != 0.0) total / computedTotal else 1.0

        return preliminary.map { (name, value) -> name to (value * adjustmentRatio) }
    }
}



fun parseReceiptText(rawText: String): ParsedReceipt {
    val priceRegex = Regex("""\$?(\d+\.\d{1,2})""")
    val summaryLabels = listOf("subtotal", "admin fee", "tax", "total")
    val qtyPrefix = Regex("""^[1-9]\d?[.)]?\s""")

    // split lines in visual order
    val rawLines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
    // print raw ocr lines before parse
    println("=== Raw OCR Lines ===")
    rawLines.forEach { line ->
        println(line)
    }
    println("=====================")
    // normalize lines in case ocr duplicated the quantity
    val normalizedLines = rawLines.map { line ->
        line.replaceFirst(
            Regex("""^([1-9]\d?)[.)]?\s+([1-9]\d?)[.)]?\s+"""),
            "$1 "
        )
    }

    // collect all prices in the order they appear
    val priceQueue = ArrayDeque<Double>()
    normalizedLines.forEach { line ->
        priceRegex.findAll(line).forEach { match ->
            priceQueue.add(match.groupValues[1].toDouble().roundToTwo())
        }
    }

    // iterate and assign each item its next price 
    val items = mutableListOf<Pair<String, Double>>()
    normalizedLines.forEach { line ->
        when {
            // must start with a quantity 
            qtyPrefix.containsMatchIn(line) -> {
                val name = line.replace(priceRegex, "").trim().removeSuffix(":")
                val price = priceQueue.removeFirstOrNull() ?: 0.0
                items.add(name to price.roundToTwo())
            }
            // ignored 
            else -> { /* skip */ }
        }
    }

   
    // collect all vals from the ocr lines
    val allValues = normalizedLines.flatMap { line ->
        priceRegex.findAll(line).map { it.groupValues[1].toDouble().roundToTwo() }
    }
    // calc subtotal and total
    val subtotal = items.sumOf { it.second }.roundToTwo()
    // use max Val
    val total = allValues.maxOrNull() ?: subtotal
    // compute tax as highest value and subtotal - subract
    val tax = (total - subtotal).roundToTwo()

    // debug nonsense
    println("=== Parsed Receipt Items ===")
    items.forEach { (n, p) -> println("$n : $p") }
    println("Subtotal : $subtotal")
    println("Tax      : $tax")
    println("Total    : $total")
    println("===========================")

    return ParsedReceipt(items, subtotal, tax, total)
}

fun Double.roundToTwo(): Double = kotlin.math.round(this * 100) / 100
