package com.example.travelplanner.DataStorage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object DataStorageManager {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val ACCOUNT_CREATED_KEY = booleanPreferencesKey("account_created")

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.first()
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun saveAccountCreated(context: Context, created: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ACCOUNT_CREATED_KEY] = created
        }
    }

    suspend fun getAccountCreated(context: Context): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[ACCOUNT_CREATED_KEY] ?: false
        }.first()
    }
}

