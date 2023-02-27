package ru.mooncalendar.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import io.github.boguszpawlowski.composecalendar.selection.SelectionState
import ru.mooncalendar.common.extension.parseToBaseUiDateFormat
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.moonCalendar.model.getDayNumberColor
import ru.mooncalendar.data.moonCalendar.model.getDayText
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground
import ru.mooncalendar.ui.theme.tintColor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CalendarScreen(
    navController: NavController
) {
    val state = rememberSelectableCalendarState(initialSelectionMode = SelectionMode.Single)

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    var dateInfoDialog by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<User?>(null) }
    val authRepository = remember(::AuthRepository)

    LaunchedEffect(key1 = Unit, block = {
        systemUiController.setStatusBarColor(
            color = primaryBackground
        )

        authRepository.getUser({
            user = it
        })
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = if(state.selectionState.selection.isNotEmpty())
                        state.selectionState.selection.last().toDate().parseToBaseUiDateFormat()
                    else
                        "Выберите дату",
                    color = primaryText())
                },
                backgroundColor = primaryBackground(),
                actions = {
                    AnimatedVisibility(visible = state.selectionState.selection.isNotEmpty()) {
                        TextButton(onClick = {
                            dateInfoDialog = true
                        }) {
                            Text(
                                text = "Далее",
                                color = tintColor
                            )
                        }
                    }
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = primaryBackground()
        ) {

            if(dateInfoDialog){
                DateInfoDialog(
                    user = user,
                    date = state.selectionState.selection.last(),
                    onDismissRequest = { dateInfoDialog = false },
                    onFurtherClick = {
                        dateInfoDialog = false

                        val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = formatDate.format(state.selectionState.selection.last().toDate())
                        navController.navigate("main_screen?dateString=$date")
                    }
                )
            }

            LazyColumn {

                item {
                    Spacer(modifier = Modifier.height(30.dp))

                    SelectableCalendar(
                        modifier = Modifier.fillMaxWidth(),
                        calendarState = state,
                        showAdjacentMonths = false,
                        dayContent = {
                            DefaultDay(state = it, currentDayColor = primaryText())
                        },
                        monthHeader = {
                            DefaultMonthHeader(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(45.dp),
                            shape = AbsoluteRoundedCornerShape(90.dp),
                            backgroundColor = Color.Green
                        ) {}

                        Text(
                            text = "Успех через любовь, принятие, комфорт",
                            color = primaryText(),
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(45.dp),
                            shape = AbsoluteRoundedCornerShape(90.dp),
                            backgroundColor = Color.Yellow
                        ) {}

                        Text(
                            text = "Успех через анализ и планирование",
                            color = primaryText(),
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(45.dp),
                            shape = AbsoluteRoundedCornerShape(90.dp),
                            backgroundColor = Color(0xFF288CE4)
                        ) {}

                        Text(
                            text = "Успех через материальный результат",
                            color = primaryText(),
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(45.dp),
                            shape = AbsoluteRoundedCornerShape(90.dp),
                            backgroundColor = Color.Red
                        ) {}

                        Text(
                            text = "Неблагоприятные дни, обнуление",
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
}

@SuppressLint("NewApi")
@Composable
private fun <T : SelectionState> DefaultDay(
    state: DayState<T>,
    modifier: Modifier = Modifier,
    selectionColor: Color = MaterialTheme.colors.secondary,
    currentDayColor: Color = MaterialTheme.colors.primary,
    onClick: (LocalDate) -> Unit = {},
) {
    val date = state.date
    val selectionState = state.selectionState

    val isSelected = selectionState.isDateSelected(date)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        elevation = if (state.isFromCurrentMonth) 4.dp else 0.dp,
        border = if (state.isCurrentDay) BorderStroke(2.dp, currentDayColor) else null,
        contentColor = if (isSelected) selectionColor else contentColorFor(
            backgroundColor = MaterialTheme.colors.surface
        ),
        backgroundColor = getDayNumberColor(date)
    ) {
        Box(
            modifier = Modifier.clickable {
                onClick(date)
                selectionState.onDateSelected(date)
            },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = date.dayOfMonth.toString())
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun DefaultMonthHeader(
    monthState: MonthState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.testTag("Decrement"),
            onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowLeft,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                contentDescription = "Previous",
            )
        }

        Text(
            modifier = Modifier.testTag("MonthLabel"),
            text = monthState.currentMonth.month
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                .lowercase()
                .replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.h4,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = monthState.currentMonth.year.toString(), style = MaterialTheme.typography.h4)
        IconButton(
            modifier = Modifier.testTag("Increment"),
            onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowRight,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                contentDescription = "Next",
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
private fun DateInfoDialog(
    user: User?,
    date: LocalDate,
    onDismissRequest: () -> Unit,
    onFurtherClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = AbsoluteRoundedCornerShape(15.dp),
        backgroundColor = primaryBackground(),
        text = {
            Column {
                Divider(color = secondaryBackground())

                Text(
                    text = "Общий ${getDayText(date).first}",
                    color = primaryText(),
                    modifier = Modifier.padding(5.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                if(user != null){
                    Text(
                        text = "Ваш личный день ${user.getMyDay(date).first}",
                        color = primaryText(),
                        modifier = Modifier.padding(5.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Divider(color = secondaryBackground())
            }
        },
        buttons = {
            TextButton(
                onClick = onFurtherClick,
                modifier = Modifier.padding(5.dp)
            ) {
                Text(
                    text = "Далее",
                    color = tintColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}