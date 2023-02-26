package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.mooncalendar.common.extension.parseToBaseUiDateFormat
import ru.mooncalendar.common.openBrowser
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.auth.model.UserRole
import ru.mooncalendar.data.qiwi.QiwiApi
import ru.mooncalendar.data.qiwi.model.Amount
import ru.mooncalendar.data.qiwi.model.InvoicingBody
import ru.mooncalendar.data.qiwi.model.InvoicingResponse
import ru.mooncalendar.data.qiwi.model.QiwiStatus
import ru.mooncalendar.data.qiwi.retrofit
import ru.mooncalendar.data.subscriptionStatement.SubscriptionStatementRepository
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatement
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatementStatus
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionType
import ru.mooncalendar.data.subscriptionStatement.model.subscriptionTableRows
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor
import ru.mooncalendar.ui.view.BaseLottieAnimation
import ru.mooncalendar.ui.view.LottieAnimationType
import ru.mooncalendar.ui.view.TableCell
import java.util.*

enum class PayType {
    QIWI,
    KASPI
}

@SuppressLint("NewApi", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    var user by remember { mutableStateOf<User?>(null) }
    var subscriptionStatement by remember { mutableStateOf<SubscriptionStatement?>(null) }
    val authRepository = remember(::AuthRepository)
    val subscriptionStatementRepository = remember(::SubscriptionStatementRepository)
    val qiwiApi = remember(::retrofit)
    var qiwiStatus by remember { mutableStateOf<QiwiStatus?>(QiwiStatus.WAITING) }

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
        backgroundColor = primaryBackground()
    ) {
        if(user != null){
            LazyColumn {

                item {
                    if(user?.userRole == UserRole.ADMIN) {
                        TextButton(onClick = {
                            navController.navigate("subscription_statement_screen")
                        }) {
                            Text(
                                text = "Заявки на выплаты (админ)",
                                color = tintColor
                            )
                        }
                    }
                }

                item {
                    if(user?.isSubscription(subscriptionStatement, statusCheck = false) == false) {
                        Subscriptions(
                            qiwiApi = qiwiApi,
                            onSubscription = { numberCard, payTyp, qiwiBillId, type ->
                                subscriptionStatementRepository.create(
                                    SubscriptionStatement(
                                        numberCard = numberCard,
                                        type = type,
                                        payTyp = payTyp,
                                        qiwiBillId = qiwiBillId
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
                                    Spacer(modifier = Modifier.height(15.dp))

                                    when(subscriptionStatement!!.payTyp){
                                        PayType.QIWI -> {
                                            Text(
                                                text = "${subscriptionStatement!!.status.text} подписке '${subscriptionStatement!!.type.title}'," +
                                                        " статус оплаты '${qiwiStatus?.name}'",
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
                                                onClick = {
                                                    scope.launch {
                                                        try {
                                                            val response =
                                                                qiwiApi.getStatus(subscriptionStatement!!.qiwiBillId!!)

                                                            qiwiStatus = response.status.value

                                                            if(qiwiStatus == QiwiStatus.PAID){
                                                                subscriptionStatementRepository.update(
                                                                    subscriptionStatement!!.apply { status = SubscriptionStatementStatus.PAID },
                                                                    onSuccess = {
                                                                        authRepository.getUser(
                                                                            onSuccess = {
                                                                                user = it
                                                                            }
                                                                        )
                                                                    },
                                                                    onFailure = {}
                                                                )
                                                            }

                                                        }catch(e:Exception) {

                                                        }
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = "Проверить статус подписке",
                                                    color = primaryText()
                                                )
                                            }
                                        }
                                        PayType.KASPI -> {

                                            val price = if(subscriptionStatement!!.payTyp == PayType.KASPI)
                                                "${subscriptionStatement!!.type.priceKz.first} тенге"
                                            else
                                                "${subscriptionStatement!!.type.priceRu.first} рублей"

                                            Text(
                                                text = "Перейдите по ссылке и оплатите подписку " +
                                                        "$price\n" +
                                                        "Подтверждение в течение часа",
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
                                                onClick = {
                                                    context.openBrowser("https://pay.kaspi.kz/pay/7dthiaop")
                                                }
                                            ) {
                                                Text(
                                                    text = "Перейти по ссылке",
                                                    color = primaryText()
                                                )
                                            }
                                        }
                                    }

                                    Subscriptions(
                                        qiwiApi = qiwiApi,
                                        onSubscription = { numberCard, payTyp, qiwiBillId, type ->
                                            subscriptionStatementRepository.create(
                                                SubscriptionStatement(
                                                    numberCard = numberCard,
                                                    type = type,
                                                    payTyp = payTyp,
                                                    qiwiBillId = qiwiBillId
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
                            SubscriptionStatementStatus.PAID -> {

                                val debitingFundsDate = user!!.debitingFundsDate(
                                    subscriptionStatement!!.type
                                )?.parseToBaseUiDateFormat()

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
                                    qiwiApi = qiwiApi,
                                    onSubscription = { numberCard, payTyp, qiwiBillId, type ->
                                        subscriptionStatementRepository.create(
                                            SubscriptionStatement(
                                                numberCard = numberCard,
                                                type = type,
                                                payTyp = payTyp,
                                                qiwiBillId = qiwiBillId
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

                item {
                    Spacer(modifier = Modifier.height(50.dp))
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
    qiwiApi: QiwiApi,
    onSubscription: (
        numberCard: String?,
        payTyp: PayType,
        qiwiBillId: String?,
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
            qiwiApi = qiwiApi,
            subscriptionType = subscriptionType!!,
            onDismissRequest = {
                subscriptionType = null
            },
            onSubscription = { numberCard, payTyp, qiwiBillId ->
                onSubscription(numberCard, payTyp, qiwiBillId, subscriptionType!!)
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
                                text = "${item.priceRu.first} ${item.priceRu.second}",
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
    qiwiApi: QiwiApi,
    subscriptionType: SubscriptionType,
    onDismissRequest: () -> Unit,
    onSubscription: (
        numberCard: String?,
        payTyp: PayType,
        qiwiBillId: String?
    ) -> Unit
) {
    val context = LocalContext.current

    var numberCard by remember { mutableStateOf("") }
    var qiwiBillId by remember { mutableStateOf("") }
    var payTyp by remember { mutableStateOf(PayType.QIWI) }
    var invoicingResponse by remember { mutableStateOf<InvoicingResponse?>(null) }

    LaunchedEffect(key1 = Unit, block = {
        try {
            qiwiBillId = UUID.randomUUID().toString()

            val response = qiwiApi.invoicing(qiwiBillId, InvoicingBody(
                amount = Amount(value = subscriptionType.priceRu.first.toFloat())
            ))

            invoicingResponse = response.body()
        }catch (e:Exception){
            println(e)
        }
    })

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = AbsoluteRoundedCornerShape(20.dp),
        backgroundColor = primaryBackground(),
        buttons = {
            Column(
                modifier = Modifier
                    .background(primaryBackground())
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(AbsoluteRoundedCornerShape(20.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    selectedTabIndex = payTyp.ordinal,
                    backgroundColor = primaryBackground(),
                    contentColor = tintColor
                ) {
                    PayType.values().forEach {
                        Tab(
                            selected = payTyp == it,
                            onClick = { payTyp = it },
                            text = {
                                Text(
                                    text = it.name.lowercase(),
                                    color = primaryText()
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                when(payTyp){
                    PayType.QIWI -> {
                        if(invoicingResponse == null){
                            CircularProgressIndicator(color = tintColor)
                        }else {

                            Text(
                                text = "Подписка ${subscriptionType.title} " +
                                        "${subscriptionType.priceRu.first} ${subscriptionType.priceRu.second}",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.W900,
                                modifier = Modifier.padding(5.dp)
                            )

                            Text(
                                text = "Перейдите по ссылке и оплатите подписку,\nпосле оплаты подписка активируеться",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.W300,
                                modifier = Modifier.padding(5.dp)
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
                                onClick = {
                                    onSubscription(null, payTyp, qiwiBillId)
                                    context.openBrowser(invoicingResponse!!.payUrl)
                                }
                            ) {
                                Text(
                                    text = "Оплатить",
                                    color = primaryText()
                                )
                            }
                        }
                    }
                    PayType.KASPI -> {

                        Text(
                            text = "Подписка ${subscriptionType.title} " +
                                    "${subscriptionType.priceKz.first} ${subscriptionType.priceKz.second}",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W900,
                            modifier = Modifier.padding(5.dp)
                        )

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
                                    onSubscription(numberCard, payTyp, null)
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
        }
    )
}