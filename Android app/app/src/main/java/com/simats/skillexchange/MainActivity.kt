package com.simats.skillexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.simats.skillexchange.data.local.SessionManager
import com.simats.skillexchange.navigation.AppNavGraph
import com.simats.skillexchange.ui.theme.SkillExchangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager(this)
        
        enableEdgeToEdge()
        setContent {
            SkillExchangeTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    sessionManager = sessionManager
                )
            }
        }
    }
}

