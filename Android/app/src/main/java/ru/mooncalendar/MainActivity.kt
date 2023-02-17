package ru.mooncalendar

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.screens.AuthScreen
import ru.mooncalendar.screens.CalendarScreen
import ru.mooncalendar.screens.MainScreen
import ru.mooncalendar.screens.ProfileScreen
import ru.mooncalendar.ui.theme.*

enum class BottomBar(val text: String, val iconId: Int) {
    CALENDAR("Календарь", R.drawable.moon),
    PROFILE("Профиль", R.drawable.user)
}

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoonCalendarTheme {
                val navController = rememberNavController()
                val auth = remember(Firebase::auth)
                var bottomBar by remember { mutableStateOf(BottomBar.CALENDAR) }

                val systemUiController = rememberSystemUiController()
                val primaryBackground = primaryBackground()

                LaunchedEffect(key1 = Unit, block = {
                    systemUiController.setNavigationBarColor(
                        color = primaryBackground
                    )
                })

                Scaffold(
                    bottomBar = {
                        BottomNavigation(
                            backgroundColor = secondaryBackground()
                        ) {
                            BottomBar.values().forEach {
                                BottomNavigationItem(
                                    selected = it == bottomBar,
                                    onClick = {
                                        bottomBar = it

                                        when(bottomBar){
                                            BottomBar.CALENDAR ->
                                                navController.navigate("main_screen")
                                            BottomBar.PROFILE -> {
                                                if(auth.currentUser != null){
                                                    navController.navigate("profile_screen")
                                                }else {
                                                    navController.navigate("auth_screen")
                                                }
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = it.text,
                                            color = primaryText()
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = it.iconId),
                                            contentDescription = null,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    },
                                    selectedContentColor = tintColor,
                                    unselectedContentColor = primaryText()
                                )
                            }
                        }
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen",
                        builder = {

                            composable("splash_screen"){

                                LaunchedEffect(key1 = Unit, block = {
                                    navController.navigate("main_screen")
                                })

                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .background(primaryBackground()))
                            }

                            composable("auth_screen"){
                                AuthScreen(navController = navController)
                            }

                            composable(
                                route = "main_screen?dateString={dateString}",
                                arguments = listOf(
                                    navArgument("dateString"){
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    }
                                )
                            ){
                                MainScreen(
                                    navController = navController,
                                    dateString = it.arguments?.getString("dateString")
                                )
                            }

                            composable("calendar_screen"){
                                CalendarScreen(navController = navController)
                            }

                            composable("profile_screen"){
                                ProfileScreen(navController = navController)
                            }
                        }
                    )
                }
            }
        }
    }
}