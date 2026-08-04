package dev.sandesh.kharcha.feature.parser.rules

import dev.sandesh.kharcha.core.model.*

/** Common interface all bank parsers implement. */
interface BankParser {
    val parserName: String
    fun parse(sender: String, body: String): ParseResult
}

/**
 * Bank of Baroda SMS parser.
 * Common BoB SMS formats:
 *  - "Dear Customer, Rs.500.00 has been debited from your A/c XXXXXXXX1234 on 01-01-2025..."
 *  - "INR 1000.00 credited to your account ending 1234 on 01/01/2025"
 *  - "Your BOB account XX1234 is debited by Rs.2500 for UPI payment"
 */
class BobParser : BankParser {

    override val parserName = "BOB"

    private val bobDebitPatterns = listOf(
        Regex(
            """(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)\s+has\s+been\s+debited\s+from\s+your\s+(?:a/c|account)[^\d]*(\d{4})""",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            """(?:a/c|account)[^\d]*(\d{4})\s+(?:is|has been)\s+debited\s+(?:by|with)\s+(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            """debited\s+(?:INR|Rs\.?|₹)?\s*([\d,]+(?:\.\d{1,2})?)\s+from\s+(?:your\s+)?(?:a/c|account)[^\d]*(\d{4})""",
            RegexOption.IGNORE_CASE
        ),
    )

    private val bobCreditPatterns = listOf(
        Regex(
            """(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)\s+(?:has been\s+)?credited\s+to\s+your\s+(?:a/c|account)[^\d]*(\d{4})""",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            """(?:a/c|account)[^\d]*(\d{4})\s+(?:is|has been)\s+credited\s+(?:by|with)\s+(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        ),
    )

    override fun parse(sender: String, body: String): ParseResult {
        if (!ParserConstants.isBobSender(sender)) {
            return ParseResult(0f, null, body, parserName)
        }

        var amount: Double? = null
        var accountLast4: String? = null
        var type: TransactionType? = null
        var confidence = 0f

        for (pattern in bobDebitPatterns) {
            val match = pattern.find(body) ?: continue
            val (acct, amt) = extractPair(match) ?: continue
            accountLast4 = acct; amount = amt; type = TransactionType.DEBIT; confidence = 0.88f; break
        }

        if (type == null) {
            for (pattern in bobCreditPatterns) {
                val match = pattern.find(body) ?: continue
                val (acct, amt) = extractPair(match) ?: continue
                accountLast4 = acct; amount = amt; type = TransactionType.CREDIT; confidence = 0.88f; break
            }
        }

        // Generic fallback
        if (type == null) {
            amount = ParserConstants.AMOUNT_PATTERN.find(body)?.groupValues?.get(1)
                ?.let { ParserConstants.parseAmount(it) }
            accountLast4 = ParserConstants.ACCOUNT_LAST4.find(body)?.groupValues?.get(1)
                ?: ParserConstants.ACCOUNT_LAST4_ALT.find(body)?.groupValues?.get(1)
            type = when {
                ParserConstants.DEBIT_KEYWORDS.containsMatchIn(body) -> TransactionType.DEBIT
                ParserConstants.CREDIT_KEYWORDS.containsMatchIn(body) -> TransactionType.CREDIT
                else -> null
            }
            if (amount != null && accountLast4 != null && type != null) confidence = 0.72f
        }

        if (amount == null || type == null || confidence < ParserConstants.MIN_CONFIDENCE_THRESHOLD) {
            return ParseResult(confidence.coerceAtMost(0.39f), null, body, parserName)
        }

        val txnRef = ParserConstants.UPI_REF.find(body)?.groupValues?.get(1) ?: ""

        val transaction = Transaction(
            accountId = 0L,
            amount = amount,
            type = type,
            merchantRaw = extractMerchant(body),
            merchantNormalized = extractMerchant(body),
            txnRef = txnRef,
            timestamp = System.currentTimeMillis(),
            source = TransactionSource.SMS,
            parseConfidence = confidence,
            status = if (confidence >= ParserConstants.HIGH_CONFIDENCE_THRESHOLD)
                TransactionStatus.CONFIRMED else TransactionStatus.NEEDS_REVIEW,
            rawSmsText = body
        )

        return ParseResult(confidence, transaction, body, parserName)
    }

    private fun extractPair(match: MatchResult): Pair<String, Double>? {
        val groups = match.groupValues.drop(1).filter { it.isNotEmpty() }
        val acct = groups.firstOrNull { it.length == 4 && it.all { c -> c.isDigit() } } ?: return null
        val amtStr = groups.firstOrNull { it.contains(Regex("""^\d""")) && it != acct } ?: return null
        return Pair(acct, ParserConstants.parseAmount(amtStr) ?: return null)
    }

    private fun extractMerchant(body: String): String {
        val toMerchant = Regex("""to\s+([A-Za-z0-9*\s\-&]{2,30}?)\s+(?:via|on|for)""", RegexOption.IGNORE_CASE)
            .find(body)?.groupValues?.get(1)?.trim()
        val forMerchant = Regex("""for\s+([A-Za-z0-9*\s\-&]{2,30}?)\s+(?:on|\.)""", RegexOption.IGNORE_CASE)
            .find(body)?.groupValues?.get(1)?.trim()
        return toMerchant ?: forMerchant ?: ""
    }
}
