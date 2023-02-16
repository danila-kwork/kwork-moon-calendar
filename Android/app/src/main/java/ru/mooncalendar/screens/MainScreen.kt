package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import ru.mooncalendar.common.extension.parseToBaseDateFormat
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.data.moonCalendar.MoonCalendarRepository
import ru.mooncalendar.data.moonCalendar.model.MoonCalendar
import ru.mooncalendar.ui.view.DatePicker
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "NewApi")
@Composable
fun MainScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val moonCalendarRepository = remember { MoonCalendarRepository() }
    var moonCalendar by remember { mutableStateOf<List<MoonCalendar>>(emptyList()) }

    var date by remember { mutableStateOf(Date()) }
    var calendarState by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit, block = {
        moonCalendarRepository.getMoonCalendar(
            filterDate = date,
            onSuccess = { moonCalendar = it },
            onFailure = {
                Toast.makeText(context, "error: $it", Toast.LENGTH_SHORT).show()
            }
        )
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = date.parseToBaseDateFormat()) },
                modifier = Modifier.clickable {
                    calendarState = true
                }
            )
        }
    ) {
        if(calendarState) {
            DatePicker(
                onDateSelected = {
                    date = it.toDate()
                },
                onDismissRequest = {
                    calendarState = false
                }
            )
        }

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

                    Divider()

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}