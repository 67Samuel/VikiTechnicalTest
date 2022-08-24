package com.samuel.vikitechnicaltest.business.data.datastore

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppDataStoreManager(
    private val context: Application
): AppDataStore {
    private val TAG: String = "AppDataStoreManagerDebug"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)
    private val scope = CoroutineScope(IO)

    override fun setValue(
        key: String,
        value: Float
    ) {
        scope.launch {
            context.dataStore.edit {
//                Log.d(TAG, "setValue: saving $key to $value")
                it[stringPreferencesKey(key)] = value.toString()
            }
        }
    }

    override suspend fun readValue(
        key: String,
    ): Float? {
//        Log.d(TAG, "readValue: pref: ${context.dataStore.data.first()}")
        return context.dataStore.data.first()[stringPreferencesKey(key)]?.toFloat()
    }

    companion object {
        private const val APP_DATASTORE = "conversion rates"
    }
}