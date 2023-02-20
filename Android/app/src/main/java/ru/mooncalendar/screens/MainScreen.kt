@file:OptIn(ExperimentalMaterialApi::class)

package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import ru.mooncalendar.R
import ru.mooncalendar.common.extension.parseToBaseDateFormat
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.common.extension.toLocalDate
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.auth.model.getAdvice
import ru.mooncalendar.data.moonCalendar.MoonCalendarRepository
import ru.mooncalendar.data.moonCalendar.model.MoonCalendar
import ru.mooncalendar.data.moonCalendar.model.getDayText
import ru.mooncalendar.data.moonCalendar.model.getRecommendations
import ru.mooncalendar.data.subscriptionStatement.SubscriptionStatementRepository
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatement
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionType
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground
import ru.mooncalendar.ui.theme.tintColor
import java.text.SimpleDateFormat
import java.util.*

enum class Tab(val text: String) {
    DESCRIPTION("Описания"),
    RECOMMENDATIONS("Рекомендации"),
    MY_MONTH("Личный месяц"),
    MY_YEAR("Личный год"),
    PEDOMETER("Шагомер")
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "NewApi")
@Composable
fun MainScreen(
    navController: NavController,
    dateString: String? = null
) {
    val context = LocalContext.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    val moonCalendarRepository = remember { MoonCalendarRepository() }
    val authRepository = remember { AuthRepository() }
    val subscriptionStatementRepository = remember(::SubscriptionStatementRepository)
    var moonCalendar by remember { mutableStateOf<List<MoonCalendar>>(emptyList()) }
    var subscriptionStatement by remember { mutableStateOf<SubscriptionStatement?>(null) }

    var tab by remember { mutableStateOf(Tab.DESCRIPTION) }

    var date by remember { mutableStateOf(Date()) }
    var user by remember { mutableStateOf<User?>(null) }

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    var isSubscription by remember { mutableStateOf(false) }
    var recommendationsTabVisibility by remember { mutableStateOf(false) }
    var pedometerTabVisibility by remember { mutableStateOf(false) }
    var myYearTabVisibility by remember { mutableStateOf(false) }
    var myMonthTabVisibility by remember { mutableStateOf(false) }

    LaunchedEffect(user, subscriptionStatement) {
        delay(1000L)
        isSubscription = user?.isSubscription(subscriptionStatement = subscriptionStatement) ?: false

        recommendationsTabVisibility = user != null && isSubscription
        pedometerTabVisibility = user != null && isSubscription

        myYearTabVisibility = user != null && isSubscription && subscriptionStatement != null
                && (subscriptionStatement?.type != SubscriptionType.LITE_MIN
                && subscriptionStatement?.type != SubscriptionType.LITE_MAX)

        myMonthTabVisibility = user != null && isSubscription && subscriptionStatement != null
                && (subscriptionStatement?.type != SubscriptionType.LITE_MIN
                && subscriptionStatement?.type != SubscriptionType.LITE_MAX)
    }

    LaunchedEffect(key1 = moonCalendar.lastOrNull(), block = {
        moonCalendar.lastOrNull()?.let {
            systemUiController.setStatusBarColor(it.moonCalendarColor())
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        systemUiController.setStatusBarColor(color = primaryBackground)

        if(dateString != null){
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)!!
        }

        authRepository.getUser(onSuccess = { user = it })

        subscriptionStatementRepository.getByUserId(
            onSuccess = {
                subscriptionStatement = it
            },
            onFailure = {  }
        )
    })

    LaunchedEffect(key1 = date, block = {
        moonCalendarRepository.getMoonCalendar(
            filterDate = date,
            onSuccess = { moonCalendar = it },
            onFailure = {
                Toast.makeText(context, "error: $it", Toast.LENGTH_SHORT).show()
            }
        )
    })

    Scaffold {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = primaryBackground()
        ) {
            LazyColumn {

                item {
                    Box {
                        Icon(
                            bitmap = Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(context.resources, R.drawable.ellipse),
                                screenWidthDp,
                                (screenHeightDp / 8),
                                false
                            ).asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .height((screenHeightDp / 8).dp)
                                .width(screenWidthDp.dp),
                            tint = moonCalendar.firstOrNull()?.moonCalendarColor()
                                ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(5.dp)
                            )

                            Text(
                                text = if(user != null)
                                    "${getDayText(date.toLocalDate()).first}\nЛичный день ${user!!.getMyDay(date.toLocalDate()).first}"
                                else
                                    getDayText(date.toLocalDate()).first,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W900
                            )

                            Card(
                                modifier = Modifier.padding(10.dp),
                                shape = AbsoluteRoundedCornerShape(90.dp),
                                backgroundColor = tintColor,
                                onClick = {
                                    navController.navigate("calendar_screen")
                                }
                            ){
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(5.dp),
                                    tint = primaryText()
                                )
                            }
                        }
                    }
                }

                item {

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            modifier = Modifier.padding(5.dp),
                            onClick = {
                                date = date.toLocalDate().minusDays(1).toDate()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = tintColor
                            )
                        }

                        Text(
                            text = date.parseToBaseDateFormat(),
                            fontWeight = FontWeight.W900,
                            modifier = Modifier.padding(5.dp)
                        )

                        IconButton(
                            modifier = Modifier.padding(5.dp),
                            onClick = {
                                date = date.toLocalDate().plusDays(1).toDate()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = tintColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }

                item {

                    Spacer(modifier = Modifier.height(5.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Tab.values().forEach {
                                TabItem(
                                    tab = it,
                                    select = tab == it,
                                    onClick = { tab = it },
                                    visibilityItem = {
                                        when(it) {
                                            Tab.DESCRIPTION -> true
                                            Tab.RECOMMENDATIONS -> recommendationsTabVisibility
                                            Tab.MY_YEAR -> myYearTabVisibility
                                            Tab.PEDOMETER -> pedometerTabVisibility
                                            Tab.MY_MONTH -> myMonthTabVisibility
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }

                item {
                    when(tab) {
                        Tab.RECOMMENDATIONS -> {
                            val advice = getAdvice(date = date.toLocalDate())

                            Spacer(modifier = Modifier.height(5.dp))

                            repeat(advice.size) {
                                val adviceItem = advice[it]

                                Text(
                                    text = adviceItem.parameter,
                                    fontWeight = FontWeight.W900,
                                    modifier = Modifier.padding(5.dp)
                                )

                                Text(
                                    text = adviceItem.state.text,
                                    fontWeight = FontWeight.W100,
                                    modifier = Modifier.padding(5.dp)
                                )

                                Divider(color = secondaryBackground())
                            }
                        }
                        Tab.MY_YEAR -> {
                            if(user != null){

                                Text(
                                    text = user!!.getMyYear(date.toLocalDate().year).second,
                                    modifier = Modifier.padding(
                                        vertical = 5.dp,
                                        horizontal = 15.dp
                                    ),
                                    color = primaryText()
                                )

                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                        Tab.DESCRIPTION -> {
                            Text(
                                text = getDayText(date = date.toLocalDate()).second,
                                modifier = Modifier.padding(
                                    vertical = 5.dp,
                                    horizontal = 15.dp
                                ),
                                color = primaryText()
                            )

                            Text(
                                text = getRecommendations(date = date.toLocalDate()),
                                modifier = Modifier.padding(
                                    vertical = 5.dp,
                                    horizontal = 15.dp
                                ),
                                color = primaryText()
                            )
                        }
                        Tab.PEDOMETER -> PedometerScreen(
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                        )
                        Tab.MY_MONTH -> {

                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun TabItem(
    tab: Tab,
    select: Boolean,
    visibilityItem: () -> Boolean,
    onClick: () -> Unit
) {
    if(visibilityItem.invoke()){
        Card(
            modifier = Modifier.padding(5.dp),
            shape = AbsoluteRoundedCornerShape(10.dp),
            backgroundColor = if(select)
                tintColor
            else
                secondaryBackground(),
            onClick = onClick
        ){
            Text(
                text = tab.text,
                color = primaryText(),
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}