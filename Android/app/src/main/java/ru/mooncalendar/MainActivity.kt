package ru.mooncalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.screens.AuthScreen
import ru.mooncalendar.screens.MainScreen
import ru.mooncalendar.ui.theme.MoonCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoonCalendarTheme {
                val navController = rememberNavController()

                val auth = remember(Firebase::auth)

                NavHost(
                    navController = navController,
                    startDestination = if(auth.currentUser != null) "main_screen" else "auth_screen",
                    builder = {
                        composable("auth_screen"){
                            AuthScreen(navController = navController)
                        }

                        composable("main_screen"){
                            MainScreen(navController = navController)
                        }
                    }
                )
            }
        }
    }
}