package com.within.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.within.app.R
import com.within.app.data.model.Journey
import com.within.app.data.model.JourneyDay
import com.within.app.data.model.JourneyPhase
import com.within.app.data.model.Tier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private data class JourneyJson(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("lengthDays") val lengthDays: Int,
    @SerializedName("tier") val tier: String,
    @SerializedName("freePreviewDays") val freePreviewDays: Int,
    @SerializedName("phases") val phases: List<PhaseJson>,
    @SerializedName("days") val days: List<DayJson>
)

private data class PhaseJson(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("from") val from: Int,
    @SerializedName("to") val to: Int,
    @SerializedName("intro") val intro: String
)

private data class DayJson(
    @SerializedName("day") val day: Int,
    @SerializedName("phaseId") val phaseId: String,
    @SerializedName("title") val title: String,
    @SerializedName("theme") val theme: String,
    @SerializedName("reflection") val reflection: List<String>,
    @SerializedName("prompt") val prompt: String,
    @SerializedName("deepDive") val deepDive: List<String>,
    @SerializedName("reaffirmation") val reaffirmation: String,
    @SerializedName("mindfulAction") val mindfulAction: String,
    @SerializedName("eveningReflection") val eveningReflection: String
)

@Singleton
class JourneyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    /**
     * Bundled journeys per locale (language tag → raw resources). Add a journey by dropping its
     * generated JSON in `res/raw` and listing it here; add a locale by translating the manuscript.
     * The catalog loads the entries for the device locale, falling back to [DEFAULT_LOCALE].
     *
     * pt-BR scaffolding: the UI chrome is already translated (`res/values-pt-rBR/strings.xml`).
     * To localise the journey *content* too: translate `content/journal_30day_pt_br.md`, run
     * `generate_journey_json.py --input content/journal_30day_pt_br.md --lang pt_br` (→
     * `res/raw/journey_pt_br.json`), then uncomment the "pt" line below. Until then, a pt-BR device
     * shows Portuguese chrome with the English journey content (graceful fallback).
     */
    private val rawByLocale: Map<String, List<Int>> = mapOf(
        "en" to listOf(R.raw.journey_en)
        // "pt" to listOf(R.raw.journey_pt_br)
    )

    /** id → journey, loaded once for the active locale. */
    private val journeysById: Map<String, Journey> by lazy { loadCatalog() }

    private fun loadCatalog(): Map<String, Journey> {
        val locale = java.util.Locale.getDefault().language
        val resources = rawByLocale[locale] ?: rawByLocale.getValue(DEFAULT_LOCALE)
        return resources.map { loadJourney(it) }.associateBy { it.id }
    }

    private fun loadJourney(rawResId: Int): Journey {
        val json = context.resources.openRawResource(rawResId)
            .bufferedReader().use { it.readText() }
        return gson.fromJson(json, JourneyJson::class.java).toDomain()
    }

    /** Every available journey — drives the library/browse screen. */
    fun catalog(): List<Journey> = journeysById.values.toList()

    fun getJourney(id: String): Journey? = journeysById[id]

    /** The default journey — used where no specific id is in hand (single-journey paths). */
    fun getJourney(): Journey = journeysById.values.first()

    fun getDay(journeyId: String, day: Int): JourneyDay? =
        journeysById[journeyId]?.days?.find { it.day == day }

    fun getPhase(journeyId: String, phaseId: String): JourneyPhase? =
        journeysById[journeyId]?.phases?.find { it.id == phaseId }

    fun phaseForDay(journeyId: String, day: Int): JourneyPhase? =
        journeysById[journeyId]?.phases?.find { day in it.from..it.to }

    /** Days a free user can open before the paywall (1..freePreviewDays). */
    fun isDayFree(journeyId: String, day: Int): Boolean =
        journeysById[journeyId]?.let { day <= it.freePreviewDays } ?: false

    /** Free users get the preview days; premium unlocks everything. Day 8 is the gate. */
    fun canAccessDay(journeyId: String, day: Int, isPremium: Boolean): Boolean =
        isPremium || isDayFree(journeyId, day)

    companion object {
        const val DEFAULT_LOCALE = "en"
    }
}

private fun JourneyJson.toDomain(): Journey = Journey(
    id = id,
    title = title,
    subtitle = subtitle,
    tagline = tagline,
    lengthDays = lengthDays,
    tier = Tier.entries.find { it.key == tier } ?: Tier.PREMIUM,
    freePreviewDays = freePreviewDays,
    phases = phases.map { JourneyPhase(it.id, it.title, it.from, it.to, it.intro) },
    days = days.map {
        JourneyDay(
            day = it.day,
            phaseId = it.phaseId,
            title = it.title,
            theme = it.theme,
            reflection = it.reflection,
            prompt = it.prompt,
            deepDive = it.deepDive,
            reaffirmation = it.reaffirmation,
            mindfulAction = it.mindfulAction,
            eveningReflection = it.eveningReflection
        )
    }
)
