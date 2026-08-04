# Build Plan — Kharcha
For use by Claude Code (or Gemini CLI equivalent) to execute autonomously, phase by phase. Read `01_PRD.md`, `02_ARCHITECTURE.md`, and `03_SECURITY.md` fully before starting Phase 0.

## Model/effort recommendation per phase
- **Phase 0 (scaffolding) & Phase 1 (DB/models)**: Sonnet, high effort. Mechanical but needs to get the schema right the first time — errors here compound.
- **Phase 2 (parser engine)**: Sonnet, high effort. This is the highest-value, most bug-prone module — real regex against real SMS samples, needs careful testing, not a place to rush.
- **Phase 3 (LLM integration)**: Opus recommended for the initial LiteRT-LM integration design (genuine architecture decision — prompt contracts, JSON schema enforcement, provider abstraction). Drop to Sonnet high once the pattern is established and you're just adding more prompt templates.
- **Phase 4 (UI/Compose screens)**: Sonnet, high effort throughout — Compose work benefits from consistent high-effort reasoning for state management correctness, but isn't architecture-critical enough to need Opus.
- **Phase 5 (security hardening)**: Opus recommended — SQLCipher key management, Keystore usage, FLAG_SECURE placement are the kind of decisions worth the extra rigor.
- **Phase 6 (reports/export, widget, polish)**: Sonnet high, or Haiku for pure repetitive tasks (e.g. generating icon mappings, boilerplate DAO methods) — switch to Haiku mid-session only for clearly mechanical sub-tasks.
- Testing tasks throughout: Sonnet high — test correctness matters as much as feature code here.

## Phase 0 — Project scaffolding
- Multi-module Gradle setup per `02_ARCHITECTURE.md` module list
- Hilt DI graph skeleton
- Compose theme/design system module (Material 3, dark mode, token system)
- CI-equivalent: local Gradle check task, unit test runner wired up
- **Success criteria**: empty app builds and launches to a blank Home screen, all modules compile, Hilt graph resolves with no missing bindings

## Phase 1 — Data layer
- Room entities/DAOs for all tables in `02_ARCHITECTURE.md` §5
- SQLCipher integration + Keystore-derived key setup (see `03_SECURITY.md` §3)
- Repository interfaces + implementations for accounts, transactions, budgets, bills
- Migration strategy documented even for v1 (so v1.1 doesn't break existing local data)
- **Success criteria**: full CRUD unit tests pass against an in-memory/test SQLCipher DB for every entity

## Phase 2 — SMS/notification ingestion + parser engine
- `SmsReceiver` (BroadcastReceiver) + bulk historical import via `Telephony.Sms` content provider
- `NotificationListenerService` implementation
- Bank-specific parsers: **SBI first** (primary daily-use account, build and test this thoroughly before moving on), then **Bank of Baroda**, then **LIC** (expect irregular formats — build with lower default confidence and route more aggressively to LLM tier), then generic fallback parser
- Dedupe logic (SMS vs notification origin)
- Confidence scoring + routing to CONFIRMED vs NEEDS_REVIEW status
- **Testing requirement**: build a local fixture file of anonymized/redacted real SMS samples (strip actual account numbers/amounts, keep structural format) for each bank — parser unit tests run against these fixtures, not live SMS, so tests are reproducible
- **Success criteria**: ≥95% correct structured extraction on the SBI fixture set without LLM involvement; BoB and LIC fixture sets documented with current accuracy even if lower, feeding Phase 3 fallback tuning

## Phase 3 — On-device LLM integration
- LiteRT-LM runtime wrapper, Gemma-3n E2B model download/cache flow (first-run, wifi-gated, storage-confirmed)
- `LlmProvider` interface + `OnDeviceGemmaProvider` implementation
- Structured-extraction prompt + strict JSON schema validation (reject malformed output, don't guess-fill)
- Categorization prompt (closed category list, single-label)
- NL-query-to-structured-query translation (LLM plans the query, app executes against Room, LLM never computes numbers itself)
- Monthly summary generation (aggregated numbers only, never raw transaction list, per `02_ARCHITECTURE.md` §4)
- Optional cloud fallback: `GeminiFlashProvider` / `GrokProvider` implementing same interface, OFF by default per `03_SECURITY.md` §5
- **Success criteria**: LLM tier correctly extracts structured data from the LIC/irregular fixture set with the same reproducible-test approach as Phase 2; provider swap (on-device ↔ cloud) works via a single settings toggle with no code changes elsewhere

## Phase 4 — Core UI (Compose)
- Bottom nav shell: Home / Transactions / Budget / Insights / Profile
- Home: month snapshot, recent transactions, budget health, quick-add FAB, "needs review" inbox surfaced prominently
- Transactions list/detail/manual entry/correction flows
- Accounts dashboard
- Budget CRUD + progress visualization
- Empty states, skeleton loaders for every screen (per PRD F45 — no exceptions)
- Onboarding flow including the explicit Android 15 restricted-settings walkthrough (per `03_SECURITY.md` §2)
- **Success criteria**: every screen has a defined loading/empty/error/populated state, navigation between all five tabs works, no unhandled null states

## Phase 5 — Security hardening pass
- Biometric/PIN lock on cold start + resume-from-background
- FLAG_SECURE on transaction/account screens
- Raw SMS purge-after-confirm logic verified end-to-end
- Local encrypted backup/restore flow
- Full data wipe/export in Settings
- **Success criteria**: manual security checklist in `03_SECURITY.md` §9 fully verified item-by-item, screenshot blocked on sensitive screens confirmed on-device

## Phase 6 — Reports, export, widget, polish
- CSV/PDF export by date range
- Charts: category breakdown, cash flow, top-merchant leaderboard
- Tag system + tag-filtered reports
- Home-screen widget (balance + month spend)
- Bill/reminder notifications via WorkManager
- Subscription/anomaly detection surfaced in Insights tab
- **Success criteria**: exported PDF/CSV opens correctly and matches in-app numbers exactly; widget updates within a reasonable interval after new transactions land

## Phase 7 — Real-world validation (you, not the agent)
- Install on your Pixel 9 Pro, run the Android 15 restricted-settings unlock flow yourself, confirm SMS import works against your real SBI/BoB/LIC messages
- Run for at least 2 weeks of real daily use before trusting budget numbers
- Log any parser misses back into the fixture test set (manually redacted) to strengthen Phase 2 tests over time

## Cross-cutting rules for every phase
- Never commit real SMS content, real account numbers, or real amounts into fixture files, tests, or documentation — always use synthetic or clearly-redacted values
- No feature in this plan sends raw SMS off-device, ever, under any settings combination — this is a hard constraint, not a default
- Every phase ends with unit tests passing before moving to the next phase
- If a phase's success criteria isn't met, stop and fix before proceeding — don't stack unstable phases
