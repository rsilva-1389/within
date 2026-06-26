# Within — Journey Engine (Design Sketch)

> **North star:** turn the finite 30-day book into a *renewable, sticky* format that gives someone a reason to pay in **month 13** — not just month 1. The journey is the monetization + retention layer; the daily message stream becomes the free acquisition + "between-journeys" layer.

This is a sketch, not a spec. It extends the current architecture rather than replacing it. Anything marked **(new)** is net-new; everything else reuses what exists.

---

## 0. The one idea that holds it together

A small **content-resolution layer** sits between your existing notification machinery and two content sources (journeys + messages). Everything else is plumbing around it.

```
                       ┌─────────────────────────────┐
                       │   NotificationScheduler      │  (existing — AlarmManager, exact)
                       │   NotificationReceiver       │
                       │   BootReceiver               │
                       └──────────────┬──────────────┘
                                      │ "what do I say at 8am today?"
                                      ▼
                       ┌─────────────────────────────┐
                       │  DailyContentResolver (new)  │
                       │  active journey? unlocked?   │
                       │  incomplete? → journey prompt│
                       │  else → message stream       │
                       └───────┬─────────────┬────────┘
                               ▼             ▼
                    JourneyRepository   MessageRepository
                        (new)              (existing)
```

The win: you **reuse all your notification infra**. Only the *source of the words* changes, and it degrades gracefully back to today's product when there's no active journey.

---

## 1. Content model (data layer)

Journeys ship as JSON in `res/raw/` — same pattern as `messages_en.json`, same Gson parsing, same future OTA path (PLAN.md v1.1). The richer shape:

```json
{
  "id": "coming-home",
  "title": "The 30-Day Journal",
  "subtitle": "Coming Home to Yourself",
  "lengthDays": 30,
  "tier": "premium",
  "freePreviewDays": 7,
  "phases": [
    { "id": "self_acceptance", "title": "Self-Acceptance", "from": 1, "to": 7, "intro": "Before you can change anything…" }
  ],
  "days": [
    {
      "day": 1, "phaseId": "self_acceptance",
      "title": "Arriving Where You Are", "theme": "Awareness",
      "reflection": ["para 1", "para 2", "para 3"],
      "prompt": "If you set aside the answer you usually give…",
      "deepDive": ["q1", "q2", "q3"],
      "reaffirmation": "I can meet myself honestly today…",
      "mindfulAction": "Set a timer for three minutes…",
      "eveningReflection": "What did you notice about yourself today…"
    }
  ]
}
```

Kotlin models parallel your existing `data/model/Message.kt`:

```kotlin
// data/model/Journey.kt  (new)
data class Journey(
    val id: String, val title: String, val subtitle: String,
    val lengthDays: Int, val tier: Tier, val freePreviewDays: Int,
    val phases: List<JourneyPhase>, val days: List<JourneyDay>,
)
data class JourneyPhase(val id: String, val title: String, val from: Int, val to: Int, val intro: String)
data class JourneyDay(
    val day: Int, val phaseId: String, val title: String, val theme: String,
    val reflection: List<String>, val prompt: String, val deepDive: List<String>,
    val reaffirmation: String, val mindfulAction: String, val eveningReflection: String,
)
enum class Tier { FREE, PREMIUM }
```

### Single source of truth (do this first, it's nearly free)
`content/journal_30day_en.md` already parses cleanly — `content/build/generate_pdf.py` does it today. Add a sibling `generate_journey_json.py` that reuses the same `parse_day()` to emit `res/raw/journey_en.json`. **One manuscript → both the PDF *and* the app content.** No re-typing, no drift, and translations (`_pt_br`) fall out of the same pipeline.

---

## 2. Persistence — this is the real architectural addition

DataStore Preferences is right for *pointers* but wrong for *entries*. Split it:

| Concern | Store | Why |
|---|---|---|
| User prefs, active-journey pointer, premium flag | **DataStore** (existing) | tiny key-value, already wired |
| Per-day progress, completion, streak | **Room (new)** | queryable, multi-journey |
| **Journal entries (free text the user writes)** | **Room (new)** | growing, relational, exportable — DataStore can't do this |

The journal entries are the point: they create the **switching cost** a message stream never can. That's your retention moat.

```kotlin
// data/local/  (new — Room)
@Entity(primaryKeys = ["journeyId", "day", "field"])
data class JournalEntry(
    val journeyId: String, val day: Int,
    val field: String,        // "prompt" | "deepDive0".."deepDive2" | "evening" | "note"
    val text: String, val updatedAt: Long,
)

@Entity(primaryKeys = ["journeyId"])
data class JourneyProgress(
    val journeyId: String, val startedEpochDay: Long,
    val currentDay: Int, val lastCompletedDay: Int, val status: String, // active|completed|paused
)
```

Provide `WithinDatabase` + DAOs through a new `di/DatabaseModule.kt` (parallels your `AppModule`).

---

## 3. Domain logic (the product decisions, encoded)

A thin domain layer (`domain/usecase/`, new). The decisions that matter:

