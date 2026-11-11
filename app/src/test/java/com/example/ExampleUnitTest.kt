package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCurrencyConversion() {
        val priceUsd = 25.0
        val priceCdf = (priceUsd * 2800).toLong()
        assertEquals(70000L, priceCdf)
    }

    @Test
    fun testOrderCalculation() {
        val articlePrice = 30.0
        val protectionFee = 1.0
        val deliveryFee = 2.0
        val total = articlePrice + protectionFee + deliveryFee
        assertEquals(33.0, total, 0.001)
    }
}
