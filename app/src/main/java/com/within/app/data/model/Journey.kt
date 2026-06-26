package com.within.app.data.model

data class Journey(
    val id: String,
    val title: String,
    val subtitle: String,
    val tagline: String,
    val lengthDays: Int,
    val tier: Tier,
    val freePreviewDays: Int,
    val phases: List<JourneyPhase>,
    val days: List<JourneyDay>
)

data class JourneyPhase(
    val id: String,
    val title: String,
    val from: Int,
    val to: Int,
    val intro: String
)

data class JourneyDay(
    val day: Int,
    val phaseId: String,
    val title: String,
    val theme: String,
    val reflection: List<String>,
    val prompt: String,
    val deepDive: List<String>,
    val reaffirmation: String,
    val mindfulAction: String,
    val eveningReflection: String
)

enum class Tier(val key: String) {
    FREE("free"),
    PREMIUM("premium")
}
