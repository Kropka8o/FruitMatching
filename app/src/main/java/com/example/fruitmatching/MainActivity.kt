package com.example.fruitmatching

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "high_score")

class MainActivity : AppCompatActivity() {
    private val HIGH_SCORE_KEY = intPreferencesKey("high_score")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val playButton = findViewById<Button>(R.id.playButton)
        val helpButton = findViewById<Button>(R.id.helpButton)

        playButton?.setOnClickListener {
            val intent = android.content.Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        helpButton?.setOnClickListener {
            val intent = android.content.Intent(this, RulesActivity::class.java)
            startActivity(intent)
            finish()
        }

        val highScoreView = findViewById<TextView>(R.id.highscoreNumber)
        val highScore = getHighScore(this)
        highScoreView.text = highScore.toString()
    }
}
