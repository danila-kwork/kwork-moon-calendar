package ru.mooncalendar.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.subscriptionStatement.SubscriptionStatementRepository
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatement
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatementStatus
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor

@Composable
fun SubscriptionStatementScreen(

) {
    val context = LocalContext.current

    val subscriptionStatementRepository = remember(::SubscriptionStatementRepository)
    val authRepository = remember(::AuthRepository)
    var subscriptionStatement by remember { mutableStateOf(emptyList<SubscriptionStatement>()) }

    LaunchedEffect(key1 = Unit, block = {
        subscriptionStatementRepository.getAll(
            onSuccess = {
                subscriptionStatement = it
            }, {}
        )
    })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = primaryBackground()
    ) {
        LazyColumn {
            items(subscriptionStatement) {
                if(it.status == SubscriptionStatementStatus.WAITING){

                    var user by remember { mutableStateOf<User?>(null) }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "id ${it.id}",
                        modifier = Modifier.padding(5.dp),
                        color = primaryText()
                    )

                    Text(
                        text = "user id ${it.userId}",
                        modifier = Modifier.padding(5.dp),
                        color = primaryText()
                    )

                    Text(
                        text = "номер карты ${it.numberCard}",
                        modifier = Modifier.padding(5.dp),
                        color = primaryText()
                    )

                    Text(
                        text = "${it.type.price.first} ${it.type.price.second}",
                        modifier = Modifier.padding(5.dp),
                        color = primaryText()
                    )

                    if(user != null){
                        Text(
                            text = "email ${user!!.email}",
                            modifier = Modifier.padding(5.dp),
                            color = primaryText()
                        )
                    }else {
                        Button(
                            modifier = Modifier.padding(5.dp),
                            shape = AbsoluteRoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = tintColor
                            ),
                            onClick = {
                                authRepository.getUser({ result ->
                                   user = result
                                },{})
                            }
                        ) {
                            Text(
                                text = "Связаться с пользователем",
                                color = primaryText()
                            )
                        }
                    }

                    Button(
                        modifier = Modifier.padding(5.dp),
                        shape = AbsoluteRoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = tintColor
                        ),
                        onClick = {
                            subscriptionStatementRepository.update(
                                subscriptionStatement = it.apply { status = SubscriptionStatementStatus.PAID },
                                onSuccess = {
                                    Toast.makeText(context, "Успешно !", Toast.LENGTH_SHORT).show()
                                    subscriptionStatementRepository.getAll({
                                        subscriptionStatement = it
                                    },{})
                                },
                                onFailure = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    ) {
                        Text(
                            text = "Подвердить",
                            color = primaryText()
                        )
                    }

                    Divider(color = tintColor)
                }
            }
        }
    }
}