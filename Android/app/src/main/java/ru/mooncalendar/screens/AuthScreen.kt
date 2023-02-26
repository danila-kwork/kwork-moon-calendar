package ru.mooncalendar.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.mooncalendar.R
import ru.mooncalendar.common.openBrowser
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
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

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
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(screenWidthDp.dp)
                        .height((screenHeightDp / 3).dp)
                        .padding(5.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 5.dp
                        )
                        .fillMaxWidth(),
                    onClick = {
                        navController.navigate("signIn")
                    },
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = tintColor
                    )
                ) {
                    Text(
                        text = "Авторизация",
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
                    shape = AbsoluteRoundedCornerShape(15.dp),
                    onClick = {
                        navController.navigate("signOn")
                    }
                ) {
                    Text(
                        text = "Зарегистрироваться",
                        color = primaryText(),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SignOn(
    navController: NavController
) {
    val authRepository = remember(::AuthRepository)

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    var errorMessage by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var privacyPolicy by remember { mutableStateOf(false) }

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
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(screenWidthDp.dp)
                        .height((screenHeightDp / 3).dp)
                        .padding(5.dp)
                )

                Text(
                    text = errorMessage,
                    fontWeight = FontWeight.W900,
                    color = Color.Red,
                    modifier = Modifier.padding(5.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.padding(5.dp),
                    label = { Text(text = "Электронная почта", color = primaryText()) },
                    shape = AbsoluteRoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = primaryBackground(),
                        textColor = primaryText()
                    )
                )

                Text(
                    text = "В формате ДД-ММ-ГГ",
                    modifier = Modifier.padding(end = 5.dp),
                    color = primaryText(),
                    fontWeight = FontWeight.W900
                )

                OutlinedTextField(
                    value = birthday,
                    onValueChange = { birthday = it },
                    modifier = Modifier.padding(5.dp),
                    label = {
                        Text(
                            text = "День рождения",
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        modifier = Modifier.padding(5.dp),
                        checked = privacyPolicy,
                        onCheckedChange = { privacyPolicy = it }
                    )

                    TextButton(onClick = { context.openBrowser("https://firebasestorage.googleapis.com/v0/b/moon--calendar.appspot.com/o/privacyPolicy%2FprivacyPolicy.html?alt=media&token=2787a8fc-2ebe-4efc-b078-1528661f4b49") }) {
                        Text(
                            text = "Политика конфиденциальности",
                            color = primaryText(),
                            modifier = Modifier.padding(5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
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
                            if(privacyPolicy){
                                errorMessage = ""

                                authRepository.reg(
                                    email = email.trim(),
                                    password = password.trim(),
                                    birthday = birthday.trim(),
                                    onSuccess = { navController.navigate("main_screen") },
                                    onFailure = { errorMessage = it  }
                                )
                            }else {
                                errorMessage = "Согласитесь с политикой конфиденциальности"
                            }
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
                        text = "Зарегистрироваться",
                        color = primaryText(),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun SignIn(
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
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(screenWidthDp.dp)
                        .height((screenHeightDp / 3).dp)
                        .padding(5.dp)
                )

                Text(
                    text = errorMessage,
                    fontWeight = FontWeight.W900,
                    color = Color.Red,
                    modifier = Modifier.padding(5.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.padding(5.dp),
                    label = { Text(text = "Электронная почта", color = primaryText()) },
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
                        text = "Авторизация",
                        color = primaryText(),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}