package com.example.myapplication.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataStoreManager constructor( val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        private val Context.dataStore by preferencesDataStore("local")
    }

    // ---------------- SAVE ----------------
    suspend fun saveData(key: String, value: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    fun saveDataFromJava(key: String, value: String, callback: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            saveData(key, value)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    // ---------------- GET ----------------
    suspend fun getString(key: String): String? {
        return context.dataStore.data.map {
            it[stringPreferencesKey(key)]
        }.first()
    }

    fun getStringFromJava(key: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val value = getString(key)
            withContext(Dispatchers.Main) {
                callback(value)
            }
        }
    }

    // ---------------- DELETE ----------------
    suspend fun deleteKey(key: String) {
        context.dataStore.edit {
            it.remove(stringPreferencesKey(key))
        }
    }

    fun deleteKeyFromJava(key: String, callback: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteKey(key) // suspend function
            withContext(Dispatchers.Main) {
                callback() // notify Java when deletion is done
            }
        }
    }
}