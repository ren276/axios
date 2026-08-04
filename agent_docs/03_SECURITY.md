# Security & Privacy — Kharcha

## 1. Threat model (personal-use app, but treat it seriously — it's financial data)
Primary risks: device theft/loss, another app on the same phone attempting to read exported data, accidental exposure through backups/logs, and (lowest but nonzero risk) a compromised optional cloud LLM call leaking derived data.
Not in scope: multi-tenant server compromise, network MITM on a backend (no backend exists), regulatory/compliance audit (this is not a commercial SaMD-grade requirement — apply good practice, not full IEC/ISO process overhead).

## 2. Android 15+ SMS permission reality (confirmed via research, applies directly to you)
- Android 15+ classifies SMS-related permissions under **restricted settings** for sideloaded apps. `READ_SMS`/`RECEIVE_SMS` are not blocked outright, but the standard permission toggle appears greyed out until the user manually does: **App info → ⋮ menu → "Allow restricted settings" → then grant SMS permission normally.**
- `SEND_SMS` is now whitelisted to system apps only on sideloaded installs — irrelevant to you since this app never sends SMS.
- **Action item**: build an explicit onboarding screen that walks through this exact tap sequence with device-specific notes (Pixel path is straightforward; note that some OEMs like Redmi may hide the menu differently — worth testing since family accounts might involve non-Pixel devices).
- This is a one-time manual step per install, not a recurring friction — document it once, done.

## 3. Data-at-rest
- Room database encrypted with **SQLCipher** (same approach as your SaMD project — reuse that setup directly)
- Encryption key: derived via Android Keystore, not hardcoded, not stored in SharedPreferences in plaintext
- No raw SMS body persisted beyond the "needs review" inbox lifecycle — purge raw text field once a transaction is confirmed/corrected by the user
- Local backup files (export feature) must also be encrypted — do not write plaintext JSON/CSV backups to unencrypted external storage; if exporting to Downloads for the user's own use, warn clearly in-UI that the exported file is unencrypted the moment it leaves the app's private storage

## 4. Data-in-use / app-level access control
- Biometric or PIN lock required on cold start AND on resume from background (use `BiometricPrompt` API, fall back to device PIN)
- Screenshot/screen-recording blocked on sensitive screens (transactions, accounts) via `FLAG_SECURE` on those activities/composable hosts
- Clipboard auto-clear after copying any sensitive value (account number, balance) if you implement copy-to-clipboard anywhere

## 5. Data-in-transit (only relevant if optional cloud LLM fallback is ever enabled)
- Default state: **cloud LLM path OFF**. All processing is on-device via Gemma 3n E2B.
- If a user (you) explicitly enables Gemini Flash / Grok fallback:
  - Only send already-derived fields (amount, category guess, anonymized merchant string) — **never raw SMS text, never account numbers, never phone number**
  - TLS is handled by the SDK/HTTPS by default — no custom cert pinning needed for a personal app calling official Google/xAI endpoints, but do confirm requests go over HTTPS only
  - Log a clear on-screen indicator whenever a cloud call is made (e.g. a small cloud icon on that particular insight), so it's never silent

## 6. Logging & crash reporting
- No raw SMS content, account numbers, or amounts in logs — ever, including debug logs. Use redacted/hashed identifiers if you need to trace parser issues (`account_last4` is acceptable in local-only debug logs since it's already a partial masked value shown in-app)
- No third-party crash reporting SDK by default (Firebase Crashlytics etc. would mean data leaves device) — if you want crash visibility, use local logcat/file logging only, reviewed manually
- Strip any accidental debug logging before treating a build as "daily driver"

## 7. Permissions checklist (request only what's needed, justify each internally even though there's no Play Store review)
| Permission | Purpose | Notes |
|---|---|---|
| `RECEIVE_SMS` | real-time transaction capture | restricted-setting unlock required, Android 15+ |
| `READ_SMS` | one-time historical import | same unlock required |
| `BIND_NOTIFICATION_LISTENER_SERVICE` | secondary ingestion via UPI app notifications | also a "special permission," separate manual grant flow in Settings → Notification access |
| Biometric (`USE_BIOMETRIC`) | app lock | standard runtime permission |
| Storage (scoped, for export) | CSV/PDF export, backup files | use scoped storage / SAF, not broad storage permission |

## 8. Backup & recovery
- Local encrypted export (password-protected archive or app-managed encrypted file) that the user manually moves wherever they want (own Drive, own USB, own choice) — app itself does not auto-upload
- Restore flow validates file integrity/schema version before import, never silently overwrites existing data without confirmation

## 9. Secure-by-default posture summary
Cloud OFF by default → biometric lock on by default → SQLCipher on by default → raw SMS purged after parse-confirm → FLAG_SECURE on sensitive screens by default. Every "more convenient but less private" option (cloud LLM, unencrypted export) should be an opt-in toggle in Settings, never the default state.
