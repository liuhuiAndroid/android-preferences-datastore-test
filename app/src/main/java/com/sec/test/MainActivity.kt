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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // At the top level of your kotlin file:
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.IO) {
            setUserName()
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.textView).text = readUserName()
            }
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