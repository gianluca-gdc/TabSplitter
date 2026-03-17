// androidApp/src/main/java/com/gianluca_gdc/tabsplitter/android/datastore/PeopleDataStore.kt
package com.gianluca_gdc.tabsplitter.android.datastore

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// create the datastore instance on context
private val Context.dataStore by preferencesDataStore(name = "people_prefs")

object PeopleKeys {
    // use each persons name as a key
    fun phoneKeyFor(name: String) = stringPreferencesKey("phone_of_$name")
}

/**
 * save or update a persons phone under their name
 */
suspend fun savePerson(context: Context, name: String, phone: String) {
    context.dataStore.edit { prefs: MutablePreferences ->
        prefs[PeopleKeys.phoneKeyFor(name)] = phone
    }
}

/**
 * delete one person by clearing their key
 */
suspend fun removePerson(context: Context, name: String) {
    context.dataStore.edit { prefs ->
        prefs.remove(PeopleKeys.phoneKeyFor(name))
    }
}

/**
 * load full map of name -> phone as a flow
 * keys(name) come back as "phone_of_<name>" ::: must strip that prefix
 */
fun loadPeople(context: Context): Flow<Map<String, String>> =
    context.dataStore.data.map { prefs ->
        prefs.asMap().entries.mapNotNull { entry ->
            val key = entry.key
            val value = entry.value as? String ?: return@mapNotNull null
            val name = key.name.removePrefix("phone_of_")
            if (name.isNotBlank() && value.isNotBlank()) {
                name to value
            } else {
                null
            }
        }.toMap()
    }