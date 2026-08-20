package dev.pennyrush.core.common

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

object MoneyFormatter {

    private val inLocale: Locale = Locale.Builder().setLanguage("en").setRegion("IN").build()

    fun format(
        amount: Double,
        currencyCode: String = "INR",
        locale: Locale = inLocale,
        showSign: Boolean = false,
    ): String {
        val currency = runCatching { Currency.getInstance(currencyCode) }.getOrNull()

        val formatted = if (currency != null) {
            NumberFormat.getCurrencyInstance(locale).apply {
                this.currency = currency
                maximumFractionDigits = 0
            }.format(abs(amount))
        } else {
            val number = NumberFormat.getNumberInstance(locale).apply {
                maximumFractionDigits = 0
            }.format(abs(amount))
            "$currencyCode$number"
        }

        return when {
            showSign && amount > 0 -> "+$formatted"
            showSign && amount < 0 -> "-$formatted"
            amount < 0 -> "-$formatted"
            else -> formatted
        }
    }

    fun compact(amount: Double, currencyCode: String = "INR"): String {
        val abs = abs(amount)
        val sign = if (amount < 0) "-" else ""
        val sym = when (currencyCode) {
            "INR" -> "₹"
            "USD" -> "$"
            else -> currencyCode
        }

        return when {
            abs >= 10_000_000 ->
                "$sign$sym${String.format(Locale.ROOT, "%.1f", abs / 10_000_000)}Cr"

            abs >= 100_000 ->
                "$sign$sym${String.format(Locale.ROOT, "%.1f", abs / 100_000)}L"

            abs >= 1_000 ->
                "$sign$sym${String.format(Locale.ROOT, "%.1f", abs / 1_000)}K"

            else -> format(amount, currencyCode)
        }
    }
}
