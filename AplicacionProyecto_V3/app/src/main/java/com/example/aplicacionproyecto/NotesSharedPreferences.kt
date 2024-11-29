package com.example.aplicacionproyecto

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
class NotesSharedPreferences(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "notes_prefs"
        private const val NOTES_KEY = "notes_list"
    }

    fun saveNotes(notes: List<Note>) {
        val json = gson.toJson(notes) // Convertir la lista de notas a JSON
        sharedPreferences.edit().putString(NOTES_KEY, json).apply()
    }

    fun loadNotes(): List<Note> {
        val json = sharedPreferences.getString(NOTES_KEY, null)
        return if (json != null) {
            val type = object : com.google.gson.reflect.TypeToken<List<Note>>() {}.type
            gson.fromJson(json, type) // Convertir JSON de vuelta a lista de notas
        } else {
            emptyList() // Si no hay notas guardadas, devolver una lista vacía
        }
    }
}
