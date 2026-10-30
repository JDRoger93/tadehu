package com.funtadehu.autosugestion.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import kotlin.random.Random

class AutosuggestionPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("funtadehu_autosugestion_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PHRASES = "key_phrases"
        private const val KEY_REST_MODE = "key_rest_mode"
        private const val KEY_INTERVAL = "key_interval"
        private const val KEY_LAST_QUOTE = "key_last_quote"

        val DEFAULT_PHRASES = listOf(
            "Todos los días, en todos los sentidos, estoy mejor, mejor y mejor.",
            "Para ser eficaz y feliz, mantengo mi mente serena, enfocada y en paz.",
            "Tengo fe inquebrantable en mi capacidad de lograr cada una de mis metas.",
            "Mi mente subconsciente acepta pensamientos de éxito, salud y bienestar.",
            "Cada desafío es una oportunidad para crecer con sabiduría y determinación.",
            "Acepto lo que no puedo cambiar y actúo con energía en lo que sí controlo.",
            "Soy dueño de mis pensamientos y elijo cultivar armonía y seguridad.",
            "Merezco el éxito, la felicidad y la tranquilidad en todo lo que hago."
        )
    }

    fun getPhrases(): List<String> {
        val raw = prefs.getString(KEY_PHRASES, null)
        if (raw.isNullOrBlank()) {
            savePhrases(DEFAULT_PHRASES)
            return DEFAULT_PHRASES
        }
        return try {
            val jsonArray = JSONArray(raw)
            val list = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optString(i)
                if (item.isNotBlank()) list.add(item)
            }
            if (list.isEmpty()) DEFAULT_PHRASES else list
        } catch (e: Exception) {
            DEFAULT_PHRASES
        }
    }

    fun savePhrases(phrases: List<String>) {
        val jsonArray = JSONArray()
        phrases.forEach { jsonArray.put(it) }
        prefs.edit().putString(KEY_PHRASES, jsonArray.toString()).apply()
    }

    fun addPhrase(phrase: String) {
        val current = getPhrases().toMutableList()
        current.add(0, phrase.trim())
        savePhrases(current)
    }

    fun deletePhrase(index: Int) {
        val current = getPhrases().toMutableList()
        if (index in current.indices && current.size > 1) {
            current.removeAt(index)
            savePhrases(current)
        }
    }

    fun isRestMode(): Boolean {
        return prefs.getBoolean(KEY_REST_MODE, false)
    }

    fun setRestMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REST_MODE, enabled).apply()
    }

    fun getIntervalMinutes(): Int {
        return prefs.getInt(KEY_INTERVAL, 30)
    }

    fun setIntervalMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_INTERVAL, minutes).apply()
    }

    fun getLastQuote(): String {
        return prefs.getString(KEY_LAST_QUOTE, DEFAULT_PHRASES.first()) ?: DEFAULT_PHRASES.first()
    }

    fun setLastQuote(quote: String) {
        prefs.edit().putString(KEY_LAST_QUOTE, quote).apply()
    }

    fun getRandomPhrase(): String {
        val list = getPhrases()
        if (list.isEmpty()) return DEFAULT_PHRASES.first()
        val picked = list[Random.nextInt(list.size)]
        setLastQuote(picked)
        return picked
    }
}

