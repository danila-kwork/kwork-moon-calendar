package ru.mooncalendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mooncalendar.ui.theme.primaryBackground
import ru.mooncalendar.ui.theme.primaryText

@Composable
fun TrainingManualScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = primaryBackground()
    ) {
        LazyColumn {

            item {
                Text(
                    text = "О приложении «Zhanat Calendar»",
                    color = primaryText(),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.W900,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )

                Text(
                    text = "    Календарь с подбором благо- приятных дней, основанный на принципах цифровой психологии Сюцай- науке о дисциплине ума и реализации души.",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "    Сюцай помогает людям понять, кто вы есть на самом деле, окунуться в подсознание и узнать себя с новой стороны. В основе науке лежат принципы Раджа-йоги, которая направлена на работу с сознанием при помощи медитации.",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "    Согласно науке, есть числа, которые способны повлиять на жизнь человека. Сюда относится не только дата рождения, но и дата вступления в брак, число имени, номер квартиры, телефона и даже даты заключения договоров. Каждый день таит за собой особую энергию, правильно направив которую, можно благоприятно повлиять на него и даже избежать негативных последствий. Данное приложение было создано для того, чтобы легко прослеживать за энергией каждого дня и осознанно проживать его. Здесь также можно рассчитать личный календарь с рекомендациями, вносить каждый день свои заметки и наблюдения, а также следить за нормой своих шагов!",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "    Основателем Сюцай является Жанат Кожамжаров, Доктор психологических наук, президент Международного Института Интегративной Медицины, обладатель множества регалий, наград и орденов. Он развил идею о том, что дата рождения человека содержит в себе уникальную информацию, открывает дверь в мир познания собственного Я и понимания своей судьбы.",
                    color = primaryText(),
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}