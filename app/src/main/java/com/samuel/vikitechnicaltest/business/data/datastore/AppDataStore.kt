package com.samuel.vikitechnicaltest.business.data.datastore


interface AppDataStore {

    fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

}