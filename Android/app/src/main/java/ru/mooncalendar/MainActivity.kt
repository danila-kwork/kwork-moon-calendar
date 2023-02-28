package ru.mooncalendar

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.screens.*
import ru.mooncalendar.ui.theme.*

enum class BottomBar(val text: String, val iconId: Int) {
    CALENDAR("Календарь", R.drawable.moon),
    PREMIUM("Подписка", R.drawable.premium),
    SETTINGS("Настройки", R.drawable.setting)
}

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainActivityViewModel

    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            MainActivityViewModel.Factory(this.application)
        )[MainActivityViewModel::class.java]

        setContent {
            MoonCalendarTheme {
                val navController = rememberNavController()
                val auth = remember(Firebase::auth)
                var bottomBar by remember { mutableStateOf(BottomBar.CALENDAR) }

                val systemUiController = rememberSystemUiController()
                val secondaryBackground = secondaryBackground()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val push =
                        rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)

                    LaunchedEffect(key1 = Unit, block = {
                        push.launchPermissionRequest()
                    })
                }


                LaunchedEffect(key1 = Unit, block = {
                    systemUiController.setNavigationBarColor(
                        color = secondaryBackground
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
                                            BottomBar.PREMIUM -> {
                                                if(auth.currentUser != null){
                                                    navController.navigate("profile_screen")
                                                }else {
                                                    navController.navigate("auth_screen")
                                                }
                                            }
                                            BottomBar.SETTINGS ->
                                                navController.navigate("settings_screen")
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

                            composable("signIn"){
                                SignIn(navController = navController)
                            }

                            composable("signOn"){
                                SignOn(navController = navController)
                            }

                            composable("training_manual"){
                                TrainingManualScreen()
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

                            composable("subscription_statement_screen"){
                                SubscriptionStatementScreen()
                            }

                            composable("settings_screen"){
                                SettingsScreen(navController = navController)
                            }

                            composable("create_info_screen"){
                                CreateInfoScreen(navController = navController)
                            }

                            composable("subscription_info_screen"){
                                SubscriptionInfoScreen()
                            }

                            composable("password_rest_screen"){
                                PasswordRestScreen()
                            }
                        }
                    )
                }
            }
        }
    }
}