package com.sec.test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.IO) {
            setUserName()
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.textView).text = readUserName()
            }
            delay(2000)
            incrementCounter()
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.textView).text = "${getCounter()}"
            }
        }
    }

    private suspend fun getCounter(): Int {
        val exampleCounterFlow: Flow<Int> = settingsDataStore.data
            .map { settings ->
                settings.exampleCounter
            }
        return exampleCounterFlow.first()
    }

    private suspend fun incrementCounter() {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setExampleCounter(currentSettings.exampleCounter + 2)
                .build()
        }
    }

    private suspend fun setUserName() {
        val userName = stringPreferencesKey("userName")
        dataStore.edit { settings ->
            settings[userName] = "seckill"
        }
    }

    private suspend fun readUserName(): String {
        val userName = stringPreferencesKey("userName")
        val userNameFlow: Flow<String> = dataStore.data
            .map { preferences ->
                preferences[userName] ?: ""
            }
        return userNameFlow.first()
    }
}