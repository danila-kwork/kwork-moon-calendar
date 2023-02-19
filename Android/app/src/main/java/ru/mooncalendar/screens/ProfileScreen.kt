package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.mooncalendar.common.extension.parseToBaseDateFormat
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.auth.model.UserRole
import ru.mooncalendar.data.subscriptionStatement.SubscriptionStatementRepository
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatement
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatementStatus
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionType
import ru.mooncalendar.data.subscriptionStatement.model.subscriptionTableRows
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground
import ru.mooncalendar.ui.theme.tintColor
import ru.mooncalendar.ui.view.BaseLottieAnimation
import ru.mooncalendar.ui.view.LottieAnimationType
import ru.mooncalendar.ui.view.TableCell

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
    var subscriptionStatement by remember { mutableStateOf<SubscriptionStatement?>(null) }
    val authRepository = remember(::AuthRepository)
    val subscriptionStatementRepository = remember(::SubscriptionStatementRepository)

    LaunchedEffect(key1 = Unit, block = {
        systemUiController.setStatusBarColor(
            color = primaryBackground
        )

        authRepository.getUser(
            onSuccess = {
                user = it
            }
        )

        subscriptionStatementRepository.getByUserId(
            onSuccess = {
                subscriptionStatement = it
            },
            onFailure = {  }
        )
    })

    Scaffold(
        backgroundColor = primaryBackground(),
    ) {
        if(user != null){
            LazyColumn {

                item {
                    if(user?.userRole == UserRole.ADMIN) {
                        TextButton(onClick = {
                            navController.navigate("subscription_statement_screen")
                        }) {
                            Text(
                                text = "Админ",
                                color = tintColor
                            )
                        }
                    }
                }

                item {
                    if(user?.isSubscription(subscriptionStatement, statusCheck = false) == false) {
                        Subscriptions(
                            onSubscription = { numberCard, type ->
                                subscriptionStatementRepository.create(
                                    SubscriptionStatement(
                                        numberCard = numberCard,
                                        type = type
                                    ),
                                    {
                                        subscriptionStatementRepository.getByUserId(
                                            onSuccess = { subscriptionStatement = it },
                                            onFailure = {}
                                        )

                                        authRepository.subscription({},{})
                                    },
                                    {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                                authRepository.subscription(
                                    onSuccess = {
                                        authRepository.getUser(onSuccess = { user = it })
                                    },
                                    onFailure = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    }
                }

                item {
                    if(
                        user?.isSubscription(subscriptionStatement, statusCheck = false) != false
                    ) {
                        when(subscriptionStatement?.status){
                            SubscriptionStatementStatus.WAITING -> {
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
                                        text = "Перейдите по ссылке и оплатите подписку " +
                                                "${subscriptionStatement!!.type.price.first} тенге\n" +
                                                "После ждите подвержения оплаты\n" +
                                                "Подверждения прийдет в течения трех дней",
                                        color = primaryText(),
                                        fontWeight = FontWeight.W900,
                                        modifier = Modifier.padding(5.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    Button(
                                        modifier = Modifier.padding(5.dp),
                                        shape = AbsoluteRoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = tintColor
                                        ),
                                        onClick = {  }
                                    ) {
                                        Text(
                                            text = "Перейти по ссылке",
                                            color = primaryText()
                                        )
                                    }
                                }
                            }
                            SubscriptionStatementStatus.PAID -> {

                                val debitingFundsDate = user!!.debitingFundsDate(
                                    subscriptionStatement!!.type
                                )?.parseToBaseDateFormat()

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
                                        text = if(debitingFundsDate == null)
                                            "Подписка оформлена"
                                        else
                                            "Подписка оформлена до $debitingFundsDate",
                                        color = primaryText(),
                                        fontWeight = FontWeight.W900,
                                        modifier = Modifier.padding(5.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            null -> {
                                Subscriptions(
                                    onSubscription = { numberCard, type ->
                                        subscriptionStatementRepository.create(
                                            SubscriptionStatement(
                                                numberCard = numberCard,
                                                type = type
                                            ),
                                            {
                                                subscriptionStatementRepository.getByUserId(
                                                    onSuccess = { subscriptionStatement = it },
                                                    onFailure = {}
                                                )

                                                authRepository.subscription({},{})
                                            },
                                            {
                                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                        authRepository.subscription(
                                            onSuccess = {
                                                authRepository.getUser(onSuccess = { user = it })
                                            },
                                            onFailure = {
                                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }else {
            BaseLottieAnimation(
                type = LottieAnimationType.Loading,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Subscriptions(
    onSubscription: (
        numberCard: String,
        type: SubscriptionType
    ) -> Unit
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    var subscriptionType by remember { mutableStateOf<SubscriptionType?>(null) }

    val tableRows = subscriptionTableRows

    val column1Weight = (screenWidthDp / 2.5).dp
    val column2Weight = (screenWidthDp / 5).dp

    if(subscriptionType != null){
        BayDialog(
            onDismissRequest = {
                subscriptionType = null
            },
            onSubscription = {
                onSubscription(it, subscriptionType!!)
                subscriptionType = null
            }
        )
    }

    Column {

        Spacer(modifier = Modifier.height(50.dp))

        LazyRow {
            item {
                SubscriptionType.values().forEach { item ->
                    Card(
                        modifier = Modifier
                            .padding(5.dp)
                            .height(180.dp),
                        shape = AbsoluteRoundedCornerShape(10.dp),
                        backgroundColor = item.color,
                        onClick = { subscriptionType = item }
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.padding(10.dp),
                                color = primaryText(),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "${item.price.first} ${item.price.second}",
                                fontWeight = FontWeight.W900,
                                modifier = Modifier.padding(10.dp),
                                color = primaryText(),
                                textAlign = TextAlign.Center
                            )

                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Button(
                                    onClick = { subscriptionType = item },
                                    modifier = Modifier.padding(10.dp),
                                    shape = AbsoluteRoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = tintColor
                                    )
                                ) {
                                    Text(
                                        text = "Купить",
                                        color = primaryText()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        LazyRow {
            item {
                TableCell(text = "", width = column1Weight)
                TableCell(text = "Lite", width = column2Weight)
                TableCell(text = "Silver", width = column2Weight)
                TableCell(text = "Gold", width = column2Weight)
            }
        }

        tableRows.forEach { row ->
            LazyRow {
                item {
                    TableCell(text = row.text, width = column1Weight)
                    TableCell(
                        text = if(row.liteSubscription)
                            "✅"
                        else
                            "❌",
                        width = column2Weight
                    )
                    TableCell(
                        text = if(row.silverSubscription)
                            "✅"
                        else
                            "❌",
                        width = column2Weight
                    )
                    TableCell(
                        text = if(row.goldSubscription)
                            "✅"
                        else
                            "❌",
                        width = column2Weight
                    )
                }
            }
        }
    }
}

@Composable
private fun BayDialog(
    onDismissRequest: () -> Unit,
    onSubscription: (numberCard: String) -> Unit
) {
    var numberCard by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest
    ){
        Column(
            modifier = Modifier
                .background(primaryBackground())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = numberCard,
                onValueChange = { numberCard = it },
                modifier = Modifier.padding(5.dp),
                label = { Text(text = "Номер карты", color = primaryText()) },
                shape = AbsoluteRoundedCornerShape(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = primaryBackground(),
                    textColor = primaryText()
                )
            )

            Spacer(modifier = Modifier.padding(10.dp))

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
                onClick = {
                    if(numberCard.isNotEmpty())
                        onSubscription(numberCard)
                }
            ) {
                Text(
                    text = "Оформить подписку",
                    color = primaryText()
                )
            }
        }
    }
}