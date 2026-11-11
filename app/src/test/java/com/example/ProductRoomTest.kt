package com.example

import com.example.data.local.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ProductRoomTest {

    @Test
    fun testProductEntityCreation() {
        val product = Product(
            id = 1,
            name = "Veste en Jean Vintage",
            description = "Superbe veste en jean taille M, importée de Gombe",
            price = 35.0,
            city = "Kinshasa",
            category = "Vêtements"
        )

        assertEquals(1, product.id)
        assertEquals("Veste en Jean Vintage", product.name)
        assertEquals("Superbe veste en jean taille M, importée de Gombe", product.description)
        assertEquals(35.0, product.price, 0.001)
        assertEquals("Kinshasa", product.city)
        assertEquals("Vêtements", product.category)
        assertEquals(98000L, product.priceCdf)
        assertNotNull(product.formattedPriceUsd)
        assertNotNull(product.formattedPriceCdf)
    }

    @Test
    fun testProductDefaultValues() {
        val product = Product(
            name = "Robe Wax",
            description = "Motif fleuri",
            price = 20.0,
            city = "Lubumbashi",
            category = "Robes"
        )

        assertEquals(0, product.id)
        assertEquals("Robe Wax", product.name)
        assertEquals("Lubumbashi", product.city)
        assertEquals("Robes", product.category)
    }
}
