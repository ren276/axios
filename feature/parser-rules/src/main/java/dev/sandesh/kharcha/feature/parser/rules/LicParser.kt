package dev.sandesh.kharcha.feature.parser.rules

import dev.sandesh.kharcha.core.model.*

/**
 * LIC (Life Insurance Corporation of India) SMS parser.
 *
 * LIC SMS characteristics (per user notes):
 *  - Mostly contain policy number + dates
 *  - Reminder to submit documents / pay premium
 *  - No consistent amount format (premium amount may or may not be present)
 *  - Treat as LOWER CONFIDENCE — route to LLM tier more aggressively
 *
 * Extracted fields:
 *  - Policy number
 *  - Due date (if present)
 *  - Premium amount (if present — often absent in reminders)
 *  - Type: always DEBIT (insurance premium payment)
 *
 * Resulting transaction category: Insurance / LIC (category ID 5)
 */
class LicParser : BankParser {

    override val parserName = "LIC"

    /** LIC max confidence — kept deliberately lower to route borderline cases to LLM */
    private val LIC_BASE_CONFIDENCE = 0.65f

    private val policyNumberPattern = Regex(
        """(?:policy\s*(?:no\.?|number)?\s*:?\s*)(\d{8,10})""",
        RegexOption.IGNORE_CASE
    )

    private val dueDatePattern = Regex(
        """(?:due\s*(?:date|on|by)|before|on|premium\s*due)\s*:?\s*(\d{1,2}[/-]\d{1,2}[/-]\d{2,4}|\d{1,2}\s+\w{3}\s+\d{4})""",
        RegexOption.IGNORE_CASE
    )

    private val licAmountPattern = Regex(
        """(?:premium|amount|Rs\.?|INR|₹)\s*:?\s*(?:Rs\.?|INR|₹)?\s*([\d,]+(?:\.\d{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    private val reminderKeywords = Regex(
        """(?:reminder|due|submit|renewal|pay\s*premium|lapse|revival|notice|instalment)""",
        RegexOption.IGNORE_CASE
    )

    private val paidKeywords = Regex(
        """(?:received|payment\s+received|premium\s+paid|thank\s+you\s+for|successfully\s+paid)""",
        RegexOption.IGNORE_CASE
    )

    override fun parse(sender: String, body: String): ParseResult {
        if (!ParserConstants.isLicSender(sender)) {
            return ParseResult(0f, null, body, parserName)
        }

        var confidence = 0.40f // Base — sender confirmed as LIC
        var amount: Double? = null
        var policyNumber: String? = null

        // Extract policy number
        policyNumber = policyNumberPattern.find(body)?.groupValues?.get(1)
        if (policyNumber != null) confidence += 0.10f

        // Extract amount if present
        val amountMatch = licAmountPattern.find(body)
        if (amountMatch != null) {
            amount = ParserConstants.parseAmount(amountMatch.groupValues[1])
            if (amount != null) confidence += 0.15f
        }

        // Determine if this is a paid confirmation or a reminder
        val isPaid = paidKeywords.containsMatchIn(body)
        val isReminder = reminderKeywords.containsMatchIn(body)

        if (isPaid) confidence += 0.10f // Payment confirmation is higher value

        // LIC messages are ALWAYS DEBIT from the user's perspective (premium payments)
        val type = TransactionType.DEBIT

        // Cap LIC confidence at LIC_BASE_CONFIDENCE — always route to at least LLM review
        confidence = confidence.coerceAtMost(LIC_BASE_CONFIDENCE)

        if (confidence < ParserConstants.MIN_CONFIDENCE_THRESHOLD) {
            return ParseResult(confidence, null, body, parserName)
        }

        val txnRef = policyNumber ?: ""

        val transaction = Transaction(
            accountId = 0L,
            amount = amount ?: 0.0, // Amount may be unknown in reminders
            type = type,
            merchantRaw = "LIC India",
            merchantNormalized = "LIC India",
            categoryId = 5L, // Insurance / LIC category
            txnRef = txnRef,
            timestamp = System.currentTimeMillis(),
            source = TransactionSource.SMS,
            parseConfidence = confidence,
            // LIC always goes to NEEDS_REVIEW so user can confirm amount
            status = TransactionStatus.NEEDS_REVIEW,
            rawSmsText = body,
            note = if (isReminder && !isPaid) "Premium reminder — not yet paid" else ""
        )

        return ParseResult(confidence, transaction, body, parserName)
    }
}
