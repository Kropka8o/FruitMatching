package com.example.fruitmatching

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val HIGH_SCORE_KEY = intPreferencesKey("high_score")

fun saveHighScore(context: Context, score: Int) = runBlocking {
    context.dataStore.edit { preferences ->
        preferences[HIGH_SCORE_KEY] = score
    }
}

fun getHighScore(context: Context): Int = runBlocking {
    val preferences = context.dataStore.data.first()
    preferences[HIGH_SCORE_KEY] ?: 0
}
