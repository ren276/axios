package dev.sandesh.kharcha.feature.parser.rules

import dev.sandesh.kharcha.core.model.*

/**
 * Generic fallback parser for unknown bank senders.
 * Uses common pattern heuristics — lower confidence (0.50–0.70).
 * Routes to LLM tier if confidence is insufficient.
 */
class GenericFallbackParser : BankParser {

    override val parserName = "GENERIC"

    // Common transaction SMS indicators
    private val isTransactionSms = Regex(
        """(?:debited|credited|debit|credit|withdrawn|transferred|paid|received|upi|txn|transaction)""",
        RegexOption.IGNORE_CASE
    )

    override fun parse(sender: String, body: String): ParseResult {
        // Reject very short messages — likely OTPs or non-financial
        if (body.length < 30) return ParseResult(0f, null, body, parserName)
        // Reject if no transaction indicators
        if (!isTransactionSms.containsMatchIn(body)) return ParseResult(0.1f, null, body, parserName)

        var confidence = 0.40f

        // Extract amount
        val amount = ParserConstants.AMOUNT_PATTERN.find(body)?.groupValues?.get(1)
            ?.let { ParserConstants.parseAmount(it) }
            ?: ParserConstants.AMOUNT_PATTERN_ALT.find(body)?.groupValues?.get(1)
                ?.let { ParserConstants.parseAmount(it) }

        if (amount == null || amount <= 0.0) return ParseResult(0.2f, null, body, parserName)
        confidence += 0.15f

        // Extract account
        val accountLast4 = ParserConstants.ACCOUNT_LAST4.find(body)?.groupValues?.get(1)
            ?: ParserConstants.ACCOUNT_LAST4_ALT.find(body)?.groupValues?.get(1)
        if (accountLast4 != null) confidence += 0.10f

        // Determine type
        val type = when {
            ParserConstants.DEBIT_KEYWORDS.containsMatchIn(body) -> {
                confidence += 0.10f; TransactionType.DEBIT
            }
            ParserConstants.CREDIT_KEYWORDS.containsMatchIn(body) -> {
                confidence += 0.10f; TransactionType.CREDIT
            }
            else -> return ParseResult(confidence.coerceAtMost(0.39f), null, body, parserName)
        }

        // Boost for UPI reference
        val upiRef = ParserConstants.UPI_REF.find(body)?.groupValues?.get(1) ?: ""
        if (upiRef.isNotEmpty()) confidence += 0.05f

        if (confidence < ParserConstants.MIN_CONFIDENCE_THRESHOLD) {
            return ParseResult(confidence, null, body, parserName)
        }

        val transaction = Transaction(
            accountId = 0L,
            amount = amount,
            type = type,
            merchantRaw = "",
            merchantNormalized = "",
            txnRef = upiRef,
            timestamp = System.currentTimeMillis(),
            source = TransactionSource.SMS,
            parseConfidence = confidence,
            // Generic parser always routes to NEEDS_REVIEW — too risky to auto-confirm unknown senders
            status = TransactionStatus.NEEDS_REVIEW,
            rawSmsText = body
        )

        return ParseResult(confidence, transaction, body, parserName)
    }
}
