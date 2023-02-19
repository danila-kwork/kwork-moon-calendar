package ru.mooncalendar.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor
import java.text.SimpleDateFormat
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
                title = { Text(text = "Выберите дату", color = primaryText()) },
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
            Column {

                Spacer(modifier = Modifier.height(30.dp))

                SelectableCalendar(
                    modifier = Modifier.fillMaxSize(),
                    calendarState = state
                )
            }
        }
    }
}