package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.R
import ru.mooncalendar.common.openBrowser
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "IntentReset")
@Composable
fun SettingsScreen(

) {
    val context = LocalContext.current
    val authRepository = remember(::AuthRepository)
    var user by remember { mutableStateOf<User?>(null) }
    val auth = remember(Firebase::auth)

    val systemUiController = rememberSystemUiController()
    val primaryBackground = primaryBackground()

    val intentSend = remember {
        Intent(Intent.ACTION_SENDTO).apply {
            type = "plain/text"
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("Info@syucai.app"))
            putExtra(Intent.EXTRA_SUBJECT, "subject")
            putExtra(Intent.EXTRA_TEXT, "mail body")
        }
    }

    LaunchedEffect(key1 = Unit, block = {

        systemUiController.setStatusBarColor(
            color = primaryBackground
        )

        try {
            auth.currentUser?.let {
                authRepository.getUser({
                    user = it
                })
            }
        }catch (_:Exception){}
    })

    Scaffold(
        backgroundColor = primaryBackground(),
        topBar = {
            TopAppBar(
                backgroundColor = primaryBackground(),
                title = {
                    Text(
                        text = user?.email ?: "Настройки",
                        color = primaryText()
                    )
                }
            )
        }
    ) {
        LazyColumn {
            item {

                Spacer(modifier = Modifier.height(50.dp))

                Column(
                    modifier = Modifier.background(secondaryBackground())
                ) {
                    user?.let {
                        Divider(color = primaryText())

                        Text(
                            text = "Дата рождения",
                            fontWeight = FontWeight.W900,
                            modifier = Modifier.padding(5.dp)
                        )

                        Text(
                            text = user?.birthday ?: "",
                            fontWeight = FontWeight.W100,
                            modifier = Modifier.padding(5.dp)
                        )

                        Divider(color = primaryText())
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Column(
                    modifier = Modifier
                        .background(secondaryBackground())
                        .clickable {
                            context.openBrowser("http://syucai.app")
                        }
                ) {
                    Divider(color = primaryText())

                    TextButton(onClick = { context.openBrowser("http://syucai.app") }) {
                        Text(
                            text = "Вебсайт",
//                            fontWeight = FontWeight.W900,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier.background(secondaryBackground())
                ) {
                    Divider(color = primaryText())

                    TextButton(onClick = {
                        context.startActivity(intentSend)
                    }) {
                        Text(
                            text = "Обратная связь",
//                            fontWeight = FontWeight.W900,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Divider(color = primaryText())
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .background(secondaryBackground())
                        .clickable {

                        }
                ) {
                    Divider(color = primaryText())

                    TextButton(onClick = { /*TODO*/ }) {
                        Text(
                            text = "Воостановить подписку",
//                            fontWeight = FontWeight.W900,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Divider(color = primaryText())
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = {
                        context.openBrowser("https://instagram.com/luna_syucai?igshid=NTdlMDg3MTY=")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.instagram),
                            contentDescription = null,
                            tint = primaryText(),
                            modifier = Modifier
                                .padding(5.dp)
                                .size(40.dp)
                        )
                    }

                    TextButton(onClick = {
                        context.openBrowser("syucai.app")
                    }) {
                        Text(
                            text = "made with Сюцай ♥️\nsyucai.app",
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = primaryText()
                        )
                    }
                }
            }
        }
    }
}