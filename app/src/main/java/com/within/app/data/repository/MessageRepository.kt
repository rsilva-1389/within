package com.within.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.within.app.R
import com.within.app.data.model.Category
import com.within.app.data.model.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private data class MessageJson(
    @SerializedName("id") val id: String,
    @SerializedName("category") val category: String,
    @SerializedName("text") val text: String
)

private data class MessagesFile(
    @SerializedName("messages") val messages: List<MessageJson>
)

@Singleton
class MessageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val messages: List<Message> by lazy { loadMessages() }

    private fun loadMessages(): List<Message> {
        val locale = context.resources.configuration.locales[0]
        val rawRes = if (locale.language == "pt") R.raw.messages_pt_br else R.raw.messages_en
        val json = context.resources.openRawResource(rawRes).bufferedReader().use { it.readText() }
        val type = object : TypeToken<MessagesFile>() {}.type
        val file: MessagesFile = gson.fromJson(json, type)
        return file.messages.mapNotNull { msg ->
            val category = Category.entries.find { it.key == msg.category } ?: return@mapNotNull null
            Message(id = msg.id, category = category, text = msg.text)
        }
    }

    fun getRandomMessage(
        allowedCategories: Set<Category> = Category.entries.toSet(),
        excludeIds: Set<String> = emptySet()
    ): Message? {
        val pool = messages.filter { it.category in allowedCategories && it.id !in excludeIds }
        return pool.randomOrNull()
            ?: messages.filter { it.category in allowedCategories }.randomOrNull()
    }

    fun getMessageById(id: String): Message? = messages.find { it.id == id }

    fun getAllFavorites(ids: Set<String>): List<Message> = messages.filter { it.id in ids }
}
