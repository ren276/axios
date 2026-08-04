package dev.sandesh.kharcha.feature.llm

import dev.sandesh.kharcha.core.model.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device Gemma 3n E2B provider via LiteRT-LM.
 *
 * STATUS: LiteRT-LM is not yet available as a standard Maven dependency.
 * This implementation provides a STUB that routes all calls to the "needs review" path.
 * When LiteRT-LM becomes available:
 *  1. Add the Gradle dependency in feature/llm/build.gradle.kts
 *  2. Replace the stub implementations below with real LiteRT-LM API calls
 *  3. The LlmProvider interface contract remains unchanged — no other code changes needed
 *
 * Model: Gemma-3n E2B (~1-2GB on-device, downloaded once at first run)
 * Runtime: LiteRT-LM (successor to MediaPipe LLM Inference API)
 * Backend: CPU/GPU (automatic selection by runtime)
 */
@Singleton
class OnDeviceGemmaProvider @Inject constructor(
    private val promptTemplates: PromptTemplates,
    private val jsonValidator: JsonSchemaValidator
) : LlmProvider {

    private val json = Json { ignoreUnknownKeys = true }

    // TODO: Replace with actual LiteRT-LM session when SDK is available
    // private var session: LlmInferenceSession? = null

    override suspend fun extractTransaction(smsText: String): LlmExtractionResult {
        // STUB: Return a "not valid" result so the ingestion layer routes to NEEDS_REVIEW
        // Real implementation would call:
        // val prompt = promptTemplates.extractionPrompt(smsText)
        // val response = session!!.generateResponse(prompt)
        // return jsonValidator.parseExtractionResult(response)
        return LlmExtractionResult(
            amount = null,
            type = null,
            merchantRaw = null,
            accountHint = null,
            timestamp = null,
            confidence = 0.0f,
            isValid = false,
            errorMessage = "LiteRT-LM not yet initialized — falling back to NEEDS_REVIEW"
        )
    }

    override suspend fun categorize(merchantName: String, context: String): Long {
        // STUB: Return MISC category
        return dev.sandesh.kharcha.core.model.CategoryIds.MISC
    }

    override suspend fun naturalLanguageToQuery(userQuery: String): StructuredQuery {
        // STUB: Return an empty query covering current month
        val now = System.currentTimeMillis()
        val monthStart = getMonthStartMs(now)
        return StructuredQuery(
            fromTimestamp = monthStart,
            toTimestamp = now,
            aggregation = AggregationType.SUM
        )
    }

    override suspend fun generateMonthlySummary(data: MonthlySummary): String {
        // STUB: Generate a basic summary without LLM
        return buildString {
            appendLine("${data.month}/${data.year} Summary")
            appendLine("Total Income: ₹${String.format("%.2f", data.totalIncome)}")
            appendLine("Total Expenses: ₹${String.format("%.2f", data.totalExpense)}")
            appendLine("Net: ₹${String.format("%.2f", data.totalIncome - data.totalExpense)}")
            if (data.categoryBreakdown.isNotEmpty()) {
                appendLine("Top categories:")
                data.categoryBreakdown.entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .forEach { (cat, amt) -> appendLine("  $cat: ₹${String.format("%.2f", amt)}") }
            }
        }
    }

    private fun getMonthStartMs(nowMs: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = nowMs
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
