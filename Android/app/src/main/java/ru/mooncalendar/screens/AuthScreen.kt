package ru.mooncalendar.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.mooncalendar.data.auth.AuthRepository

@Composable
fun AuthScreen(
    navController: NavController
) {
    val authRepository = remember(::AuthRepository)

    var errorMessage by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage,
            fontWeight = FontWeight.W900,
            color = Color.Red,
            modifier = Modifier.padding(5.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.padding(5.dp),
            label = { Text(text = "Пароль") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.padding(5.dp),
            label = { Text(text = "Email") }
        )

        OutlinedButton(
            modifier = Modifier.padding(5.dp),
            onClick = {
                try {
                    authRepository.auth(
                        email = email,
                        password = password,
                        onSuccess = { navController.navigate("main_screen") },
                        onFailure = { errorMessage = it  }
                    )
                }catch (e:Exception){
                    errorMessage = e.message.toString()
                }
            }
        ) {
            Text(text = "Авторезироваться")
        }

        OutlinedButton(
            modifier = Modifier.padding(5.dp),
            onClick = {
                try {
                    authRepository.reg(
                        email = email,
                        password = password,
                        onSuccess = { navController.navigate("main_screen") },
                        onFailure = { errorMessage = it  }
                    )
                }catch (e:Exception){
                    errorMessage = e.message.toString()
                }
            }
        ) {
            Text(text = "Зарегестрироваться")
        }
    }
}