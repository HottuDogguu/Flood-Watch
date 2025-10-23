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

class DataStoreManager constructor(val context: Context) {
    private val hashingUtility = HashingUtility()

    companion object {
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        private val Context.dataStore by preferencesDataStore("secure_user_prefs")
    }

    // ---------------- SAVE ----------------
    suspend fun saveData(key: String, value: String) {
        val encryptedValue = hashingUtility.encrypt(value)
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = encryptedValue
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

    suspend fun readData(key: String): String? {
        val prefs = context.dataStore.data.first()
        val encrypted = prefs[stringPreferencesKey(key)]
        return encrypted?.let { hashingUtility.decrypt(it) }
    }


    fun getStringFromJava(key: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val value = readData(key)
            withContext(Dispatchers.Main) {
                callback(value)
            }
        }
    }


    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { it.clear() }
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

    fun clearAllFromJava(callback: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            clearAll()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}