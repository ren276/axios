# PRD — Personal Finance & Expense Tracker (codename: "Kharcha")
Owner: Sandesh Verma | Type: Personal-use Android app | Status: v1 build-ready

## 1. Purpose
A private, fully on-device expense tracker that auto-reads bank/UPI SMS (SBI, Bank of Baroda, LIC premium notices, family accounts) and turns them into categorized transactions, budgets, and insights — with zero data leaving the device. Built as a personal tool, not a Play Store product, so we deliberately ignore Play Store compliance constraints and lean into what a sideloaded app can do.

## 2. Non-goals (explicitly out of scope for v1)
- No backend server, no cloud sync, no multi-user accounts
- No lending/credit products (this is the thing we're explicitly avoiding — Axio's model)
- No Play Store distribution — sideloaded APK only, installed on personal device(s)
- No iOS
- No bill-split-with-contacts networking (kept as a local calculator only)
- No cloud LLM calls in the default path (Gemini/Grok kept as optional, off-by-default fallback)

## 3. Target device / environment
- Primary: Pixel 9 Pro (per existing dev setup), minSdk 26/27 per existing SaMD project convention — confirm minSdk 26 unless the LLM runtime needs higher
- Android 15+ restricted-settings flow applies (see SECURITY.md) — one-time manual permission unlock is expected and documented in-app

## 4. Users / personas
Single user: Sandesh. Secondary implicit "read-only awareness": family accounts (Bank of Baroda) may be tracked if their SMS land on the same device/SIM — treat as another account under the same local user, not a separate profile system.

## 5. Feature list (v1 — build all of these)

### 5.1 SMS/Notification Ingestion
- F1. `BroadcastReceiver` for real-time incoming SMS (`RECEIVE_SMS`)
- F2. One-time bulk historical import on first launch (`READ_SMS`, last 6–12 months, user-configurable)
- F3. `NotificationListenerService` as secondary ingestion path (catches UPI app notifications — GPay/PhonePe — when bank SMS is delayed or blocked)
- F4. Bank-specific parsers, in priority order: **SBI (debit/UPI)**, **Bank of Baroda**, **LIC** (premium due/paid notices — these are irregular in format, treat as lower-confidence template)
- F5. Generic fallback parser using common regex patterns (amount, debit/credit keyword, account last-4, balance) for any 5th unrecognized sender
- F6. Duplicate detection across SMS + notification sources (dedupe key: amount + timestamp window + account last-4)
- F7. Manual correction UI — tap any transaction, fix amount/category/merchant/account, correction feeds a local override table (not a training loop for v1 — see BUILD_PLAN for v2 idea)
- F8. Parser confidence score per transaction; anything below threshold routes to Tier 2 (on-device LLM) or a "needs review" inbox

### 5.2 On-device Intelligence (Gemma 3n E2B via LiteRT-LM)
- F9. Fallback structured extraction for unparsed/low-confidence SMS
- F10. Merchant name normalization ("SWIGGY*BLR0234" → "Swiggy")
- F11. Category classification (food, travel, subscriptions, rent, LIC/insurance, family transfer, utilities, shopping, medical, misc)
- F12. Natural-language query over local transaction data ("how much did I spend on food last month")
- F13. Auto-generated monthly narrative summary
- F14. Subscription/recurring-payment detection (same merchant + same amount within tolerance, recurring interval)
- F15. Anomaly flagging (spend significantly outside historical pattern for that category)
- F16. Budget-pace prediction (projected month-end spend per category based on current velocity)
- F17. Optional cloud fallback toggle (Gemini Flash / Grok) — OFF by default, and even when ON, only derived fields (merchant, amount, category-so-far) are sent, never raw SMS text

### 5.3 Accounts & Balances
- F18. Multi-account dashboard, auto-created from parsed SMS (masked account/card number, running balance)
- F19. Manual balance override/correction
- F20. Net-worth-lite view (sum of tracked account balances; no investment valuation in v1)

### 5.4 Budgeting
- F21. Category-wise monthly budgets, editable
- F22. Progress bar/ring per category (green/amber/red thresholds at 50/80/100%)
- F23. Local push notification at 50%/80%/100% of category budget
- F24. Toggle: rolling budget vs fixed monthly reset

### 5.5 Bills & Reminders
- F25. Auto-detected due dates (credit card statement due, LIC premium due) parsed from SMS where present
- F26. Manual bill/reminder creation
- F27. WorkManager-scheduled local notifications for reminders

### 5.6 Reports & Export
- F28. CSV export by date range
- F29. PDF export (styled report, not raw dump) by date range
- F30. Category breakdown chart (pie + bar), weekly/monthly/yearly toggle
- F31. Cash flow view (income vs expense over time, line/bar)
- F32. Top-merchant leaderboard
- F33. Custom tags on transactions (e.g. "Goa trip") with tag-filtered reports

### 5.7 Manual Entry
- F34. Manual transaction entry (cash spends SMS can't catch)
- F35. Recurring manual entries (e.g. rent paid in cash monthly)
- F36. Local-only bill-split calculator ("my share" math, no contact sync)

### 5.8 Security & Privacy (see SECURITY.md for full detail)
- F37. Room DB encrypted with SQLCipher
- F38. Biometric/PIN app-lock on launch and on resume-from-background
- F39. Local encrypted backup/restore (file export, user-controlled, no auto-cloud)
- F40. Full data wipe/export option in settings

### 5.9 UX/UI
- F41. Bottom nav: Home / Transactions / Budget / Insights / Profile
- F42. Home = month snapshot, recent transactions, budget health, quick-add FAB
- F43. Material 3 + Compose, dynamic color optional, native dark mode
- F44. Merchant category icons (static local icon set, no network fetch)
- F45. Skeleton loaders / empty states for every screen — no jank, no blank white flashes
- F46. Home-screen widget: balance + this-month-spend at a glance
- F47. Onboarding flow that walks through the Android 15 restricted-settings unlock step explicitly (screenshots/inline instructions), since this is a real friction point on first install

## 6. Explicitly deferred to v2 (documented so nothing is lost, not built now)
- Investment/mutual fund SMS tracking
- Separate loan/EMI amortization tracker
- Multi-currency
- Voice quick-entry
- Parser self-improvement loop from manual corrections
- ATM locator (skip permanently — low value per research)

## 7. Success criteria for v1
- 95%+ of real SBI/BoB SMS parsed correctly without LLM fallback (regex-first should dominate)
- LLM fallback handles the remaining unrecognized/irregular formats (LIC especially) with reasonable accuracy, reviewed manually for first month
- Zero network calls containing raw SMS content
- App cold-start to Home screen under 1s on Pixel 9 Pro
- No crashes across a full month of real daily use

## 8. Open decisions to confirm before/at build start
- Exact minSdk (26 vs 27) — check Gemma 3n / LiteRT-LM minimum requirement, may force higher minSdk than your SaMD app
- Widget: implement in v1 or defer to v1.1
- Whether LIC SMS volume is high enough to justify a dedicated parser vs always routing to LLM tier
