package dev.sandesh.kharcha.feature.llm

import dev.sandesh.kharcha.core.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prompt templates for all LLM interactions.
 * Structured to enforce JSON output schema — rejects open-ended generation.
 * Per ARCHITECTURE.md §4: "validate before accepting — reject/flag malformed output"
 */
@Singleton
class PromptTemplates @Inject constructor() {

    /**
     * Structured extraction prompt.
     * Constrains output to strict JSON schema.
     * NEVER includes full account numbers, phone numbers, or personal identifiers.
     */
    fun extractionPrompt(smsBody: String): String = """
You are a bank SMS parser. Extract transaction details from the following SMS message.

SMS: "$smsBody"

Respond with ONLY a JSON object in this exact format (no markdown, no explanation):
{
  "amount": <number or null>,
  "type": <"DEBIT" or "CREDIT" or null>,
  "merchantRaw": <"merchant name string" or null>,
  "accountHint": <"last 4 digits string" or null>,
  "timestamp": <unix epoch milliseconds or null>,
  "confidence": <float 0.0 to 1.0>
}

Rules:
- If a field cannot be determined, use null
- amount must be a positive number in INR
- type must be exactly "DEBIT" or "CREDIT"
- merchantRaw should be the raw merchant/payee name from the SMS
- accountHint should be the last 4 digits of the account number only
- confidence should reflect how certain you are of the extraction
    """.trimIndent()

    /**
     * Category classification prompt.
     * Closed category list — no open generation.
     */
    fun categorizationPrompt(merchantName: String): String = """
Classify the following merchant/payee into exactly ONE of these categories:
FOOD, TRAVEL, SUBSCRIPTIONS, RENT, INSURANCE, FAMILY_TRANSFER, UTILITIES, SHOPPING, MEDICAL, MISC

Merchant: "$merchantName"

Respond with ONLY the category name, nothing else.
    """.trimIndent()

    /**
     * Natural language to query translation.
     * LLM plans the query — app executes it. LLM never computes numbers.
     */
    fun nlQueryPrompt(userQuery: String): String = """
Convert this natural language query about personal expenses into a structured query spec.

User query: "$userQuery"

Available categories: food, travel, subscriptions, rent, insurance, family_transfer, utilities, shopping, medical, misc
Available aggregations: SUM, COUNT, AVERAGE, LIST

Respond with ONLY a JSON object:
{
  "categoryNames": <array of category names or null for all>,
  "fromDate": <"YYYY-MM-DD" or null>,
  "toDate": <"YYYY-MM-DD" or null>,
  "aggregation": <"SUM" or "COUNT" or "AVERAGE" or "LIST">,
  "transactionType": <"DEBIT" or "CREDIT" or null>
}
    """.trimIndent()

    /**
     * Monthly summary generation.
     * Only aggregated numbers passed — never raw transaction list.
     */
    fun summaryPrompt(data: MonthlySummary): String = """
Generate a brief, friendly financial summary (2-3 sentences) based on these aggregated stats.
Do NOT make up specific transactions. Only reference the numbers provided.

Month: ${data.month}/${data.year}
Total Income: ₹${String.format("%.0f", data.totalIncome)}
Total Expenses: ₹${String.format("%.0f", data.totalExpense)}
Change vs last month: ${data.vsLastMonthPercent.let { if (it > 0) "+${it.toInt()}%" else "${it.toInt()}%" }}
Top spending categories: ${data.categoryBreakdown.entries.sortedByDescending { it.value }.take(3).joinToString(", ") { "${it.key}: ₹${String.format("%.0f", it.value)}" }}

Write a helpful 2-3 sentence summary:
    """.trimIndent()
}

/**
 * JSON schema validator for LLM output.
 * Per ARCHITECTURE.md §4: "reject malformed output rather than guessing"
 */
@Singleton
class JsonSchemaValidator @Inject constructor() {

    private val json = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseExtractionResult(rawOutput: String): LlmExtractionResult {
        return try {
            // Strip any markdown code fences if model ignores instructions
            val cleaned = rawOutput
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val result = json.decodeFromString<LlmExtractionResult>(cleaned)

            // Validate required constraints
            val amount = result.amount
            if (amount != null && amount <= 0) {
                return result.copy(isValid = false, errorMessage = "Invalid amount: $amount")
            }

            result.copy(isValid = true)
        } catch (e: Exception) {
            LlmExtractionResult(
                amount = null, type = null, merchantRaw = null, accountHint = null, timestamp = null,
                confidence = 0f, isValid = false, errorMessage = "Parse error: ${e.message}"
            )
        }
    }

    fun parseCategoryResult(rawOutput: String): String? {
        val cleaned = rawOutput.trim().uppercase()
        val validCategories = setOf("FOOD", "TRAVEL", "SUBSCRIPTIONS", "RENT", "INSURANCE",
            "FAMILY_TRANSFER", "UTILITIES", "SHOPPING", "MEDICAL", "MISC")
        return if (cleaned in validCategories) cleaned else null
    }
}
