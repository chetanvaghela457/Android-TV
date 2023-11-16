package com.strimm.application.prefstore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.strimm.application.utils.aquaintDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PrefStoreImpl(context: Context) : PrefStore {
    private val prefStore by lazy { context.aquaintDataStore }

    override suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        prefStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun getInt(key: Preferences.Key<Int>): Flow<Int> =
        prefStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: 0 }

    override fun getLong(key: Preferences.Key<Long>): Flow<Long> =
        prefStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: 0L }

    override fun getFloat(key: Preferences.Key<Float>): Flow<Float> =
        prefStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: 0f }

    override fun getDouble(key: Preferences.Key<Double>): Flow<Double> =
        prefStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: 0.0 }

    override fun getBoolean(key: Preferences.Key<Boolean>): Flow<Boolean> =
        prefStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: false }

    override fun getString(key: Preferences.Key<String>): Flow<String> =
        prefStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: "" }


    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        prefStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    suspend fun removeAllPreferences() {
        prefStore.edit { preferences ->
            preferences.clear()
        }
    }

}