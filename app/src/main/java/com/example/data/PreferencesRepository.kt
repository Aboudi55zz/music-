package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.InstrumentTimbre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.maqamDataStore: DataStore<Preferences> by preferencesDataStore(name = "maqam_prefs")

class PreferencesRepository(private val context: Context) {
    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorite_maqamat")
        val INSTRUMENT = stringPreferencesKey("selected_instrument")
        val BEST_STREAK = intPreferencesKey("quiz_best_streak")
        val TOTAL_CORRECT = intPreferencesKey("quiz_total_correct")
    }

    val favoriteIds: Flow<Set<String>> = context.maqamDataStore.data.map { prefs ->
        prefs[Keys.FAVORITES] ?: emptySet()
    }

    val selectedInstrument: Flow<InstrumentTimbre> = context.maqamDataStore.data.map { prefs ->
        val raw = prefs[Keys.INSTRUMENT]
        runCatching { if (raw != null) InstrumentTimbre.valueOf(raw) else InstrumentTimbre.OUD }
            .getOrDefault(InstrumentTimbre.OUD)
    }

    val bestStreak: Flow<Int> = context.maqamDataStore.data.map { prefs ->
        prefs[Keys.BEST_STREAK] ?: 0
    }

    val totalCorrect: Flow<Int> = context.maqamDataStore.data.map { prefs ->
        prefs[Keys.TOTAL_CORRECT] ?: 0
    }

    suspend fun toggleFavorite(maqamId: String) {
        context.maqamDataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITES] ?: emptySet()
            prefs[Keys.FAVORITES] = if (maqamId in current) current - maqamId else current + maqamId
        }
    }

    suspend fun setInstrument(timbre: InstrumentTimbre) {
        context.maqamDataStore.edit { prefs ->
            prefs[Keys.INSTRUMENT] = timbre.name
        }
    }

    suspend fun recordQuizCorrect(currentStreak: Int) {
        context.maqamDataStore.edit { prefs ->
            val prevBest = prefs[Keys.BEST_STREAK] ?: 0
            if (currentStreak > prevBest) {
                prefs[Keys.BEST_STREAK] = currentStreak
            }
            prefs[Keys.TOTAL_CORRECT] = (prefs[Keys.TOTAL_CORRECT] ?: 0) + 1
        }
    }
}
