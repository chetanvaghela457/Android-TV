package com.strimm.application.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.strimm.application.model.ThemeData
import com.strimm.application.prefstore.PreferenceKeys
import java.io.IOException

val Context.aquaintDataStore: DataStore<Preferences> by preferencesDataStore(PreferenceKeys.PREF_STORE_NAME)

val sortTypes = arrayListOf(
    "Due First",
    "Due Last",
    "Priority : High to Low",
    "Priority : Low to High",
)

var mainThemeData = ThemeData()

const val API_UTC_TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val API_LOCAL_DATE_FORMAT="yyyy-MM-dd"
const val TIME_SLOT_DATE_FORMAT="dd MMM yyyy"

fun getJsonDataFromRaw(context: Context, fileName: Int): String {
    val jsonString: String
    try {
        jsonString =
            context.resources.openRawResource(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return "null"
    }
    return jsonString
}

