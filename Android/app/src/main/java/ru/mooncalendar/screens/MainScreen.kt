@file:OptIn(ExperimentalMaterialApi::class)

package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.skydoves.cloudy.Cloudy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mooncalendar.R
import ru.mooncalendar.common.extension.*
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.auth.model.getAdvice
import ru.mooncalendar.data.database.MainDatabase
import ru.mooncalendar.data.database.notes.Note
import ru.mooncalendar.data.info.Info
import ru.mooncalendar.data.info.InfoRepository
import ru.mooncalendar.data.moonCalendar.MoonCalendarRepository
import ru.mooncalendar.data.moonCalendar.model.*
import ru.mooncalendar.data.subscriptionStatement.SubscriptionStatementRepository
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatement
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionType
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground
import ru.mooncalendar.ui.theme.tintColor
import ru.mooncalendar.ui.view.ExpandableCardView
import java.text.SimpleDateFormat
import java.util.*

enum class Tab(val text: String) {
    DESCRIPTION("Описание"),
    RECOMMENDATIONS("Рекомендации"),
    MY_DESCRIPTION("Личные периоды"),
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
    val owner = LocalLifecycleOwner.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    val scope = rememberCoroutineScope()
    val auth = remember(Firebase::auth)
    val moonCalendarRepository = remember { MoonCalendarRepository() }
    val authRepository = remember { AuthRepository() }
    val infoRepository = remember(::InfoRepository)
    val noteDao = remember(MainDatabase.getInstance(context)::noteDao)
    val subscriptionStatementRepository = remember(::SubscriptionStatementRepository)
    var moonCalendar by remember { mutableStateOf<List<MoonCalendar>>(emptyList()) }
    var subscriptionStatement by remember { mutableStateOf<SubscriptionStatement?>(null) }
    var noteDescEditDialog by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf(Tab.DESCRIPTION) }

    var date by remember { mutableStateOf(Date()) }
    var user by remember { mutableStateOf<User?>(null) }
    var note by remember { mutableStateOf<Note?>(null) }
    var info by remember { mutableStateOf<Info?>(null) }

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    var isSubscription by remember { mutableStateOf(false) }
    var recommendationsTabVisibility by remember { mutableStateOf(false) }
    var pedometerTabVisibility by remember { mutableStateOf(false) }
    var myYearTabVisibility by remember { mutableStateOf(false) }
    var myMonthTabVisibility by remember { mutableStateOf(false) }
    var infoVisibility by remember { mutableStateOf(false) }

    var blur by remember { mutableStateOf(false) }

    noteDao.getByDate(date = date.parseToBaseDateFormat()).observe(owner){
        note = it
    }

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

        infoVisibility = user != null && isSubscription && subscriptionStatement != null
                && (subscriptionStatement?.type != SubscriptionType.LITE_MIN
                && subscriptionStatement?.type != SubscriptionType.LITE_MAX)
    }

    LaunchedEffect(key1 = moonCalendar.lastOrNull(), block = {
        moonCalendar.lastOrNull()?.let {
            systemUiController.setStatusBarColor(
                Color(0xFF166239)//it.moonCalendarColor()
            )
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

        infoRepository.get(date = date.parseToBaseDateFormat()) { info = null; info = it }

        moonCalendarRepository.getMoonCalendar(
            filterDate = date,
            onSuccess = { moonCalendar = it },
            onFailure = {
                Toast.makeText(context, "error: $it", Toast.LENGTH_SHORT).show()
            }
        )
    })

    LaunchedEffect(user,isSubscription, block = {
        delay(1000L)
        blur = !isSubscription
    })

    Scaffold {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = primaryBackground()
        ) {
            if(noteDescEditDialog){
                NoteDescEdit(
                    currentDesc = note?.description ?: "",
                    onDismissRequest = { noteDescEditDialog = false },
                    editDesc = { desc ->
                        scope.launch {
                            noteDao.upsert(
                                Note(
                                    date = date.parseToBaseDateFormat(),
                                    description = desc
                                )
                            )

                            noteDescEditDialog = false
                        }
                    }
                )
            }

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
                            tint = Color(0xFF166239) //moonCalendar.firstOrNull()?.moonCalendarColor()
                                //?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
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
                                    if(user != null && isSubscription){
                                        navController.navigate("calendar_screen")
                                    }else {
                                        if(auth.currentUser == null)
                                            navController.navigate("auth_screen")
                                        else
                                            navController.navigate("profile_screen")
                                    }
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
                            text = date.parseToBaseUiDateFormat(),
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
                                            Tab.PEDOMETER -> pedometerTabVisibility
                                            Tab.MY_DESCRIPTION -> myMonthTabVisibility
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    if(blur && tab == Tab.DESCRIPTION){

                        val text = getDayTextShort(date = date.toLocalDate()).second

                        Box {
                            Text(
                                text = text.ifEmpty { getShortRecommendations(date = date.toLocalDate()) },
                                modifier = Modifier.padding(
                                    vertical = 2.dp,
                                    horizontal = 15.dp
                                ),
                                color = primaryText()
                            )
                        }
                    }
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
                        Tab.DESCRIPTION -> {

                            if(infoVisibility){
                                info?.let {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        shape = AbsoluteRoundedCornerShape(15.dp),
                                        backgroundColor = Color(0xFFFFF1E4)
                                    ) {
                                        Column {
                                            Text(
                                                text = "Aффирмация дня",
                                                fontWeight = FontWeight.W900,
                                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 3.dp),
                                                fontSize = 20.sp,
                                                color = primaryText()
                                            )

                                            Text(
                                                text = it.info,
                                                color = primaryText(),
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }

                            if(blur){
                                Box {
                                    Cloudy(
                                        modifier = Modifier
                                            .clickable {
                                                if (auth.currentUser == null)
                                                    navController.navigate("auth_screen")
                                                else
                                                    navController.navigate("profile_screen")
                                            }
                                            .padding(
                                                vertical = 2.dp,
                                                horizontal = 15.dp
                                            ),
                                        radius = 15,
                                        key1 = blur,
                                        key2 = date
                                    ) {
                                        Text(
                                            text = getRecommendations(date = date.toLocalDate()),
                                            color = primaryText()
                                        )
                                    }
                                }
                            }

                            if(!blur){
                                Text(
                                    text = getDayText(date = date.toLocalDate()).second,
                                    modifier = Modifier.padding(
                                        horizontal = 15.dp
                                    ),
                                    color = primaryText()
                                )

                                Text(
                                    text = getRecommendations(date = date.toLocalDate()),
                                    modifier = Modifier.padding(
                                        vertical = 2.dp,
                                        horizontal = 15.dp
                                    ),
                                    color = primaryText()
                                )
                            }

                            if(date.toLocalDate().dayOfMonth in listOf(10,20,30)){
                                Text(
                                    text = "Даже если в сумме сегодняшняя дата дает благоприятное число, по науке Сюцай это день, когда результаты наших действий могут быть обнулены!\n" +
                                            "\n" +
                                            "Следите за эмоциональным и физическим здоровьем, сохраняйте отношения!\n" +
                                            "\n" +
                                            "НЕ РЕКОМЕНДУЕТСЯ\n" +
                                            "\n" +
                                            "Не стоит подписывать значимые бумаги, крупные покупки, а также принять важные решения\n" +
                                            "\n" +
                                            "Не идти на поводу своего ЭГО, быть жесткими и азартными",
                                    modifier = Modifier.padding(
                                        vertical = 2.dp,
                                        horizontal = 15.dp
                                    ),
                                    color = primaryText()
                                )
                            }

                            Text(
                                text = "Заметка",
                                fontWeight = FontWeight.W900,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 3.dp),
                                fontSize = 26.sp
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                shape = AbsoluteRoundedCornerShape(15.dp),
                                backgroundColor = tintColor,
                                onClick = {
                                    noteDescEditDialog = true
                                }
                            ) {
                                Text(
                                    text = note?.description
                                        ?: "Пример текста: Что вы ощущаете и какие у вас отношения с окружающими в этот день?",
                                    color = primaryText(),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        Tab.PEDOMETER -> PedometerScreen(
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                        )
                        Tab.MY_DESCRIPTION -> {
                            if(user != null){
                                val localDate = remember(date::toLocalDate)

                                Divider(color = secondaryBackground())

                                ExpandableCardView(
                                    title = "Личный год",
                                    body = user!!.getMyYear(date.toLocalDate().year).second
                                )

                                Divider(color = secondaryBackground())

                                ExpandableCardView(
                                    title = "Личный месяц",
                                    body = user!!.getMyMonth(
                                        localDate.year,
                                        localDate.month.value
                                    ).second
                                )

                                Divider(color = secondaryBackground())
                                ExpandableCardView(
                                    title = "Личный день",
                                    body = user!!.getMyDay(
                                        localDate
                                    ).second
                                )
                                Divider(color = secondaryBackground())

                                Spacer(modifier = Modifier.height(5.dp))
                            }
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
                Color(0xFFFFE8D7),
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

@Composable
private fun NoteDescEdit(
    currentDesc: String,
    onDismissRequest: () -> Unit,
    editDesc: (String) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    val textFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit, block = {
        textFieldFocusRequester.requestFocus()
        desc = currentDesc
    })

    AlertDialog(
        onDismissRequest = onDismissRequest,
        backgroundColor = primaryBackground(),
        shape = AbsoluteRoundedCornerShape(15.dp),
        buttons = {
            OutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .heightIn(min = 150.dp)
                    .focusRequester(textFieldFocusRequester),
                value = desc,
                onValueChange = { desc = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = primaryBackground(),
                    textColor = primaryText()
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = {
                    editDesc(desc)
                })
            )
        }
    )
}