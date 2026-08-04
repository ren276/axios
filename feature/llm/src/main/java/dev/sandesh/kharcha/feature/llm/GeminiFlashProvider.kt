package dev.sandesh.kharcha.feature.llm

import dev.sandesh.kharcha.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini Flash cloud LLM provider — OFF by default.
 *
 * SECURITY CONTRACT (ARCHITECTURE.md §4, SECURITY.md §5):
 * - NEVER sends raw SMS text to the cloud
 * - NEVER sends account numbers, phone numbers, or personal identifiers
 * - ONLY sends: derived merchant name, amount, best-guess category
 * - Triggered ONLY when user explicitly enables cloud fallback in Settings
 *
 * API key stored in DataStore, not hardcoded.
 */
@Singleton
class GeminiFlashProvider @Inject constructor(
    private val promptTemplates: PromptTemplates,
    private val jsonValidator: JsonSchemaValidator
) : LlmProvider {

    // TODO: Inject DataStore for API key + user preference
    // private val apiKey: String get() = dataStore.getApiKey()

    override suspend fun extractTransaction(smsText: String): LlmExtractionResult {
        // SECURITY: This provider should NEVER be called with raw SMS text for extraction.
        // The ingestion layer must pre-process and anonymize before calling cloud providers.
        // Return invalid to force on-device path for extraction.
        return LlmExtractionResult(
            amount = null, type = null, merchantRaw = null, accountHint = null, timestamp = null,
            confidence = 0f, isValid = false,
            errorMessage = "Gemini Flash provider does not handle raw SMS extraction for security. Use OnDeviceGemmaProvider."
        )
    }

    override suspend fun categorize(merchantName: String, context: String): Long =
        withContext(Dispatchers.IO) {
            // TODO: Call Gemini Flash API with anonymized merchant name only
            // val response = geminiClient.generateContent(promptTemplates.categorizationPrompt(merchantName))
            // return@withContext categoryNameToId(jsonValidator.parseCategoryResult(response.text))
            CategoryIds.MISC // Stub
        }

    override suspend fun naturalLanguageToQuery(userQuery: String): StructuredQuery =
        withContext(Dispatchers.IO) {
            // TODO: Call Gemini Flash API with user query only (no transaction data)
            val now = System.currentTimeMillis()
            StructuredQuery(fromTimestamp = null, toTimestamp = now, aggregation = AggregationType.SUM)
        }

    override suspend fun generateMonthlySummary(data: MonthlySummary): String =
        withContext(Dispatchers.IO) {
            // TODO: Call Gemini Flash API with aggregated data only (never raw transactions)
            // val response = geminiClient.generateContent(promptTemplates.summaryPrompt(data))
            // return@withContext response.text ?: "Summary unavailable"
            "Cloud summary unavailable — Gemini Flash provider not yet configured."
        }
}

/**
 * Hilt DI module for LLM providers.
 * The active provider is determined by user settings (DataStore).
 * Default: OnDeviceGemmaProvider.
 */
