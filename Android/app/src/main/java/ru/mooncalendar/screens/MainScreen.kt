package ru.mooncalendar.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ru.mooncalendar.data.moonCalendar.MoonCalendarRepository
import ru.mooncalendar.data.moonCalendar.model.MoonCalendar
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController
) {
    val moonCalendarRepository = remember(::MoonCalendarRepository)
    var moonCalendar by remember { mutableStateOf<List<MoonCalendar>>(emptyList()) }

    val date = Date()

    LaunchedEffect(key1 = Unit, block = {
        moonCalendarRepository.getMoonCalendar(
            filterDate = date.toString(),
            onSuccess = { moonCalendar = it }
        )
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = date.toString()) },
                modifier = Modifier.clickable {

                }
            )
        }
    ) {
        LazyColumn {
            items(moonCalendar) { item ->
                Column {
                    Image(
                        painter = rememberAsyncImagePainter(model = item.moonImageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(5.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = item.title,
                        fontWeight = FontWeight.W900,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = item.description,
                        modifier = Modifier.padding(5.dp)
                    )

                    repeat(item.table.size) {
                        val row = item.table[it]

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.parameter,
                                modifier = Modifier.padding(5.dp)
                            )

                            Text(
                                text = row.value,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }

                    Divider()

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}