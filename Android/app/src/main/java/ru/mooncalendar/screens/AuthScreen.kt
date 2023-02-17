package ru.mooncalendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor
import ru.mooncalendar.ui.view.BaseLottieAnimation
import ru.mooncalendar.ui.view.LottieAnimationType

@Composable
fun AuthScreen(
    navController: NavController
) {
    val authRepository = remember(::AuthRepository)

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    var errorMessage by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit, block = {
        systemUiController.setStatusBarColor(
            color = primaryBackground
        )
    })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = primaryBackground()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                BaseLottieAnimation(
                    type = LottieAnimationType.Welcome,
                    modifier = Modifier
                        .width(screenWidthDp.dp)
                        .height((screenHeightDp / 3).dp)
                        .padding(5.dp)
                )

                Text(
                    text = errorMessage,
                    fontWeight = FontWeight.W900,
                    color = Color.Red,
                    modifier = Modifier.padding(5.dp),
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.padding(5.dp),
                    label = { Text(text = "Электроная почта", color = primaryText()) },
                    shape = AbsoluteRoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = primaryBackground(),
                        textColor = primaryText()
                    )
                )

                OutlinedTextField(
                    value = birthday,
                    onValueChange = { birthday = it },
                    modifier = Modifier.padding(5.dp),
                    label = {
                        Text(
                            text = "День рождения 19-05-2005",
                            color = primaryText()
                        )
                    },
                    shape = AbsoluteRoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = primaryBackground(),
                        textColor = primaryText()
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.padding(5.dp),
                    label = { Text(text = "Пароль", color = primaryText()) },
                    shape = AbsoluteRoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = primaryBackground(),
                        textColor = primaryText()
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 5.dp
                        )
                        .fillMaxWidth(),
                    onClick = {
                        try {
                            errorMessage = ""

                            authRepository.auth(
                                email = email.trim(),
                                password = password.trim(),
                                onSuccess = { navController.navigate("main_screen") },
                                onFailure = { errorMessage = it  }
                            )
                        }catch (e:Exception){
                            errorMessage = e.message.toString()
                        }
                    },
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = tintColor
                    )
                ) {
                    Text(
                        text = "Авторезироваться",
                        color = primaryText(),
                        modifier = Modifier.padding(5.dp)
                    )
                }

                Button(
                    modifier = Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 5.dp
                        )
                        .fillMaxWidth(),
                    onClick = {
                        try {
                            errorMessage = ""

                            authRepository.reg(
                                email = email.trim(),
                                password = password.trim(),
                                birthday = birthday.trim(),
                                onSuccess = { navController.navigate("main_screen") },
                                onFailure = { errorMessage = it  }
                            )
                        }catch (e:Exception){
                            errorMessage = e.message.toString()
                        }
                    },
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = tintColor
                    )
                ) {
                    Text(
                        text = "Зарегестрироваться",
                        color = primaryText(),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}