package dev.sandesh.kharcha.feature.ingestion.service

import dev.sandesh.kharcha.core.model.*
import dev.sandesh.kharcha.feature.parser.rules.ParserConstants
import dev.sandesh.kharcha.feature.parser.rules.ParserEngine
import dev.sandesh.kharcha.feature.llm.LlmProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core ingestion service — coordinates parsing, dedup, account resolution, and DB storage.
 *
 * Flow per ARCHITECTURE.md §3:
 *  SMS/Notification → ParserEngine → confidence check → (CONFIRMED | LLM tier | NEEDS_REVIEW)
 *  → dedupe check → insert to Room
 */
@Singleton
class IngestionService @Inject constructor(
    private val parserEngine: ParserEngine,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val llmProvider: LlmProvider,
) {

    /**
     * Process a single incoming or historical SMS.
     * Returns true if a transaction was created (not a duplicate, not skipped).
     */
    suspend fun processSms(
        sender: String,
        body: String,
        isHistorical: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        val parseResult = parserEngine.parse(sender, body)

        // No match or OTP/promo skip
        val parsedTxn = parseResult.transaction ?: return@withContext false
        if (parseResult.confidence <= 0f) return@withContext false

        // Resolve or create account
        val accountId = resolveAccount(sender, parsedTxn)

        // Dedupe check: same amount + account + timestamp within ±2 minutes
        val dupeWindow = 2 * 60 * 1000L
        val existingDupe = transactionRepository.checkDuplicate(
            amount = parsedTxn.amount,
            accountLast4 = extractLast4FromSender(sender),
            timestampFrom = parsedTxn.timestamp - dupeWindow,
            timestampTo = parsedTxn.timestamp + dupeWindow
        )
        if (existingDupe != null) return@withContext false // Deduplicated

        // Determine final status
        val finalTxn = when {
            parseResult.confidence >= ParserConstants.HIGH_CONFIDENCE_THRESHOLD -> {
                // High confidence → auto-confirm, schedule raw SMS purge
                parsedTxn.copy(
                    accountId = accountId,
                    status = TransactionStatus.CONFIRMED
                )
            }
            parseResult.confidence >= ParserConstants.MIN_CONFIDENCE_THRESHOLD -> {
                // Low confidence → try LLM tier
                val llmResult = tryLlmExtraction(parsedTxn, body)
                llmResult?.copy(accountId = accountId) ?: parsedTxn.copy(
                    accountId = accountId,
                    status = TransactionStatus.NEEDS_REVIEW
                )
            }
            else -> {
                // Too low confidence → straight to needs review
                parsedTxn.copy(
                    accountId = accountId,
                    status = TransactionStatus.NEEDS_REVIEW
                )
            }
        }

        val insertedId = transactionRepository.insertTransaction(finalTxn)

        // If CONFIRMED, schedule raw SMS purge immediately
        if (finalTxn.status == TransactionStatus.CONFIRMED && insertedId > 0) {
            transactionRepository.purgeRawSmsText(insertedId)
        }

        return@withContext insertedId > 0
    }

    /**
     * Process a UPI notification (secondary ingestion path).
     * Uses generic parser + LLM for extraction since notification format varies wildly.
     */
    suspend fun processNotification(
        packageName: String,
        notificationBody: String
    ): Boolean = withContext(Dispatchers.IO) {
        // Map package name to a pseudo-sender for the generic parser
        val pseudoSender = when {
            packageName.contains("google") -> "GPAY"
            packageName.contains("phonepe") -> "PHONEPE"
            packageName.contains("paytm") -> "PAYTM"
            else -> "UPI_APP"
        }

        val parseResult = parserEngine.parse(pseudoSender, notificationBody)

        val parsedTxn = parseResult.transaction ?: return@withContext false
        if (parseResult.confidence <= 0f) return@withContext false

        val txn = parsedTxn.copy(
            source = TransactionSource.NOTIFICATION,
            status = TransactionStatus.NEEDS_REVIEW // Notification parsing always goes to review
        )

        // Check dedup
        val dupeWindow = 2 * 60 * 1000L
        val existingDupe = transactionRepository.checkDuplicate(
            amount = txn.amount,
            accountLast4 = "",
            timestampFrom = txn.timestamp - dupeWindow,
            timestampTo = txn.timestamp + dupeWindow
        )
        if (existingDupe != null) return@withContext false

        val id = transactionRepository.insertTransaction(txn)
        return@withContext id > 0
    }

    private suspend fun resolveAccount(sender: String, txn: Transaction): Long {
        // TODO: Extract account last4 from txn and look up/create account
        // For now, return a placeholder — full implementation in post-parse enrichment
        return 0L
    }

    private fun extractLast4FromSender(sender: String): String {
        return ParserConstants.ACCOUNT_LAST4_ALT.find(sender)?.groupValues?.get(1) ?: ""
    }

    private suspend fun tryLlmExtraction(baseTxn: Transaction, rawBody: String): Transaction? {
        return try {
            val llmResult = llmProvider.extractTransaction(rawBody)
            val amount = llmResult.amount
            val type = llmResult.type
            if (!llmResult.isValid || amount == null || type == null) return null

            baseTxn.copy(
                amount = amount,
                type = type,
                merchantRaw = llmResult.merchantRaw ?: baseTxn.merchantRaw,
                merchantNormalized = llmResult.merchantRaw ?: baseTxn.merchantNormalized,
                parseConfidence = llmResult.confidence,
                status = TransactionStatus.LLM_PARSED
            )
        } catch (e: Exception) {
            null
        }
    }
}
