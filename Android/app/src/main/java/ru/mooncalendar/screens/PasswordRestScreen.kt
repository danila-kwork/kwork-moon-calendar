package ru.mooncalendar.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.mooncalendar.R
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor

@Composable
fun PasswordRestScreen(

) {
    val context = LocalContext.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    val authRepository = remember(::AuthRepository)
    var email by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = primaryBackground()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .width(screenWidthDp.dp)
                    .height((screenHeightDp / 3).dp)
                    .padding(5.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = 5.dp
                    )
                    .fillMaxWidth(),
                onClick = {
                    try {
                        authRepository.passwordReset(email, onSuccess = {
                            Toast.makeText(context, "Письмо отправлено", Toast.LENGTH_SHORT).show()
                        }, onFailure = {
                            Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
                        })
                    }catch (e:Exception) {
                        Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = AbsoluteRoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = tintColor
                )
            ) {
                Text(
                    text = "Отправить письмо для сброса пароля",
                    color = primaryText(),
                    modifier = Modifier.padding(5.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}