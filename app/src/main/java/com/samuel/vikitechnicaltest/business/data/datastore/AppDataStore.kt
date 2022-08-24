package com.samuel.vikitechnicaltest.business.data.datastore


interface AppDataStore {

    fun setValue(
        key: String,
        value: Float
    )

    suspend fun readValue(
        key: String,
    ): Float?



}