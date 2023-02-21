package ru.mooncalendar.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import ru.mooncalendar.data.database.MainDatabase
import ru.mooncalendar.data.database.pedometer.Day
import ru.mooncalendar.data.database.pedometer.Goal
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground
import ru.mooncalendar.ui.theme.tintColor
import ru.mooncalendar.ui.view.ChartComponent

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PedometerScreen(date: String) {

    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

    val scope = rememberCoroutineScope()
    val mainDatabase = remember { MainDatabase.getInstance(context) }

    val dayDao = remember(mainDatabase::dayDao)
    val goalDao = remember(mainDatabase::goalDao)

    var day by remember { mutableStateOf<Day?>(null) }
    var goal by remember { mutableStateOf<Goal?>(null) }
    var editGoalDialog by remember { mutableStateOf(false) }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberMultiplePermissionsState(permissions = listOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.FOREGROUND_SERVICE
        ))
    } else {
        rememberMultiplePermissionsState(permissions = listOf())
    }

    dayDao.getByDate(date).observe(owner){
        day = it
    }

    goalDao.getLast().observe(owner){
        goal = it
    }

    LaunchedEffect(key1 = Unit, block = {
        permissions.launchMultiplePermissionRequest()
    })

    if(editGoalDialog){
        EditGoalDialog(
            goal = goal,
            onDismissRequest = { editGoalDialog = false }
        ) { steps ->
            scope.launch {
                goalDao.updateAllSteps(steps)
                editGoalDialog = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChartComponent(
            indicatorValue = day?.steps ?: 0,
            maxIndicatorValue = goal?.steps ?: 2000,
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column {
            Divider(color = secondaryBackground())

            Text(
                text = "${day?.steps ?: 0} шагов / ${((day?.steps ?: 0) * 0.75).toInt()}" +
                        " метров (${((day?.steps ?: 0) * 100) / (goal?.steps ?: 2000)} %)",
                color = primaryText(),
                fontWeight = FontWeight.W900,
                modifier = Modifier.padding(5.dp)
            )

            Text(
                text = "Пройдено за сегодня",
                color = primaryText(),
                fontWeight = FontWeight.W100,
                modifier = Modifier.padding(5.dp)
            )

            Divider(color = secondaryBackground())
        }

        Column(
            modifier = Modifier.clickable {
                editGoalDialog = true
            }
        ) {
            Divider(color = secondaryBackground())

            Text(
                text = "${goal?.steps ?: 2000} шагов / ${((goal?.steps ?: 2000) * 0.75).toInt()} метров",
                color = primaryText(),
                fontWeight = FontWeight.W900,
                modifier = Modifier.padding(5.dp)
            )

            Text(
                text = "Цель на день",
                color = primaryText(),
                fontWeight = FontWeight.W100,
                modifier = Modifier.padding(5.dp)
            )

            Divider(color = secondaryBackground())
        }
    }
}

@Composable
private fun EditGoalDialog(
    goal: Goal?,
    onDismissRequest: () -> Unit,
    editGoal: (steps: Int) -> Unit
) {
    var goalSteps by remember { mutableStateOf((goal?.steps ?: 2000).toString()) }

    Dialog(onDismissRequest = onDismissRequest) {
        TextField(
            value = goalSteps,
            onValueChange = { goalSteps = it },
            modifier = Modifier.padding(5.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = primaryBackground(),
                textColor = primaryText()
            ),
            label = { Text(text = "Шагов", color = primaryText()) },
            trailingIcon = {
                IconButton(onClick = { editGoal(goalSteps.toInt()) }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = tintColor
                    )
                }
            }
        )
    }
}