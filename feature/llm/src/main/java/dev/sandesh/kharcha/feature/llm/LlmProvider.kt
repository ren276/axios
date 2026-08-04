package dev.sandesh.kharcha.feature.llm

import dev.sandesh.kharcha.core.model.*

/**
 * LLM provider interface — unified contract for all LLM backends.
 *
 * Implementations:
 *  - [OnDeviceGemmaProvider] — Gemma 3n E2B via LiteRT-LM (default, on-device)
 *  - [GeminiFlashProvider] — Gemini 2.5 Flash via Google AI (cloud, OFF by default)
 *  - [GrokProvider] — Grok via xAI (cloud, OFF by default)
 *
 * Per ARCHITECTURE.md §4: "only derived, already-anonymized fields are ever sent if
 * the cloud path is enabled" — implementations must enforce this contract.
 *
 * CRITICAL SECURITY RULE: No raw SMS text, account numbers, or full phone numbers
 * may be passed to cloud providers, ever. Only amount, category guess, and anonymized
 * merchant strings.
 */
interface LlmProvider {

    /**
     * Extract structured transaction data from an SMS body.
     * For cloud providers: MUST anonymize before sending.
     */
    suspend fun extractTransaction(smsText: String): LlmExtractionResult

    /**
     * Classify a merchant string into a category.
     * Only merchant name (normalized) is passed — no amounts, no account info.
     */
    suspend fun categorize(merchantName: String, context: String = ""): Long // Returns category ID

    /**
     * Convert a natural-language query to a structured query spec.
     * LLM PLANS the query — app EXECUTES it against Room.
     * LLM never computes numbers or receives raw transaction data.
     */
    suspend fun naturalLanguageToQuery(userQuery: String): StructuredQuery

    /**
     * Generate a monthly narrative summary.
     * Only aggregated numbers (totals, deltas) are passed — never raw transactions.
     */
    suspend fun generateMonthlySummary(data: MonthlySummary): String
}
