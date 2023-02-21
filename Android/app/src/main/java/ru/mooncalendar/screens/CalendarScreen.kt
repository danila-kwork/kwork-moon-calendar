package ru.mooncalendar.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import io.github.boguszpawlowski.composecalendar.selection.SelectionState
import ru.mooncalendar.common.extension.parseToBaseUiDateFormat
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.data.auth.model.getAdvice
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CalendarScreen(
    navController: NavController
) {
    val state = rememberSelectableCalendarState(initialSelectionMode = SelectionMode.Single)

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    LaunchedEffect(key1 = Unit, block = {
        systemUiController.setStatusBarColor(
            color = primaryBackground
        )
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
                            val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val date = formatDate.format(state.selectionState.selection.last().toDate())
                            navController.navigate("main_screen?dateString=$date")
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
            LazyColumn {

                item {
                    Spacer(modifier = Modifier.height(30.dp))

                    SelectableCalendar(
                        modifier = Modifier.fillMaxWidth(),
                        calendarState = state,
                        dayContent = {
                            DefaultDay(state = it)
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
                            text = "Благоприятный день",
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
                            backgroundColor = Color.Gray
                        ) {}

                        Text(
                            text = "Нейтральный день",
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
                            text = "Неблагоприятный день",
                            color = primaryText(),
                            modifier = Modifier.padding(5.dp)
                        )
                    }
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

    val advice = getAdvice(date).distinctBy { it.state }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        elevation = if (state.isFromCurrentMonth) 4.dp else 0.dp,
        border = if (state.isCurrentDay) BorderStroke(1.dp, currentDayColor) else null,
        contentColor = if (isSelected) selectionColor else contentColorFor(
            backgroundColor = MaterialTheme.colors.surface
        ),
        backgroundColor = if(advice.size == 1){
            advice.last().state.color
        }else {
            Color.Gray
        }
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
