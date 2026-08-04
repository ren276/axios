package dev.sandesh.kharcha.feature.parser.rules

/**
 * Shared regex patterns and constants used across all bank parsers.
 * Central place for threshold tuning.
 */
object ParserConstants {

    /** Confidence must meet or exceed this to auto-confirm a transaction without LLM review */
    const val HIGH_CONFIDENCE_THRESHOLD = 0.85f

    /** Transactions with confidence below this always route to NEEDS_REVIEW */
    const val MIN_CONFIDENCE_THRESHOLD = 0.40f

    // ─── Common regex components ──────────────────────────────────────────────

    /** Matches Indian currency amounts: 1,23,456.78 or 1234.56 or 1234 */
    val AMOUNT_PATTERN = Regex(
        """(?:Rs\.?|INR|₹)\s*([\d,]+(?:\.\d{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    /** Alternate amount pattern — amount first, then currency marker */
    val AMOUNT_PATTERN_ALT = Regex(
        """([\d,]+(?:\.\d{1,2})?)\s*(?:Rs\.?|INR|₹)""",
        RegexOption.IGNORE_CASE
    )

    /** Debit keywords */
    val DEBIT_KEYWORDS = Regex(
        """(?:debited|debit|dr\b|withdrawn|paid|spent|purchase|upi\s*(?:ref|txn)|transferred\s*from)""",
        RegexOption.IGNORE_CASE
    )

    /** Credit keywords */
    val CREDIT_KEYWORDS = Regex(
        """(?:credited|credit|cr\b|received|deposited|refund|cashback|transferred\s*to\s*your)""",
        RegexOption.IGNORE_CASE
    )

    /** Account last 4 digits — various formats */
    val ACCOUNT_LAST4 = Regex(
        """(?:a/c|acct|account|ac|card)\s*(?:no\.?|number)?\s*[xX*]{0,10}(\d{4})""",
        RegexOption.IGNORE_CASE
    )

    /** Alternative account last 4 — "XX1234" or "****1234" pattern */
    val ACCOUNT_LAST4_ALT = Regex("""[xX*]{2,}\s*(\d{4})""")

    /** Balance after transaction */
    val BALANCE_PATTERN = Regex(
        """(?:(?:avail(?:able)?|bal(?:ance)?)\s*(?:is|:)?\s*(?:Rs\.?|INR|₹)?\s*([\d,]+(?:\.\d{1,2})?))""",
        RegexOption.IGNORE_CASE
    )

    /** UPI transaction reference */
    val UPI_REF = Regex(
        """(?:UPI\s*(?:Ref|Txn|ID|No)\.?\s*:?\s*)([A-Z0-9]{12,})""",
        RegexOption.IGNORE_CASE
    )

    /** Date patterns — DD/MM/YY, DD-MM-YYYY, DD MMM YYYY */
    val DATE_PATTERN = Regex(
        """(\d{2})[/-](\d{2})[/-](\d{2,4})""",
        RegexOption.IGNORE_CASE
    )

    // ─── Known sender IDs ─────────────────────────────────────────────────────

    val SBI_SENDERS = setOf(
        "SBIINB", "SBICRD", "SBIUPI", "SBISMS", "SBIBNK", "SBIIRB",
        "SBI", "SBIBLR", "SBIMUM", "SBIDEL", "SBIBOM",
        "AM-SBIINB", "VM-SBIINB", "BP-SBIINB", "JK-SBIINB", "TN-SBIINB"
    )

    val BOB_SENDERS = setOf(
        "BOBIBD", "BOBSMS", "BOBCRD", "BOBIBD", "BANKOB",
        "AM-BOBIBD", "VM-BOBIBD", "BP-BOBIBD"
    )

    val LIC_SENDERS = setOf(
        "LICIND", "LICINDIA", "LIC", "LICIOF", "LICPOL",
        "AM-LICIND", "VM-LICIND"
    )

    // ─── Helper functions ─────────────────────────────────────────────────────

    /**
     * Parses "1,23,456.78" or "1234.56" to Double.
     * Returns null if not a valid number.
     */
    fun parseAmount(raw: String): Double? {
        return raw.replace(",", "").toDoubleOrNull()
    }

    /**
     * Checks if sender matches known SBI senders.
     */
    fun isSbiSender(sender: String): Boolean =
        SBI_SENDERS.any { sender.contains(it, ignoreCase = true) }

    fun isBobSender(sender: String): Boolean =
        BOB_SENDERS.any { sender.contains(it, ignoreCase = true) }

    fun isLicSender(sender: String): Boolean =
        LIC_SENDERS.any { sender.contains(it, ignoreCase = true) }
}
