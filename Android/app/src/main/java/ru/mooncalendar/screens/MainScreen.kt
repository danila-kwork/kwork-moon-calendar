package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.mooncalendar.R
import ru.mooncalendar.common.extension.parseToBaseDateFormat
import ru.mooncalendar.data.moonCalendar.MoonCalendarRepository
import ru.mooncalendar.data.moonCalendar.model.MoonCalendar
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground
import ru.mooncalendar.ui.theme.tintColor
import ru.mooncalendar.ui.view.BaseLottieAnimation
import ru.mooncalendar.ui.view.LottieAnimationType
import java.text.SimpleDateFormat
import java.util.*

enum class Tab(val text: String) {
    DESCRIPTION("Описание"),
    PARAMETERS("Параметры")
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
    var moonCalendar by remember { mutableStateOf<List<MoonCalendar>>(emptyList()) }
    var tab by remember { mutableStateOf(Tab.DESCRIPTION) }

    var date by remember { mutableStateOf(Date()) }

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    if(dateString != null){
        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)!!
    }

    LaunchedEffect(key1 = Unit, block = {
        systemUiController.setStatusBarColor(
            color = primaryBackground
        )
    })

    LaunchedEffect(key1 = date, block = {
        moonCalendar = emptyList()

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
            if(moonCalendar.isNotEmpty()){
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
                                tint = moonCalendar.first().moonCalendarColor()
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = moonCalendar.lastOrNull()?.moonImageUrl
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(5.dp)
                                )

                                Text(
                                    text = date.parseToBaseDateFormat(),
                                    color = primaryText(),
                                    textAlign = TextAlign.Center,
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
                            Tab.values().forEach {
                                Card(
                                    modifier = Modifier.padding(5.dp),
                                    shape = AbsoluteRoundedCornerShape(10.dp),
                                    backgroundColor = if(it == tab)
                                        tintColor
                                    else
                                        secondaryBackground(),
                                    onClick = {
                                        tab = it
                                    }
                                ){
                                    Text(
                                        text = it.text,
                                        color = primaryText(),
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    items(moonCalendar) { item ->
                        Column {
                            if(tab == Tab.DESCRIPTION){
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.W900,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = primaryText()
                                )

                                Text(
                                    text = item.description,
                                    modifier = Modifier.padding(
                                        vertical = 5.dp,
                                        horizontal = 15.dp
                                    ),
                                    color = primaryText()
                                )
                            }else {
                                repeat(item.table.size){
                                    val table = item.table[it]

                                    Text(
                                        text = table.value,
                                        fontWeight = FontWeight.W900,
                                        modifier = Modifier.padding(5.dp)
                                    )

                                    Text(
                                        text = table.parameter,
                                        fontWeight = FontWeight.W100,
                                        modifier = Modifier.padding(5.dp)
                                    )

                                    Divider(color = secondaryBackground())
                                }

                            }

                            Spacer(modifier = Modifier.height(25.dp))

                            if(item != moonCalendar.last()){
                                Divider(color = tintColor)

                                Spacer(modifier = Modifier.height(25.dp))
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
}