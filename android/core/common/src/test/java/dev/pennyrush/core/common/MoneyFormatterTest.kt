package dev.pennyrush.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyFormatterTest {

    @Test
    fun `compact preserves negative sign for thousands`() {
        assertEquals("-\u20B95.0K", MoneyFormatter.compact(-5_000.0))
    }

    @Test
    fun `compact formats positive thousands`() {
        assertEquals("\u20B95.0K", MoneyFormatter.compact(5_000.0))
    }

    @Test
    fun `compact delegates small negative amounts to format`() {
        assertEquals("-\u20B9500", MoneyFormatter.compact(-500.0))
    }

    @Test
    fun `compact formats lakh boundary`() {
        assertEquals("\u20B91.0L", MoneyFormatter.compact(100_000.0))
    }

    @Test
    fun `compact formats crore boundary`() {
        assertEquals("\u20B91.0Cr", MoneyFormatter.compact(10_000_000.0))
    }

    @Test
    fun `compact uses raw unknown currency code`() {
        assertEquals("EUR5.0K", MoneyFormatter.compact(5_000.0, "EUR"))
    }

    @Test
    fun `compact formatting is locale independent`() {
        val originalLocale = java.util.Locale.getDefault()
        try {
            java.util.Locale.setDefault(java.util.Locale.GERMANY)

            assertEquals("\u20B95.0K", MoneyFormatter.compact(5_000.0))
        } finally {
            java.util.Locale.setDefault(originalLocale)
        }
    }
}
