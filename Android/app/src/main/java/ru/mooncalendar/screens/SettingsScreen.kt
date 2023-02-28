package ru.mooncalendar.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.boguszpawlowski.composecalendar.kotlinxDateTime.now
import kotlinx.datetime.LocalDate
import ru.mooncalendar.R
import ru.mooncalendar.common.MaskVisualTransformation
import ru.mooncalendar.common.extension.parseToDateFormat
import ru.mooncalendar.common.extension.parserFormat
import ru.mooncalendar.common.openBrowser
import ru.mooncalendar.data.auth.AuthRepository
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.auth.model.UserRole
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText
import ru.mooncalendar.ui.theme.secondaryBackground
import java.util.*


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "IntentReset")
@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val authRepository = remember(::AuthRepository)
    var user by remember { mutableStateOf<User?>(null) }
    var alertEditDate by remember { mutableStateOf(false) }
    val auth = remember(Firebase::auth)
    val currentDate by remember { mutableStateOf(LocalDate.now()) }

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
                        text = "Настройки",
                        color = primaryText()
                    )
                }
            )
        }
    ) {

        if(alertEditDate && user != null){
            AlertEditDate(
                onDismissRequest = { alertEditDate = false },
                currentDate = user!!.birthday,
                editDate = {
                    authRepository.editDateUser(it) {
                        alertEditDate = false
                        authRepository.getUser({ user = it })
                    }
                }
            )
        }

        LazyColumn {
            item {

                Spacer(modifier = Modifier.height(50.dp))

                if(user?.userRole == UserRole.ADMIN){
                    Divider(color = primaryText())

                    Row(
                        modifier = Modifier
                            .background(secondaryBackground())
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("create_info_screen")
                            },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Добавить аффирмацию",
                            fontWeight = FontWeight.W900,
                            modifier = Modifier.padding(5.dp)
                        )

                        Text(
                            text = "->",
                            fontWeight = FontWeight.W100,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .background(secondaryBackground())
                        .clickable {
                            if (user?.userRole == UserRole.ADMIN) {
                                alertEditDate = true
                            }
                        }
                ) {
                    user?.let {

                        Divider(color = primaryText())

                        Text(
                            text = "Электронная почта",
                            fontWeight = FontWeight.W900,
                            modifier = Modifier.padding(5.dp)
                        )

                        Text(
                            text = user?.email ?: "",
                            fontWeight = FontWeight.W100,
                            modifier = Modifier.padding(5.dp)
                        )

                        Divider(color = primaryText())

                        Text(
                            text = "Дата рождения",
                            fontWeight = FontWeight.W900,
                            modifier = Modifier.padding(5.dp)
                        )

                        Text(
                            text = "${user?.birthday}\n Ваш личный ${user?.getMyYearShortText(currentDate.year)?.text}",
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

                        }
                ) {
                    Divider(color = primaryText())

                    TextButton(onClick = {
                        navController.navigate("training_manual")
                    }) {
                        Text(
                            text = "О приложении «Жанат»",
//                            fontWeight = FontWeight.W900,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .background(secondaryBackground())
                        .clickable {

                        }
                ) {
                    Divider(color = primaryText())

                    TextButton(onClick = {
                        navController.navigate("subscription_info_screen")
                    }) {
                        Text(
                            text = "Методичка и информация о подписке",
//                            fontWeight = FontWeight.W900,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

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

                if(auth.currentUser != null) {
                    Column(
                        modifier = Modifier.background(secondaryBackground())
                    ) {

                        TextButton(onClick = {
                            auth.signOut()
                            navController.navigate("auth_screen")
                        }) {
                            Text(
                                text = "Выйти",
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.Red
                            )
                        }

                        Divider(color = primaryText())
                    }
                }


                Spacer(modifier = Modifier.height(20.dp))

//                Column(
//                    modifier = Modifier
//                        .background(secondaryBackground())
//                        .clickable {
//
//                        }
//                ) {
//                    Divider(color = primaryText())
//
//                    TextButton(onClick = { /*TODO*/ }) {
//                        Text(
//                            text = "Восстановить подписку",
////                            fontWeight = FontWeight.W900,
//                            modifier = Modifier
//                                .padding(5.dp)
//                                .fillMaxWidth(),
//                            textAlign = TextAlign.Center
//                        )
//                    }
//
//                    Divider(color = primaryText())
//                }

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
                            text = "By Lunara Kanash ♥️\nsyucai.app",
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.padding(5.dp),
                            textAlign = TextAlign.Center,
                            color = primaryText()
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
private fun AlertEditDate(
    currentDate: String,
    onDismissRequest: () -> Unit,
    editDate: (String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    val textFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit, block = {
        textFieldFocusRequester.requestFocus()
        val fromFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        date = fromFormat.parse(currentDate).parserFormat()
    })

    AlertDialog(
        onDismissRequest = onDismissRequest,
        backgroundColor = primaryBackground(),
        shape = AbsoluteRoundedCornerShape(15.dp),
        buttons = {
            OutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .focusRequester(textFieldFocusRequester),
                value = date,
                onValueChange = {
                    if(it.length <= 8){
                        date = it
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = primaryBackground(),
                    textColor = primaryText()
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = {
                    editDate(date)
                }),
                visualTransformation = MaskVisualTransformation("##-##-####"),
                label = {
                    Text(
                        text = "В формате ДД-ММ-ГГГГ",
                        color = primaryText()
                    )
                }
            )
        }
    )
}