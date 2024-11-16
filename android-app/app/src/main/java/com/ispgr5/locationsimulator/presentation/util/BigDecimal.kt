package com.ispgr5.locationsimulator.presentation.util

import java.math.BigDecimal
import java.math.RoundingMode


fun <T : Number> BigDecimal.between(lowerInclusive: T, upperExclusive: T): Boolean {
    val satisfiesLower = this >= lowerInclusive.toBigDecimal()
    val satisfiesUpper = this < upperExclusive.toBigDecimal()
    return satisfiesLower && satisfiesUpper
}

private fun Number.toBigDecimal(): BigDecimal {
    return when (this) {
        is Double -> BigDecimal.valueOf(this)
        is Long -> BigDecimal.valueOf(this)
        is Float -> this.toDouble().toBigDecimal()
        is Int -> this.toLong().toBigDecimal()
        else -> throw UnsupportedOperationException("can't convert $this (${this::class.simpleName}) to BigDecimal")
    }
}

fun BigDecimal.millisToSeconds(): BigDecimal =
    this.divide(BigDecimal.valueOf(1000L), 1, RoundingMode.HALF_UP)
