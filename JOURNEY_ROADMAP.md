# Within — Journey Build Roadmap

> **Purpose:** execution plan for turning the 30-day journey into the premium product.
> Written as a handoff: a fresh session can resume from this file alone.
> **Companion docs:** `JOURNEY_ENGINE.md` (architecture/why), `PLAN.md` (product/business), `content/journal_30day_en.md` (the manuscript = content source of truth).

---

## How to resume in a new session (read this first)

1. The project memories auto-load and orient you: **journey pivot**, **code conventions**, **workflow prefs**. Trust them.
2. Skim `JOURNEY_ENGINE.md` for the architecture (the `DailyContentResolver` idea, Room, paced unlock).
3. Copy patterns from **`data/repository/MessageRepository.kt`** — it is the template for content/data code (Gson + `@SerializedName` DTOs + `@Inject constructor` + lazy load + DTO→domain map). Enums carry a `key` (`Category.kt`, `Tier`).
4. **Ask the user before running long Gradle builds** — they usually build in Android Studio.

### Where we are now (Phase 0 + 1 ✅ + Phase 2 ✅ + Phase 3 partial + Phase 4 partial)
> **Build status:** Phases 0–1 compiled clean previously. Phase 2/3/4 changes below are **code-complete but not yet built** — Phase 3 added the **Google Play Billing dependency**, so a Gradle sync + build in Android Studio is required next.
- **Phase 2 ✅ (code-complete):** `JourneyMapScreen` (+VM), `ReflectionsScreen` (+VM), and the post-completion `CelebrationOverlay` (Ori `gratitude` + streak). Two quiet links on the Today screen; routes `journey/map` + `journey/reflections`.
- **Phase 3 (2 of 4):** ✅ **Google Play Billing** — `billing/BillingManager.kt` owns the `BillingClient` (connect/restore/purchase/acknowledge) and writes entitlement into `UserPreferences.isPremium`, so the rest of the app is unchanged; started from `MainActivity`; real plans + live prices in `PaywallSheet`; `BILLING` permission + product ids in `Pricing.kt` (`within_premium` / `annual` / `monthly` — **must be created in Play Console**). ✅ **Content catalog** — `JourneyRepository` is now a locale-aware catalog keyed by id (`getJourney(id)`, `getDay(id,day)`, `catalog()`); all call sites pass `journeyId`. ☐ `JourneyLibraryScreen` + ☐ personalization remain (both want journey #2 authored first).
- **Phase 4 (1 done + 1 scaffolded):** ✅ **Shareable reaffirmation cards** — `share/ShareCardRenderer.kt` draws a branded 1080² PNG (Android Canvas, version-safe), shared via FileProvider (`shareReaffirmation`); share button on `ReaffirmationCard`. ◐ **pt-BR scaffolded** — `values-pt-rBR/strings.xml` fully synced + translated (96/96 keys), stat labels moved to resources, generator phase titles locale-aware; only the manuscript translation + `journey_pt_br.json` + uncommenting `rawByLocale["pt"]` remain. ☐ analytics, ☐ store listing remain.

### Earlier baseline (Phase 0 + Phase 1 ✅ BUILD-COMPLETE)
Compiles clean (`JAVA_HOME=~/android-studio/jbr ./gradlew :app:compileDebugKotlin`); not yet run on a device.
- **Phase 0:** `res/raw/journey_en.json` (id `coming-home`, 30 days, 4 phases) + `data/model/Journey.kt` + `data/repository/JourneyRepository.kt`. *(Fixed a JVM clash found on first compile: a `private val journey` getter collided with `fun getJourney()` → backing field renamed `cachedJourney`.)*
- **Phase 1:** Room persistence, progress/journaling repo, day-8 paywall, journey UI (Today + Day), journey-as-home, onboarding reframed to auto-start the journey, and the notification resolver — all detailed (☑) in the Phase 1 checklist below.

### Decisions — LOCKED (2026-06-24) ✅
All confirmed with the user; the build reflects these:
- [x] **Home screen:** the journey *replaces* the home when a journey is active; the dark message `HomeScreen` is no longer routed (kept only as a silent notification fallback). *(User called the old home "dogpoop" — onboarding's cream aesthetic is now the design north star; see memory `within-design-north-star`.)*
- [x] **Journaling:** optional but encouraged — completion never requires an entry; the field autosaves whenever used.
- [x] **Pacing:** paced + catch-up — `availableDay = min(lengthDays, today − startedEpochDay + 1)`.
- [x] **Pricing/packaging:** annual ~$59.99 + monthly ~$8.99 + 7-day trial, as placeholder copy in `billing/Pricing.kt` (change there; real prices land with billing).
- [x] **Billing timing:** `isPremium` stubbed for Phase 1; the day-8 paywall logs taps (`recordPaywallTap`) as a free willingness-to-pay signal. **Pulling Google Play Billing forward is the recommended next step** (WTP is the riskiest assumption for the MRR goal).

---

## Phase 1 — Monetizable core ✅ BUILD-COMPLETE (2026-06-24)

**Goal (met in code):** start the journey, read + journal each day, days unlock daily, day 8 hits a paywall, and the daily notification delivers the journey prompt. `isPremium` is stubbed (real billing = Phase 3 / pull forward).

### 1.1 Persistence — Room ☑
- [x] Room in `gradle/libs.versions.toml` + `app/build.gradle.kts` (`room-runtime` + `room-ktx` impl, `room-compiler` via **ksp**). Room 2.6.1.
- [x] `data/local/entity/JournalEntry.kt` — `@Entity(primaryKeys=["journeyId","day","field"])`: `journeyId, day, field, text, updatedAt`.
- [x] `data/local/entity/JourneyProgress.kt` — `@Entity(primaryKeys=["journeyId"])`: `journeyId, startedEpochDay, status`. *(Dropped `currentDay`/`lastCompletedDay` from the sketch — both derivable, avoids persisted-state drift.)*
- [x] **`data/local/entity/DayProgress.kt` — added beyond the sketch** — `@Entity(primaryKeys=["journeyId","day"])`: `completed, mindfulActionDone, completedAt`. Needed because journaling is optional (completion ≠ a journal entry) and the mindful action is independently checkable; also powers the streak.
- [x] DAOs: `JournalEntryDao`, `JourneyProgressDao`, `DayProgressDao` (suspend fns + `Flow` reads).
- [x] `data/local/WithinDatabase.kt` — `@Database(version=1)`, 3 entities, DB `within.db`.
- [x] `di/DatabaseModule.kt` — `@Provides @Singleton` the DB + each DAO.

### 1.2 Progress + journaling logic ☑
- [x] `data/repository/JourneyProgressRepository.kt` (content stays read-only in `JourneyRepository`): `startJourney`, `availableDay` (`min(len, today−start+1)`), `isUnlocked`, `isCompleted`/`markCompleted`, `setMindfulActionDone`, `saveEntry`/`entriesFor`, `currentStreak`, `completedDays`. Stateless — every method takes an explicit journeyId.
- [x] `UserPreferences` — added `activeJourneyId`, `isPremium` (stub), and `recordPaywallTap` (local WTP signal).

### 1.3 Paywall gate ☑
- [x] `JourneyRepository.canAccessDay(day, isPremium)` = `isPremium || isDayFree(day)`. Day 8 is the trigger. `unlockStub()` (sets premium) for testing; pricing copy lives in `billing/Pricing.kt`.

### 1.4 UI — `ui/journey/` (Compose) ☑ *(onboarding cream aesthetic, NOT the dark home — see memory `within-design-north-star`)*
- [x] `JourneyTodayScreen` + ViewModel — not-started (intro) + active states; progress ring, streak/completed pills, phase chip, today's `DayCard`, Begin/Continue CTA.
- [x] `JourneyDayScreen` + ViewModel — reflection → prompt **+ inline `JournalField` (autosaves to Room)** → collapsible deep-dives → `ReaffirmationCard` (centerpiece) → checkable mindful action → evening reflection → mark-complete. Day > 7 → locked state + `PaywallSheet`.
- [x] `ui/journey/components/` — `ProgressRing, DayCard, ReaffirmationCard, JournalField, PaywallSheet, JourneySection, MindfulActionRow`. Ori loader extracted to shared `ui/components/Companion.kt`.
- [x] `ui/navigation/AppNavigation.kt` — journey is the post-onboarding home; routes + deep link `within://journey/{journeyId}/day/{day}` (manifest `<intent-filter>` for the `within` scheme added).
- [x] **Onboarding reframed + auto-starts the journey** (`completeOnboarding(onDone)` → `startJourney` + `setActiveJourneyId`; message-preview → `JourneyPreviewCard`) so the user lands directly in Day 1 — no second "Begin".

### 1.5 Notifications ☑
- [x] `notification/DailyContentResolver.kt` — active + unlocked + incomplete → prompt (morning) / `eveningReflection` (≥17:00); else `MessageRepository` fallback. Returns a `DailyContent` sealed type.
- [x] Wired into `NotificationReceiver.kt` (payload source swapped); taps deep-link into `JourneyDayScreen`. Channel renamed "Daily Reflections".

**Phase 1 acceptance — code-level ☑, on-device ☐ (build in Android Studio):** start journey → survives restart (Room); paced daily unlock; day-8 paywall; prompt autosaves to Room; notification shows the active day's prompt + deep-links in, message fallback when no journey. *Caveat: paced unlock needs the calendar to advance (or lower `freePreviewDays`) to reach the day-8 paywall; arbitrary days become tappable with the Phase 2 JourneyMap.*

---

## Phase 2 — Stickiness (make them stay) ✅ code-complete
- [x] `JourneyMapScreen` (+`JourneyMapViewModel`) — 30-day overview grouped by phase; day nodes are completed/available/locked (locked = not yet paced-unlocked, non-tappable); tap opens the day (paywall still gates 8+). Route `journey/map`.
- [x] `ReflectionsScreen` (+`ReflectionsViewModel`) — every saved entry grouped by day in reading order, friendly field labels, empty state; tap a day to revisit. Route `journey/reflections`. Uses `JourneyProgressRepository.allEntries`.
- [x] Completion moments — `CelebrationOverlay` (Ori `gratitude`, streak, final-day message), one-shot via `Celebration` in `JourneyDayViewModel.markComplete`.
- [ ] *(Optional, not done)* export the user's own entries as a personal PDF (reuse `content/build/` approach).
- Entry points: two quiet links ("View all 30 days" / "Your reflections") under the Continue CTA on `JourneyTodayScreen`.

---

## Phase 3 — Billing & recurrence (turn it into revenue)
- [x] **Google Play Billing** — `billing/BillingManager.kt` (`@Singleton`, `BillingClient`, connect/restore/purchase/acknowledge) is the entitlement source of truth and writes `UserPreferences.setPremium(...)`; app still reads `userPreferences.isPremium` (zero blast radius). Started in `MainActivity.onCreate`. `PaywallSheet` now launches the real flow with live prices; `Pricing.kt` holds product/plan ids. **Remaining external step:** create the `within_premium` subscription + `annual`/`monthly` base plans in the Play Console, upload a signed build to a test track, and test the purchase (can't be done from code). `recordPaywallTap` (WTP signal) still fires.
- [x] Generalize content loading for **multiple journeys + locales** — `JourneyRepository` is now a `rawByLocale` catalog → `journeysById`; API is id-aware (`getJourney(id)`, `getDay(id,day)`, `phaseForDay(id,day)`, `canAccessDay(id,day,premium)`, `catalog()`). Add a journey = drop JSON in `res/raw` + list it; add a locale = translate + add under its language tag.
- [ ] `JourneyLibraryScreen` — browse `catalog()`; on tap `setActiveJourneyId` + `startJourney`. **Premature until journey #2 is authored** (would be a 1-item list now). The catalog + active-id plumbing is ready.
- [ ] Personalization — resurface the user's Day 24 values / Day 26 future-self letter later in the journey. *(Needs the manuscript's specific day/field semantics.)*

---

## Phase 4 — Launch prep & growth
- [~] **pt-BR — scaffolding done (2026-06-25), content pending.** ✅ `res/values-pt-rBR/strings.xml` rewritten in full sync with `values/strings.xml` (96/96 keys, verified by diff) and translated — it was stale (pre-pivot daily-message keys). ✅ Hardcoded stat labels (`day streak`/`completed`) moved to resources so the home localises. ✅ Generator is locale-aware for phase titles (`PHASES_BY_LANG`, `--lang pt_br` selects them; en output verified byte-identical). ✅ `JourneyRepository.rawByLocale` documented + ready (falls back to en content on a pt device). **Remaining (content):** translate `content/journal_30day_en.md` → `content/journal_30day_pt_br.md` keeping the English `**Theme:**`/`**Reflection**`/… structural section labels; run `generate_journey_json.py --input content/journal_30day_pt_br.md --lang pt_br`; uncomment the `"pt"` line in `rawByLocale`. (Optional: PDF via `generate_pdf.py`.)
- [x] **Shareable reaffirmation cards** — `share/ShareCardRenderer.kt` draws a branded 1080² PNG (warm gradient + reaffirmation + WITHIN wordmark + Ori), shared via FileProvider (`shareReaffirmation`, `res/xml/file_paths.xml`, manifest `<provider>`). Share button on `ReaffirmationCard` in `JourneyDayScreen`. *(Tunable: card size/typography/footer copy in `ShareCardRenderer`.)*
- [ ] **Analytics** — funnel: install → start → day-7 → paywall → paid → renewal.
- [ ] Store listing / ASO; annual-plan push.

---

## Regenerating content (single source of truth = the manuscript)

Edit `content/journal_30day_en.md`, then regenerate **both** outputs:

```bash
# App JSON (stdlib only)
python3 content/build/generate_journey_json.py            # → app/src/main/res/raw/journey_en.json

# PDF (needs WeasyPrint in a venv — the /tmp venv is EPHEMERAL, recreate if missing:)
python3 -m venv /tmp/wjvenv && /tmp/wjvenv/bin/pip install weasyprint
/tmp/wjvenv/bin/python content/build/generate_pdf.py --all   # → content/journal_30day_en.pdf (69pp)
/tmp/wjvenv/bin/python content/build/generate_pdf.py         # → ..._sample.pdf (cover + Phase 1)
```

Product config (price gate) lives in `generate_journey_json.py`: `TIER`, `FREE_PREVIEW_DAYS` (the day-8 paywall).

## Build / verify
```bash
JAVA_HOME=~/android-studio/jbr ./gradlew :app:compileDebugKotlin   # type-check only
JAVA_HOME=~/android-studio/jbr ./gradlew assembleDebug             # full APK
```
**Ask the user first** — they usually build in Android Studio.
