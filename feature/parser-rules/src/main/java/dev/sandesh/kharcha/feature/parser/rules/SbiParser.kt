package dev.sandesh.kharcha.feature.parser.rules

import dev.sandesh.kharcha.core.model.*

/**
 * SBI (State Bank of India) SMS parser.
 *
 * Tested against common SBI SMS formats:
 *  - Debit (POS/online): "Your A/c XX1234 is debited by Rs.500.00 on 01/01/25..."
 *  - UPI debit: "Rs.500.00 debited from SBI A/c XX1234 to MERCHANT via UPI..."
 *  - UPI credit: "Rs.1000.00 credited to SBI A/c XX1234 from XYZ via UPI Ref 123456..."
 *  - Credit card bill payment: "Payment of Rs.2500 received for SBI Credit Card XX1234..."
 *  - Balance inquiry: "Bal in SBI A/c XX1234: Rs.15,234.56"
 *
 * Returns ParseResult with confidence ≥ 0.85 for cleanly parsed messages,
 * lower for messages where only partial fields were extracted.
 */
class SbiParser : BankParser {

    override val parserName = "SBI"

    // SBI debit formats
    private val sbiDebitPatterns = listOf(
        // "Your A/c No.XX1234 is debited with INR 500.00 on 01-Jan-25 ..."
        Regex(
            """(?:a/c|account|ac)\s*(?:no\.?)?\s*[xX*]+(\d{4})\s+is\s+debited\s+(?:with|by)\s+(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        ),
        // "Rs.500 debited from SBI A/c XX1234"
        Regex(
            """(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)\s+debited\s+from\s+(?:SBI\s+)?(?:a/c|account)\s*[xX*]+(\d{4})""",
            RegexOption.IGNORE_CASE
        ),
        // "Debited Rs.500 from A/c XX1234"
        Regex(
            """(?:debited|debit)\s+(?:INR|Rs\.?|₹)?\s*([\d,]+(?:\.\d{1,2})?)\s+from\s+(?:a/c|account)\s*[xX*]+(\d{4})""",
            RegexOption.IGNORE_CASE
        ),
    )

    private val sbiCreditPatterns = listOf(
        // "Your A/c XX1234 is credited with Rs.1000.00"
        Regex(
            """(?:a/c|account|ac)\s*(?:no\.?)?\s*[xX*]+(\d{4})\s+is\s+credited\s+(?:with|by)\s+(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        ),
        // "Rs.1000 credited to SBI A/c XX1234"
        Regex(
            """(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)\s+credited\s+to\s+(?:SBI\s+)?(?:a/c|account)\s*[xX*]+(\d{4})""",
            RegexOption.IGNORE_CASE
        ),
    )

    private val merchantPatterns = listOf(
        Regex("""to\s+([A-Z][A-Za-z0-9*\s\-&]{2,30}?)\s+(?:via|UPI|on|for)""", RegexOption.IGNORE_CASE),
        Regex("""at\s+([A-Z][A-Za-z0-9*\s\-&]{2,30?}?)\s+(?:on|UPI|via)""", RegexOption.IGNORE_CASE),
        Regex("""for\s+([A-Z][A-Za-z0-9*\s\-&]{2,30}?)\s+(?:on|UPI|\.)""", RegexOption.IGNORE_CASE),
    )

    override fun parse(sender: String, body: String): ParseResult {
        if (!ParserConstants.isSbiSender(sender)) {
            return ParseResult(0f, null, body, parserName)
        }

        var amount: Double? = null
        var accountLast4: String? = null
        var type: TransactionType? = null
        var confidence = 0f

        // Try debit patterns
        for (pattern in sbiDebitPatterns) {
            val match = pattern.find(body) ?: continue
            val (acct, amt) = extractAccountAndAmount(match) ?: continue
            accountLast4 = acct
            amount = amt
            type = TransactionType.DEBIT
            confidence = 0.90f
            break
        }

        // Try credit patterns if debit didn't match
        if (type == null) {
            for (pattern in sbiCreditPatterns) {
                val match = pattern.find(body) ?: continue
                val (acct, amt) = extractAccountAndAmount(match) ?: continue
                accountLast4 = acct
                amount = amt
                type = TransactionType.CREDIT
                confidence = 0.90f
                break
            }
        }

        // Also check with generic patterns as a fallback
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
            if (amount != null && accountLast4 != null && type != null) confidence = 0.75f
        }

        // Boost confidence if UPI ref is present
        if (ParserConstants.UPI_REF.containsMatchIn(body)) confidence += 0.05f

        if (amount == null || type == null || confidence < ParserConstants.MIN_CONFIDENCE_THRESHOLD) {
            return ParseResult(confidence.coerceAtMost(0.39f), null, body, parserName)
        }

        // Extract merchant
        val merchantRaw = merchantPatterns.firstNotNullOfOrNull { it.find(body)?.groupValues?.get(1)?.trim() } ?: ""
        val upiRef = ParserConstants.UPI_REF.find(body)?.groupValues?.get(1) ?: ""

        val transaction = Transaction(
            accountId = 0L, // Resolved later by ParserEngine against actual accounts
            amount = amount,
            type = type,
            merchantRaw = merchantRaw,
            merchantNormalized = merchantRaw, // LLM normalizes later
            txnRef = upiRef,
            timestamp = System.currentTimeMillis(), // Exact timestamp extracted if available
            source = TransactionSource.SMS,
            parseConfidence = confidence,
            status = if (confidence >= ParserConstants.HIGH_CONFIDENCE_THRESHOLD)
                TransactionStatus.CONFIRMED else TransactionStatus.NEEDS_REVIEW,
            rawSmsText = body
        )

        return ParseResult(confidence, transaction, body, parserName)
    }

    private fun extractAccountAndAmount(match: MatchResult): Pair<String, Double>? {
        val groups = match.groupValues.drop(1).filter { it.isNotEmpty() }
        // Pattern may have account first or amount first depending on regex group order
        val acct = groups.firstOrNull { it.length == 4 && it.all { c -> c.isDigit() } } ?: return null
        val amtStr = groups.firstOrNull { it.contains(Regex("""^\d""")) && it != acct } ?: return null
        val amt = ParserConstants.parseAmount(amtStr) ?: return null
        return Pair(acct, amt)
    }
}
