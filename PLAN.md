# Within — Product Plan

> **Slogan:** Within — One Positive Thought at a Time

## What it is

An Android app (iOS later) that sends daily positive messages to the user. No server, no database — all content lives in JSON files bundled at build time. Fully internationalized, launching with **pt-BR** and **en-US**.

---

## Message Categories

| Key | Display (en) | Display (pt-BR) |
|-----|-------------|----------------|
| `self_worth` | Self-Worth | Autoestima |
| `self_kindness` | Self-Kindness | Autocompaixão |
| `presence` | Presence | Presença |
| `momentum` | Momentum | Impulso |
| `growth` | Growth | Crescimento |
| `reflection` | Reflection | Reflexão |

---

## Core Features (MVP)

- **Onboarding** (runs once): welcome → category selection → messages per day → notification time picker → permission request
- **Daily notifications**: 1–5 per day at user-defined times, exact scheduling via AlarmManager, re-scheduled on boot
- **Home screen**: today's message card with favorite and refresh actions
- **Settings**: change frequency, notification times, active categories
- **Favorites**: save messages that resonate
- **Streak counter**: consecutive days of engagement
- **Shareable cards**: export message as a branded image (post-MVP v1.1)
- **Home screen widget**: today's message on the launcher (post-MVP v1.1)

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **DI:** Hilt
- **Persistence:** DataStore Preferences
- **Notifications:** AlarmManager (exact) + BroadcastReceiver + WorkManager (boot re-schedule)
- **Content:** JSON files in `res/raw/`, parsed at runtime with Gson
- **i18n:** Android resource system (`values/`, `values-pt-rBR/`)

---

## Monetization — Path to $1,000 MRR by December 2026

### Model: Freemium + Subscription

| Tier | Price | Includes |
|------|-------|----------|
| Free | $0 | 1 message/day, 2 categories, no widget |
| Premium | $2.99/mo or $19.99/yr | Up to 5/day, all categories, widget, favorites, shareable cards |

**Math:** ~335 paying users at $2.99/mo = $1,000 MRR.

### Conversion Triggers (built into the product)
- Free users hit a paywall when trying to save a second favorite
- Widget requires Premium — high-intent users convert fast
- After 7-day streak: in-app prompt to unlock all categories

### Growth Timeline

| Month | MRR Target | Focus |
|-------|-----------|-------|
| Jul–Aug 2026 | $0 → Launch | Build MVP, write 50+ messages/category |
| Sep 2026 | ~$150 | Launch shareable cards, post daily on Reels/TikTok |
| Oct 2026 | ~$350 | ASO optimization, Reddit communities |
| Nov 2026 | ~$600 | Holiday push, gifting angle ("give someone a year of Within") |
| Dec 2026 | ~$1,000 | Annual plan push at year-end |

### Key Growth Levers
1. **Shareable cards** — every share is free acquisition; the message IS the content
2. **Daily social posts** — use your own messages as content on short-form video
3. **ASO** — optimize for "daily affirmations", "positive thinking", "mensagens positivas"
4. **Communities** — r/selfimprovement, r/getdisciplined, Brazilian wellness communities

---

## Risks

| Risk | Mitigation |
|------|-----------|
| Notification fatigue / uninstalls | Quality copy > quantity; give users full control |
| Android battery optimization killing alarms | Use `setExactAndAllowWhileIdle`, handle Doze mode |
| Content staleness | Keep JSON updatable via remote fetch with local fallback (v1.1) |
| Low conversion | A/B test paywall trigger points; measure free→paid funnel closely |

---

## Content Principle

> The tech is a vehicle. The copy is the product.

Before writing code, validate 10 messages per category with real people. Emotional resonance determines retention; retention determines conversion.

---

## Roadmap

### v1.0 (MVP)
- Onboarding, home screen, settings
- Daily notifications with exact scheduling
- Favorites + streak
- pt-BR + en-US content (50 messages/category minimum)
- Free tier + Premium paywall

### v1.1
- Shareable message cards
- Home screen widget
- Remote JSON content updates (OTA messages without app update)

### v1.2
- iOS (SwiftUI, same JSON content format)
- Category weighting based on "resonated" reactions
- Streak-based re-engagement notifications
