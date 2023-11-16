package com.strimm.application.prefstore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PrefStore {

    suspend fun <T> savePreference(key: Preferences.Key<T>, value: T)

    fun getInt(key: Preferences.Key<Int>): Flow<Int>
    fun getLong(key: Preferences.Key<Long>): Flow<Long>
    fun getFloat(key: Preferences.Key<Float>): Flow<Float>
    fun getDouble(key: Preferences.Key<Double>): Flow<Double>
    fun getBoolean(key: Preferences.Key<Boolean>): Flow<Boolean>
    fun getString(key: Preferences.Key<String>): Flow<String>
}