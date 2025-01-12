package com.example.travelplanner.DataStorage

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStorageManager(private val context: Context) {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val ACCOUNT_CREATED_KEY = stringPreferencesKey("account_created")
    private val ACCOUNT_GMAIL_KEY = stringPreferencesKey("account_gmail")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[TOKEN_KEY]
            }
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun saveAccountToken(accountToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCOUNT_CREATED_KEY] = accountToken
        }
        Log.d("DataStorageManager", "Saved accountCreated: $accountToken")
    }

    suspend fun clearAccountToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCOUNT_CREATED_KEY)
        }
        Log.d("DataStorageManager", "Cleared accountCreated token")
    }

    fun getAccountToken(): Flow<String?> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[ACCOUNT_CREATED_KEY]
            }
    }

    suspend fun saveAccountEmail(email: String){
        context.dataStore.edit { preferences ->
            preferences[ACCOUNT_GMAIL_KEY] = email
        }
        Log.d("DataStorageManager", "Saved accountCreatedEmail: $email")
    }

    fun getAccountEmail(): Flow<String?> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[ACCOUNT_GMAIL_KEY]
            }
    }
}