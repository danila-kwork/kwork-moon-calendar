package ru.mooncalendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.mooncalendar.common.extension.parseToBaseDateFormat
import ru.mooncalendar.data.info.Info
import ru.mooncalendar.data.info.InfoRepository
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.tintColor
import java.util.Date

@Composable
fun CreateInfoScreen(
    navController: NavController
) {
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    val infoRepository = remember(::InfoRepository)
    var date by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit, block = {
        date = Date().parseToBaseDateFormat()
    })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = primaryBackground()
    ) {
        LazyColumn(
            modifier = Modifier.size(screenWidthDp, screenHeightDp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(
                    text = "Укажите дату в формате 2005-09-19",
                    color = primaryText(),
                    modifier = Modifier.padding(5.dp)
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.padding(5.dp),
                    label = {
                        Text(
                            text = "Дата",
                            color = primaryText()
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = primaryBackground(),
                        cursorColor = tintColor,
                        textColor = primaryText()
                    )
                )

                OutlinedTextField(
                    value = info,
                    onValueChange = { info = it },
                    modifier = Modifier.padding(5.dp),
                    label = {
                        Text(
                            text = "Aффирмация",
                            color = primaryText()
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = primaryBackground(),
                        cursorColor = tintColor,
                        textColor = primaryText()
                    )
                )

                Button(
                    modifier = Modifier.padding(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = tintColor
                    ),
                    onClick = {
                        infoRepository.create(
                            Info(date = date, info = info),
                            onSuccess = navController::navigateUp
                        )
                    }
                ) {
                    Text(
                        text = "Добавить",
                        color = primaryText()
                    )
                }
            }
        }
    }
}