- **Pacing — paced unlock, not binge.** Day _N_ unlocks on `startedEpochDay + (N-1)`. This is what makes the daily notification *mean* something and builds the streak/habit that drives retention. Missed days stay open to "catch up"; you can't skip ahead. (A binge-able journey is just an ebook — and ebooks don't retain.)
  ```kotlin
  fun availableDay(p: JourneyProgress, lengthDays: Int, today: Long): Int =
      minOf(lengthDays, (today - p.startedEpochDay + 1).toInt())
  ```
- **Daily content resolution.** `ResolveDailyContent` → journey prompt if active+unlocked+incomplete, else message-stream fallback. This is the unification point.
- **Gating.** `CanAccessDay(journey, day, isPremium)` → free users get `day <= freePreviewDays`; day 8 → paywall. **This replaces your weak "save a 2nd favorite" trigger with a far stronger one:** a user mid-journey, invested, momentum-driven.
- **Streak** = consecutive completed days (you already wanted a streak counter; now it has real substance to count).

---

## 4. Notification integration (reuse, don't rebuild)

`NotificationScheduler` / `NotificationReceiver` / `BootReceiver` stay as-is. In the receiver, swap the payload source to call `ResolveDailyContent`. Add deep links so the notification opens the day:

```
within://journey/{journeyId}/day/{n}     → JourneyDayScreen
```

Morning slot → that day's `prompt`; evening slot → `eveningReflection`. When no journey is active, it's your current message — same code path, graceful fallback.

---

## 5. UI surfaces (Compose, `ui/journey/` — new)

Reuse the design system (cream/navy/coral/teal, Fredoka/Nunito, **Ori reacting to progress**). The PDF you already built *is* the visual spec.

- **JourneyTodayScreen** — new home when a journey is active: today's day card, streak, progress ring (X/30), current phase. Falls back to your existing `HomeScreen` (message card) when no journey is active.
- **JourneyDayScreen** — the day, section by section (collapsible to avoid overwhelm): reflection → prompt **with an inline journaling field** (saves to Room) → deep-dives (optional fields) → reaffirmation (the shareable centerpiece) → mindful action (checkable) → evening reflection.
- **JourneyMapScreen** — the 30-day overview (your PDF's contents page, interactive): phases, locked/unlocked/done states, tap to revisit. Visualizing progress *is* a retention driver.
- **PaywallSheet** — at the day-8 gate.
- *(later)* **JourneyLibraryScreen** (recurrence: journeys #2, #3…) and **ReflectionsScreen** (every entry the user has written — their "second brain," the thing they won't abandon).

---

## 6. Package / layer map

```
data/
  model/        Journey, JourneyPhase, JourneyDay, Tier         (new)  + Message, Category (existing)
  content/      JourneyContentSource — reads res/raw/journey_*.json   (new, parallels MessageRepository)
  local/        WithinDatabase, JournalEntryDao, JourneyProgressDao   (new — Room)
  preferences/  UserPreferences (existing) + activeJourneyId, isPremium
  repository/   JourneyRepository (content+progress+entries)    (new)  + MessageRepository (existing)
domain/usecase/ ResolveDailyContent, AvailableDay, CanAccessDay, SaveReflection, ComputeStreak   (new)
notification/   NotificationScheduler / Receiver / BootReceiver (existing) + DailyContentResolver (new)
ui/journey/     JourneyToday, JourneyDay, JourneyMap, Paywall (+ ViewModels)   (new)
di/             AppModule (existing) + DatabaseModule (new)
```

---

## 7. Build sequence (runway-aware → Dec 2026)

- **Phase 0 — content pipeline (days, ~free).** `generate_journey_json.py` → `res/raw/journey_en.json`. Proves single-source-of-truth; no app code. *Lowest-risk first step.*
- **Phase 1 — monetizable core (the MVP that matters).** Room + `JourneyRepository` + `JourneyTodayScreen` + `JourneyDayScreen` (with journaling) + paced unlock + **day-8 paywall** + notification resolver. Free tier = 7-day taster → paywall; existing message stream stays as the free/fallback layer. *This is the conversion + retention engine.*
- **Phase 2 — stickiness.** JourneyMap, Reflections list, **PDF export of the user's own entries** (reuse the generator!), streak polish.
- **Phase 3 — recurrence.** JourneyLibrary + journey #2/#3 (seasonal/themed), personalization (resurface their Day 24 values, their Day 26 future-self letter). *This is what makes month 13 alive.*

---

## 8. Open decisions (yours to make — they change the engine)

1. **Does the journey replace the home screen for active users, or live beside the message card?** (Affects navigation + whether messages stay primary.)
2. **Is journaling required or optional?** Optional = broader market, less friction, but weaker switching cost. Recommend optional-but-encouraged.
3. **Paced (1/day) vs. self-paced unlock?** Paced retains better; self-paced respects autonomy. Recommend paced with catch-up.
4. **Pricing tier for journeys.** A guided-journey + journaling product can support $40–70/yr; your $19.99 anchors to a commodity message app. Decide before you wire the paywall — it changes the MRR math.
5. **Migrate streak/progress fully to Room, or keep a lightweight copy in DataStore for the widget?** (Widget reads should be cheap.)
