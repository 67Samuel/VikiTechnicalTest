package com.samuel.vikitechnicaltest.business.data.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppDataStoreManager(
    private val context: Application
): AppDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)
    private val scope = CoroutineScope(Main)

    override fun setValue(
        key: String,
        value: String
    ) {
        scope.launch {
            context.dataStore.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
    }

    override suspend fun readValue(
        key: String,
    ): String? {
        return context.dataStore.data.first()[stringPreferencesKey(key)]
    }

    companion object {
        private const val APP_DATASTORE = "conversion rates"
    }
}