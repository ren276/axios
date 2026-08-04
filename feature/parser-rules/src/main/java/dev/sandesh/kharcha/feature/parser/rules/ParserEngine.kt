package dev.sandesh.kharcha.feature.parser.rules

import dev.sandesh.kharcha.core.model.*


/**
 * Parser orchestration engine.
 * Tries parsers in priority order: SBI → BoB → LIC → Generic
 * Returns the first result with confidence ≥ threshold.
 *
 * Does NOT handle LLM routing — that's done by the ingestion layer
 * based on ParseResult.confidence.
 */
class ParserEngine(
    private val parsers: List<BankParser> = listOf(
        SbiParser(),
        BobParser(),
        LicParser(),
        GenericFallbackParser(),
    )
) {

    /**
     * Parse an SMS message.
     * Returns the best ParseResult from all parsers.
     * If no parser reaches HIGH_CONFIDENCE_THRESHOLD, returns the highest-confidence result.
     */
    fun parse(sender: String, body: String): ParseResult {
        // Skip obvious non-transaction SMS early (OTPs, promotional)
        if (isLikelyOtp(body)) return ParseResult(0f, null, body, "SKIP_OTP")
        if (isLikelyPromotional(body)) return ParseResult(0f, null, body, "SKIP_PROMO")

        val results = parsers.mapNotNull { parser ->
            val result = parser.parse(sender, body)
            if (result.confidence > 0f) result else null
        }

        if (results.isEmpty()) return ParseResult(0f, null, body, "NO_MATCH")

        // Return the highest confidence result
        return results.maxByOrNull { it.confidence }!!
    }

    private fun isLikelyOtp(body: String): Boolean {
        val otpPattern = Regex(
            """(?:OTP|one.time\s+pass(?:word|code)|verification\s+code|is\s+your\s+(?:otp|pass))""",
            RegexOption.IGNORE_CASE
        )
        return otpPattern.containsMatchIn(body) && body.length < 200
    }

    private fun isLikelyPromotional(body: String): Boolean {
        val promoPattern = Regex(
            """(?:offer|discount|sale|cashback\s+offer|click\s+here|congratulations\s+you\s+(?:have\s+)?won|unsubscribe|reply\s+stop)""",
            RegexOption.IGNORE_CASE
        )
        // Only flag as promotional if no amount is present
        return promoPattern.containsMatchIn(body) &&
                !ParserConstants.AMOUNT_PATTERN.containsMatchIn(body) &&
                !ParserConstants.DEBIT_KEYWORDS.containsMatchIn(body)
    }
}
