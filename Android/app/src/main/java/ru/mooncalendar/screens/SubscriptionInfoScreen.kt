package ru.mooncalendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText

@Composable
fun SubscriptionInfoScreen(

) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = primaryBackground()
    ) {
        LazyColumn {

            item {

                Text(
                    text = "Методичка по использованию календаря «Zhanat»",
                    color = primaryText(),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.W900,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )

                Text(
                    text = buildAnnotatedString {
                        append("Открыв приложение, необходимо ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("ЗАРЕГИСТРИРОВАТЬСЯ")
                        }
                        append(", добавив электронную почту, придумав пароль и. ввеля дату рождения (по нему в приложении будет проходить расчет календаря).")
                    },
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Оплату можно воспроизвести в самом приложении, где можно удобно рассчитаться через QIWI либо KASPI на выбор.",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "    Как только пройдет регистрация нового пользователя, приложение рассчитает вам календарь.",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify,
                )

                Text(
                    text = buildAnnotatedString {
                        append("    На главной странице идет описание текущего дня, а при подписке ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("«SILVER» ")
                        }
                        append("и ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("«GOLD» ")
                        }
                        append("доступны рекомендации к нему, а также возможность добавлять ежедневные заметки, получать аффирмации! Есть также следить за личным днем, месяцем либо годом и доступен шагомер, где указана минимальная норма и ваши текущие шаги.")
                    },
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify,
                )

                Text(
                    text = "    В самом календаре зеленым указаны наиболее благоприятные дни для сделок и важных дел, красным неблагоприятные и желтым нейтральные. ",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify,
                )

                Text(
                    text = "    Для того, чтобы углубиться в понимание энергии цифр, узнать о себе полную информацию через призму цифровой психологии, советуем всем пройти индивидуальную консультацию у Профессионального мастера Сюцай и автора приложения- Лунары Канаш. Вас ждет разбор даты рождения: числа сознания, возможности реализации миссии; проработка матрицы и получение инструментов для наработки недостающих энергий; разработка стратегии жизни; расчет имени, и не только! Это лишь малая часть всего, что ждет каждого на консультации!",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify,
                )

                Text(
                    text = buildAnnotatedString {
                        append("    В этом случае наиболее выгодное предложение для вас — это покупка ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("«GOLD» ")
                        }
                        append("пакета календаря.")
                    },
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify,
                )

                Text(
                    text = buildAnnotatedString {
                        append("    По иным вопросам и пожеланиям можно обратиться в службу поддержки, написав на почту ")
                        withStyle(style = SpanStyle()){
                            append("syucaicom@gmail.com")
                        }
                        append(".")
                    },
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify,
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "О подписке",
                    color = primaryText(),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.W900,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )

                Column(
                    modifier = Modifier.padding(start = 5.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("    ● В ")
                            withStyle(style = SpanStyle()){
                                append("«LITE» ")
                            }
                            append("версию календаря включены общие благоприятные/нейтральные/неблагоприятные дни для заключения сделок, крупных покупок, принятия важных решений.")
                        },
                        color = primaryText(),
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                    )

                    Text(
                        text = "Приобретая доступ на месяц, стоимостью в 1200 тенге (210 рублей), пользователь получает информацию только на текущий месяц, но при покупке годовой подписки за 8900 тенге (1500 рублей) с выгодой до 40% будет доступен календарь на весь 2023 год.",
                        color = primaryText(),
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("    ● Пакет ")
                            withStyle(style = SpanStyle()){
                                append("«SILVER» ")
                            }
                            append("включает в себя общий и личный календарь 2023 года с благоприятными/нейтральными/неблагоприятными днями для заключения сделок, крупных покупок, принятия важных решений; аффирмации на каждый день; шагомер на 10 000 шагов; рекомендации по личному году/месяцу и возможность писать ежедневные заметки с наблюдениями эмоций и действий.")
                        },
                        color = primaryText(),
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                    )

                    Text(
                        text = "Доступ на месяц: 2400 тенге (402 рубля)\n" +
                                "Покупка на год: 15 900 тенге (2670 рублей)\n" +
                                "Выгода более 50%",
                        color = primaryText(),
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("    ● Пакет ")
                            withStyle(style = SpanStyle()){
                                append("«GOLD» ")
                            }
                            append("включает в себя общий и личный календарь 2023 года с благоприятными/нейтральными/неблагоприятными днями для заключения сделок, крупных покупок, принятия важных решений; аффирмации на каждый день; шагомер на 10 000 шагов; рекомендации по личному году/месяцу и возможность писать ежедневные заметки с наблюдениями эмоций и действий. А также личную консультацию с полным разбором в два этапа от профессионального мастера Сюцай и автора приложения Лунары Канаш.")
                        },
                        color = primaryText(),
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                    )

                    Text(
                        text = "Стоимость: 42 000 тенге (7044 рубля).",
                        color = primaryText(),
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}