package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.mooncalendar.common.extension.parseToBaseDateFormat
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor
import ru.mooncalendar.ui.view.BaseLottieAnimation
import ru.mooncalendar.ui.view.LottieAnimationType

@SuppressLint("NewApi", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    var user by remember { mutableStateOf<User?>(null) }
    val authRepository = remember(::AuthRepository)

    LaunchedEffect(key1 = Unit, block = {
        systemUiController.setStatusBarColor(
            color = primaryBackground
        )

        authRepository.getUser(
            onSuccess = { user = it }
        )
    })

    Scaffold(
        backgroundColor = primaryBackground(),
        topBar = {
            TopAppBar(
                backgroundColor = primaryBackground(),
                title = {
                    Text(
                        text = user?.email ?: "",
                        color = primaryText()
                    )
                }
            )
        }
    ) {
        if(user != null){

            if(user?.isSubscription() == true) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    BaseLottieAnimation(
                        type = LottieAnimationType.SUBSCRIPTION,
                        modifier = Modifier
                            .width(screenWidthDp.dp)
                            .height((screenHeightDp / 3).dp)
                            .padding(5.dp)
                    )

                    Text(
                        text = "Подписка оформлена до ${user!!.debitingFundsDate().parseToBaseDateFormat()}",
                        color = primaryText(),
                        fontWeight = FontWeight.W900,
                        modifier = Modifier.padding(5.dp),
                        textAlign = TextAlign.Center
                    )

//                Button(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(
//                            horizontal = 20.dp,
//                            vertical = 10.dp
//                        ),
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = tintColor
//                    ),
//                    onClick = {
//                        Toast.makeText(context, "Подписка отменена", Toast.LENGTH_SHORT).show()
//                    }
//                ) {
//                    Text(
//                        text = "Отменить подписку",
//                        color = primaryText()
//                    )
//                }
                }
            }else {
                Subscription(
                    onSubscription = {
                        authRepository.subscription(
                            onSuccess = {
                                authRepository.getUser(
                                    onSuccess = { user = it }
                                )
                                Toast.makeText(context, "Успешно !", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }

        }else {
            BaseLottieAnimation(
                type = LottieAnimationType.Loading,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun Subscription(
    onSubscription: () -> Unit
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        BaseLottieAnimation(
            type = LottieAnimationType.SUBSCRIPTION,
            modifier = Modifier
                .width(screenWidthDp.dp)
                .height((screenHeightDp / 3).dp)
                .padding(5.dp)
        )

        Text(
            text = "Подписка\n150 рублей в месяц",
            fontWeight = FontWeight.W900,
            modifier = Modifier.padding(),
            color = primaryText(),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Вы получите\n" +
                    "1. Доступ к параметрам\n" +
                    "2. Выбор даты в календаре",
            fontWeight = FontWeight.W900,
            modifier = Modifier.padding(),
            color = primaryText(),
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = tintColor
            ),
            shape = AbsoluteRoundedCornerShape(10.dp),
            onClick = onSubscription
        ) {
            Text(
                text = "Оформить подписку",
                color = primaryText()
            )
        }
    }
}