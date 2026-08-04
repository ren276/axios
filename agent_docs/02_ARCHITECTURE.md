# Architecture — Kharcha (Personal Finance Tracker)

## 1. High-level architecture
Fully on-device, no backend. Single Android app, Clean Architecture + MVVM, matching the pattern already used in the PHC/SaMD project (Kotlin, Compose, Hilt, Room+SQLCipher, Coroutines/Flow).

```
UI (Compose) 
  -> ViewModel (per screen, Hilt-injected)
    -> UseCase layer (business logic, testable, no Android deps)
      -> Repository layer (interfaces)
        -> Data sources:
           - Local: Room+SQLCipher (transactions, accounts, budgets, tags, rules)
           - SMS: BroadcastReceiver + ContentResolver (Telephony.Sms)
           - Notifications: NotificationListenerService
           - On-device LLM: LiteRT-LM runtime wrapper
           - Optional cloud LLM: Gemini Flash / Grok clients (behind a feature flag, default OFF)
```

## 2. Module structure (multi-module, mirrors SaMD project conventions)
- `app` — DI graph assembly, navigation host, app-level config
- `core:database` — Room entities, DAOs, SQLCipher setup, migrations
- `core:model` — domain models (Transaction, Account, Budget, Tag, Category)
- `core:designsystem` — Compose theme, tokens, shared components
- `feature:ingestion` — SMS receiver, notification listener, parser engine, dedupe logic
- `feature:parser-rules` — bank-specific regex parsers (SBI, BoB, LIC, generic fallback), unit-testable in isolation
- `feature:llm` — LiteRT-LM wrapper, prompt templates, structured-output parsing, optional cloud fallback clients
- `feature:transactions` — transaction list, detail, manual entry, correction UI
- `feature:budget` — budget CRUD, progress tracking, notification triggers
- `feature:reports` — charts, CSV/PDF export
- `feature:accounts` — account dashboard, balance management
- `feature:settings` — security settings, backup/restore, LLM toggle, data wipe

Rationale for multi-module: the parser-rules module especially benefits from isolation — it needs heavy unit testing against real (redacted) SMS samples without pulling in Android framework deps.

## 3. Data flow: SMS → Transaction
1. SMS arrives → `SmsReceiver` (BroadcastReceiver) or bulk import reads `Telephony.Sms` content provider
2. Raw SMS body + sender ID passed to `ParserEngine`
3. `ParserEngine` tries bank-specific regex parsers in order (SBI → BoB → LIC → generic) — each returns a `ParseResult(confidence: Float, transaction: Transaction?)`
4. If confidence ≥ threshold (e.g. 0.85) → transaction auto-saved to Room, status = CONFIRMED
5. If confidence < threshold → route to LLM tier:
   - `LlmParser` (on-device Gemma 3n E2B via LiteRT-LM) receives the raw SMS text with a structured-extraction prompt, returns JSON
   - Result saved with status = LLM_PARSED, surfaced in a "Review" inbox on Home for one-tap confirm/correct
6. Dedupe check runs before final insert: match on (amount, account_last4, timestamp within ±2 min window) against both SMS-origin and notification-origin candidates
7. Post-insert: category classification (regex merchant dictionary first, LLM fallback for unknown merchants), subscription/anomaly checks run async via WorkManager

## 4. On-device LLM integration (Gemma 3n E2B, LiteRT-LM)
- Model: pre-converted `.task`/`.litertlm` variant of Gemma-3n E2B pulled once from LiteRT Community (Hugging Face) at first-run setup, cached locally (~1-2GB on device storage — flag this to user during onboarding, needs wifi + storage confirmation)
- Runtime: LiteRT-LM (successor to MediaPipe LLM Inference API, which is now maintenance-only — use LiteRT-LM directly, not the older API)
- Backend: CPU/GPU (MediaPipe/LiteRT does not expose direct NPU selection — acceptable for this use case, latency isn't critical)
- Usage patterns:
  - **Structured extraction**: prompt constrains output to strict JSON schema (amount, type, merchant, account_hint, date), validate before accepting — reject/flag malformed output rather than guessing
  - **Categorization**: single-label classification prompt, closed category list passed in-context, not open generation
  - **NL query**: two-step — LLM converts user's natural language question into a structured local query spec (date range + category + aggregation), app executes it against Room DB directly (never let the LLM "make up" numbers — it plans the query, doesn't compute the answer)
  - **Summary generation**: LLM receives only aggregated numbers (category totals, deltas vs last month) — never raw transaction list — to keep prompts small and avoid ever exposing raw SMS content even locally in logs
- Fallback cloud path (Gemini Flash / Grok): same JSON-contract interface (`LlmProvider` interface with `GeminiFlashProvider`/`GrokProvider`/`OnDeviceGemmaProvider` implementations) so swapping is trivial; only derived, already-anonymized fields are ever sent if this path is enabled

## 5. Storage schema (core tables)
- `accounts` (id, bank_name, account_last4, type[BANK/CARD/WALLET], current_balance, updated_at)
- `transactions` (id, account_id, amount, type[DEBIT/CREDIT], merchant_raw, merchant_normalized, category_id, txn_ref, timestamp, source[SMS/NOTIFICATION/MANUAL], parse_confidence, status[CONFIRMED/LLM_PARSED/NEEDS_REVIEW], is_duplicate_of, tags)
- `categories` (id, name, icon_ref, is_custom)
- `budgets` (id, category_id, amount, period[MONTHLY/ROLLING], start_date)
- `bills` (id, name, due_date, amount, is_recurring, recurrence_rule, linked_account_id)
- `parser_rules` (versioned locally, allows future rule updates without full app rebuild — optional v1.1)

All tables live inside the single SQLCipher-encrypted Room database. No table stores raw SMS text long-term — parse the SMS, store structured fields, discard/short-retain the raw body only for the "needs review" inbox (purge raw text once user confirms/corrects).

## 6. Tech stack summary
| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Async | Coroutines + Flow |
| Local DB | Room + SQLCipher |
| On-device LLM | Gemma 3n E2B via LiteRT-LM |
| Optional cloud LLM | Gemini 2.5 Flash / Grok (behind feature flag) |
| Charts | Compose-native charting (e.g. Vico or custom Canvas) — avoid heavy unmaintained libs |
| Export | Local PDF generation (Android PdfDocument API) + CSV writer |
| Background work | WorkManager |
| Notifications | NotificationCompat + WorkManager-scheduled triggers |
| Testing | JUnit (parser rules especially need strong coverage), Compose UI tests |

## 7. Why no backend
Confirmed with you: fully on-device only. This removes an entire class of concerns (auth, API security, server costs, sync conflict resolution) and is strictly better for a single-user privacy-sensitive personal finance tool. Local encrypted backup/restore via file export covers the "what if I lose my phone" case without needing cloud infrastructure.
