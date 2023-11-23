package com.strimm.application.prefstore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    const val PREF_STORE_NAME = "AquaintPrefStore"
    val IS_AUTH_TOKEN_EXPIRED = booleanPreferencesKey("isUserAPITokenExpired")
    val API_AUTH_TOKEN = stringPreferencesKey("apiAuthorizationToken")
    val USER_ID = intPreferencesKey("USER_ID")
    val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
    val FAVOURITE_DATA = stringPreferencesKey("fav_data")

}
