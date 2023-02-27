package ru.mooncalendar.data.moonCalendar.model

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.database.DataSnapshot
import ru.mooncalendar.common.extension.toLocalDate
import ru.mooncalendar.ui.theme.tintColor
import java.time.LocalDate
import java.util.*



data class MoonCalendar(
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val moonImageUrl: String = "",
    val table: List<Table> = emptyList()
){
    @SuppressLint("NewApi")
    fun getDayRecommendations(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.parse(this.date).toLocalDate()

        return when(date.dayOfMonth) {
            else -> ""
        }
    }

    @SuppressLint("NewApi")
    fun moonCalendarColor(): Color {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.parse(this.date).toLocalDate()

        var year = date.year
        var yearSum = 0

        while(year > 0){
            yearSum += year % 10
            year /=10
        }

        var month = date.month.value
        var monthSum = 0

        while(month > 0){
            monthSum += month % 10
            month /=10
        }

        var day = date.dayOfMonth
        var daySum = 0

        while(day > 0){
            daySum += day % 10
            day /=10
        }

        var sum = yearSum + monthSum + daySum
        var number = 0

        while(sum > 0){
            number += sum % 10
            sum /=10
        }

        return when(number) {
            1 -> Color(0xFFFFD500)
            2 -> Color(0xFF443A3A)
            3 -> Color(0xFFFF6F00)
            4 -> Color(0xFF39352A)
            5 -> Color(0xFF13B80E)
            6 -> Color(0xFFE6A8D7)
            7 -> Color(0xFFE0B0FF)
            8 -> Color(0xFF034875)
            9 -> Color(0xFFC50606)
            else -> tintColor
        }
    }
}

data class Table(
    val parameter: String = "",
    val value: String = ""
)

fun DataSnapshot.mapMoonCalendar(
    date: String
): MoonCalendar {

    try {
        val table = this.child("table").value.toString().split(";").toTypedArray()

        return MoonCalendar(
            date = this.child("date").value.toString(),
            title = this.child("title").value.toString(),
            description = this.child("desc").value.toString(),
            moonImageUrl = this.child("moon_image_url").value.toString(),
            table = table.map {

                val column = it.split("-").toTypedArray()

                Table(
                    parameter = column[0],
                    value = column[1]
                )
            }
        )
    }catch (e:Exception){
        return MoonCalendar(
            date = date
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun getDayNumberColor(
    date: LocalDate
): Color {
    var day = date.dayOfMonth
    var year = date.year
    var month = date.monthValue

    var sum = 0
    var number = 0

    if(day in listOf(10, 20, 30))
        return Color.Red

    while(day > 0){
        sum += day % 10
        day /=10
    }

    while(year > 0){
        sum += year % 10
        year /=10
    }

    while(month > 0){
        sum += month % 10
        month /=10
    }

    while(sum > 0){
        number += sum % 10
        sum /=10
    }

    return when (number) {
        3 -> Color.Yellow
        6 -> Color.Green
        8 -> Color(0xFF288CE4)
        else -> MaterialTheme.colors.surface
    }
}

@SuppressLint("NewApi")
fun getDayText(
    date: LocalDate,
    number: Int? = null
): Pair<String, AnnotatedString> {

    var day = date.dayOfMonth
    var year = date.year
    var month = number ?: date.monthValue

    var sum = 0

    if(number == null){
        while(day > 0){
            sum += day % 10
            day /=10
        }

        while(year > 0){
            sum += year % 10
            year /=10
        }

        while(month > 0){
            sum += month % 10
            month /=10
        }
    }else {
        while(month > 0){
            sum += month % 10
            month /=10
        }
    }

    if(day in listOf(10, 20, 30))
        return "ДЕНЬ $sum" to buildAnnotatedString {
            append("  Даже если в сумме сегодняшняя дата дает благоприятное число, по науке Сюцай это день, когда результаты наших действий могут быть обнулены! Поэтому стоит отложить на другой день крупные покупки, договоры, кредиты и т.д.")
            append("\n")
            append("  Следите за эмоциональным и физическим здоровьем, сохраняйте отношения!")

            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  Не стоит подписывать значимые бумаги, крупные покупки, а также принять важные решения. Не идите на поводу своего ЭГО, будьте мягче и уклоняйтесь от азартных действий")
        }

    return when(sum) {
        1 -> "ДЕНЬ 1.\nДЕНЬ НАЧАЛА\nВСЕГО НОВОГО " to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: СОЛНЦЕ.")
                }
            }
            append("  День Солнца, лидерства и начала всего нового. Хороший день для начала новых дел.")
            append("\n")
            append("\n")
            append("  Этот день несет много энергии. Она пойдет или на создание или в разрушение.")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Используйте энергию дня правильно: не уходить в эгоизм. Важно оставаться в покое и выстраивать стратегию задуманного плана.")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("   Подписывать договоры, но хорошо начинать какие-либо процессы.")
        }
        2 -> "ДЕНЬ 2.\nДЕНЬ ДИПЛОМАТИИ." to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: ЛУНА")
                }
            }
            append("  День, когда необходимо налаживать отношения. Будьте хорошим дипломатом. Может появиться желание разорвать их, но их необходимо налаживать. В ресурсном состоянии через энергию понимания удастся добиться идеальных договоренностей. В «минусе» -день сомнений и может присутствовать депрессия. Может возникнуть желание разорвать или выяснять отношения, но их следует наоборот укреплять!")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Больше ходить, трудиться и служить людям. Прожить день через дипломатию и понимание.")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  Не самый лучший̆ день для принятия важных решений.")
        }
        3 -> "ДЕНЬ 3.\nДЕНЬ АНАЛИЗА\nИ УСПЕХА." to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: ЮПИТЕР")
                }
            }
            append("  Сегодня энергия анализа будет работать с вами. Через анализ можно прийти к успеху")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Благоприятный день для принятия серьезных решений, подписания договоров и совершения покупок. Хороший день для бракосочетания, медицинских процедур и операций.")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  Есть вероятность азарта, но за этим могут последовать потери. Будет желание получения легкой выгоды. Поэтому важно все делать только через анализ.")
        }
        4 -> "ДЕНЬ 4.\nДЕНЬ МИСТИКИ\nИ ЗНАНИЙ." to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: РАХУ")
                }
            }
            append("  Сегодня могут происходить необъяснимые приятные мистические события и подарки от Вселенной.")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Важно быть на позитиве, чтобы были только положительные мистические события, иначе могут быть потери и неожиданные неприятности.")
            append("\n")
            append("\n")
            append("  Рекомендуется получать знания (проходить курсы, обучения). Поработать над целеполаганием и определять цели. Важно быть в хорошем настроении. Люди вокруг будут в неудовлетворенном состоянии, поэтому возможно ощущение, что никуда не успеваешь.")
            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  Не желательно начинать новые проекты, принимать решения и подписывать договоры.")
        }
        5 -> "ДЕНЬ 5.\nДЕНЬ КОММУНИКАЦИЙ." to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: МЕРКУРИЙ")
                }
            }
            append("  Все возможности приходят через коммуникацию. Налаживайте связи, знакомьтесь и общайтесь!")
            append("\n")
            append("\n")
            append("  День, когда что-то тайное может стать явным…")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Решать дела через здоровую коммуникацию Хороший̆ день, чтобы создавать новые знакомства, общаться, делать бизнес, выкладывать посты в социальных сетях. Помните, что у каждого своя логика и правда.")
            append("\n")
            append("\n")
            append("  В минусе - день борьбы. Может проявляться беспечность.")
            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  Подписывать договоры и делать серьезные покупки.")
        }
        6 -> "ДЕНЬ 6.\nДЕНЬ ЛЮБВИ,\nКОМФОРТА И УСПЕХА." to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: ВЕНЕРА")
                }
            }
            append("  Сегодня могут сбыться все ваши мечты! ")
            append("\n")
            append("\n")
            append("  Работает энергия любви и счастья. Работать и принимать решения максимально через любовь. Идеальный день для свиданий и дел, которые принесут за собой успех!")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Благоприятный̆ день заключения брака, принятия решений и подписания договоров. Совершайте покупки и начинайте большие проекты!")
            append("\n")
            append("\n")
            append("  В минусе будет тянуть к мстительности, лени и чрезмерному комфорту. Возможны обострения хронических заболеваний и повышенный эмоциональный фон, а также страдания из-за отсутствия комфорта в любых аспектах")
        }
        7 -> "ДЕНЬ 7.\nДЕНЬ ТРАНСФОРМАЦИИ\nИ КРИЗИСА." to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: КЕТУ")
                }
            }
            append("  Осознанный выход за рамки комфорта, либо Вселенная будет выводить за рамки привычного. Все нужно смиренно принимать, чтобы пришел духовный рост. Держите себя в дисциплине: служение, молитва, занятие йогой, обучение и задача от Творца!")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Важные дела лучше отложить, так как люди испытывают кризис. Если люди создают вам кризис- не разрушайтесь!")
            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  В этот день не желательно начинать новые проекты. Не рекомендуется подписывать договоры и принимать решения. Не стоит продавать недвижимость и вкладывать деньги.")
        }
        8 -> "ДЕНЬ 8.\nДЕНЬ ТРУДА\nИ ОБУЧЕНИЯ" to buildAnnotatedString {

            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: САТУРН")
                }
            }
            append("  Необходимо много учиться. Приобретенные навыки будут служить вам на протяжении жизни. Труд принесет финансовый результат")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Благоприятный̆ день для заключения брака, договоров. В этот день можно выгодно продать или подать объявление. Важно много работать.")
            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  Не стоит брать кредиты. Можно попасть. В зону ограничений и Вас будет сжимать пространство. Могут быть сомнения, недоверие, желание все тотально проконтролировать.")
        }
        9 -> "ДЕНЬ 9.\nДЕНЬ СЛУЖЕНИЯ\nИ РАЗРУШЕНИЯ." to buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: МАРС")
                }
            }
            append("  Будут завершаться начатые дела и может наблюдаться воинственная энергия.")

            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("СОВЕТ:")
                }
            }

            append("  Не ведитесь на эмоции, оставайтесь в покое. Можно обнаружить новые возможности. Хороший день для благотворительности.")
            append("\n")
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("НЕ РЕКОМЕНДУЕТСЯ")
                }
            }

            append("  Подписывать договоры. Важно максимально служить, не принимать серьезных решений.")

        }
        else -> getDayText(date, sum)
    }
}

@SuppressLint("NewApi")
fun getDayTextShort(
    date: LocalDate,
    number: Int? = null
): Pair<String, AnnotatedString> {

    var day = number ?: date.dayOfMonth

    var sum = 0

    while(day > 0){
        sum += day % 10
        day /=10
    }

    return when(sum) {
        1 -> "ДЕНЬ 1.\nДЕНЬ НАЧАЛА\nВСЕГО НОВОГО " to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: СОЛНЦЕ.")
                }
            }

            append("\n")
            append("  День Солнца, лидерства и начала всего нового. Хороший день для начала новых дел.")
        }
        2 -> "ДЕНЬ 2.\nДЕНЬ ДИПЛОМАТИИ." to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: ЛУНА")
                }
            }

            append("\n")
            append("  День, когда необходимо налаживать отношения. Будьте хорошим дипломатом. Может появиться желание разорвать их, но их необходимо налаживать.")
        }
        3 -> "ДЕНЬ 3.\nДЕНЬ АНАЛИЗА\nИ УСПЕХА." to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: ЮПИТЕР")
                }
            }

            append("\n")
            append("  Сегодня энергия анализа будет работать с вами. Через анализ можно прийти к успеху.")
        }
        4 -> "ДЕНЬ 4.\nДЕНЬ МИСТИКИ\nИ ЗНАНИЙ." to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: РАХУ")
                }
            }

            append("\n")
            append("  Сегодня могут происходить необъяснимые приятные мистические события и подарки от Вселенной.")
        }
        5 -> "ДЕНЬ 5.\nДЕНЬ КОММУНИКАЦИЙ." to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: МЕРКУРИЙ")
                }
            }

            append("\n")
            append("  Все возможности приходят через коммуникацию. Налаживайте связи, знакомьтесь и общайтесь!")
        }
        6 -> "ДЕНЬ 6.\nДЕНЬ ЛЮБВИ,\nКОМФОРТА И УСПЕХА." to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: ВЕНЕРА")
                }
            }

            append("\n")
            append("  Сегодня могут сбыться все ваши мечты! ")
        }
        7 -> "ДЕНЬ 7.\nДЕНЬ ТРАНСФОРМАЦИИ\nИ КРИЗИСА." to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: КЕТУ")
                }
            }

            append("\n")
            append("  Осознанный выход за рамки комфорта, либо Вселенная будет выводить за рамки привычного.")
        }
        8 -> "ДЕНЬ 8.\nДЕНЬ ТРУДА\nИ ОБУЧЕНИЯ" to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: САТУРН")
                }
            }

            append("\n")
            append("  Необходимо много учиться. Приобретенные навыки будут служить вам на протяжении жизни.")
        }
        9 -> "ДЕНЬ 9.\nДЕНЬ СЛУЖЕНИЯ\nИ РАЗРУШЕНИЯ." to buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)){
                withStyle(SpanStyle(fontWeight = FontWeight.W900)){
                    append("ЭНЕРГИЯ ДНЯ: МАРС")
                }
            }

            append("\n")
            append("  Будут завершаться начатые дела и может наблюдаться воинственная энергия.")
        }
        else -> getDayTextShort(date, sum)
    }
}

@SuppressLint("NewApi")
fun getRecommendations(date: LocalDate): String {

    val dayOfWeek = date.dayOfWeek

    return when(dayOfWeek.value){
        1 -> {
            "Понедельник – важно не перенапрягаться, для женщин заниматься собой и женскими делами: встреча с подругами, угощать и кормить всех вкусной и здоровой едой, особенно полезными сладостями. Для мужчин в этот день важно поддерживать эмоциональное спокойствие. Полезно гулять у воды, а также медитировать чуть дольше, чем обычно. \n" +
                    "Покупать вещи, совершать покупки, вступать в брак. Принимать решения на уровне чувств. Искать одобрение у женщин. \n" +
                    "Не принимать трудных решений, требующих больших усилий, напряжений. Не стригите волосы, ногти. Будьте сдержанными и предусмотрительными"
        }
        2 -> {
            "Вторник – хорошо заниматься спортом, единоборствами, активно действовать, начинать ремонт, делать физическую работу, возобновлять отложенные дела, проводить хирургические операции, копать землю, ходить в бассейн и в баню. Неблагоприятно гневаться, если что-то идет не по плану, выяснять отношения в состоянии гнева, обиды и раздражения.\n" +
                    "В этот день рекомендуется совершать судебные процессы. Показатели обещают процесс лечение, медицина. Проводить спортивные мероприятия. \n" +
                    "В этот день не начинайте новые дела, впервые созданные, появившиеся. Или возникшие недавно в замен прежних, избегайте поездок, будьте осторожны в транспорте, избегайте ссор и нервных напряжений."
        }
        3 -> {
            "Среда – очень хороши любые торговые сделки, покупки и продажи, бизнес-встречи, подписание договоров, изучение новых наук и иностранных языков, всякого рода общение, встречи с друзьями.\n" +
                    "Надо заниматься торговлей, бизнесом. Начинать новые процессы. Приобретать знания. Заводить друзей, жениться. Хорошо делать публикации. \n" +
                    "Избегайте возбуждения, стрессовых ситуаций от сильных внешних раздражителей, не настраивайтесь на конфликт от столкновения интересов, не лгите даже ради благой цели. Не будьте излишне серьезными , не замыкайтесь в себе. \n"
        }
        4 -> {
            "Четверг – благоприятен для всех начинаний: свадьбы, крещения детей, бизнеса, финансовых вложений, открытия счета в банке, консультирования, посещения лекций, образовательных программ.\n" +
                    "Благотворительностью, пожертвованиями. Заниматься высшими знаниями. Совершать покупки больших вещей. \n" +
                    "Не допускайте злобу, гнев, не будьте жадными, не благородными, легкомысленными, жестокими, не бездельничайте, не лгите, не искажайте истину. \n"
        }
        5 -> {
            "Пятница – благоприятно: делать свадьбы, организовать праздники, покупать украшения и красивую одежду, заниматься дизайном, устраивать романтические свидания, посещать концерты, театры, художественные выставки, заниматься творчеством, дарить и принимать изысканные подарки, покупать красивые автомобили и жилье; ходить в парикмахерскую и косметологу; начинать путешествия.\n" +
                    "Покупать украшения, цветы, одежду любые красивые вещи. Для вступления в брак. Ходить в гости. Принимать гостей. \n" +
                    "Не продавайте большие и важные вещи. Не составляйте завещание, не грустите. Не уединяйтесь. Не надо делать самоанализ. \n"
        }
        6 -> {
            "Суббота – благоприятно отдыхать, соблюдать посты, уединиться, проводить глубокий внутренний анализ, медитировать, совершать и упорядочивать текущие дела. Если требуется принять какое-то решение, оно должно быть очень обдуманным и взвешенным.\n" +
                    "Отдыхать медитировать, заниматься йогой. Хозяйственными делами, связанными с землей. Домашними делами. \n" +
                    "Не делайте важные дела, не переутомляйтесь, не стригите волосы \n" +
                    "и ногти, не стирайте. \n"
        }
        7 -> {
            "Воскресенье – благоприятно быть активным, вставать рано утром, реализовывать любые бизнес- проекты и важные дела, встречаться с влиятельными людьми и начальством, осуществлять выборы в государственные органы, активно проводить время на природе, наполняться энергией.\n" +
                    "Наслаждаться жизнью, солнцем, быть на природе. Благоприятна работа, связанная с золотом, медью. деревьями, шёлком, огнём. \n" +
                    "Не идти на поводу у своего ЭГО, не будьте мелочными, инертными, бездеятельный, без инициативными. Не лгите не будьте жестокими. \n"
        }
        10, 20, 30 -> {
            "ДЕНЬ НЕ РЕКОМЕНДУЕТСЯ\nЭНЕРГИЯ ДНЯ: \n" +
                    "НЕ РЕКОМЕНДУЕТСЯ\n" +
                    "Не желательно начинать новые проекты и события. Есть высокая вероятность обнуления всех результатов ваших действий. Отложить на другой день крупные покупки, договоры, кредиты и т.д.\n"
        }
        else -> ""
    }
}

@SuppressLint("NewApi")
fun getShortRecommendations(date: LocalDate): String {

    val dayOfWeek = date.dayOfWeek

    return when(dayOfWeek.value){
        1 -> {
            "Понедельник – важно не перенапрягаться, для женщин заниматься собой и женскими делами: встреча с подругами, угощать и кормить всех вкусной едой"
        }
        2 -> {
            "Вторник – хорошо заниматься спортом, единоборствами, активно действовать, начинать ремонт, делать физическую работу."
        }
        3 -> {
            "Среда – очень хороши любые торговые сделки, покупки и продажи, бизнес-встречи."
        }
        4 -> {
            "Четверг – благоприятен для всех начинаний: свадьбы, крещения детей, бизнеса, финансовых вложений."
        }
        5 -> {
            "Пятница – благоприятно: делать свадьбы, организовать праздники, покупать украшения и красивую одежду, заниматься дизайном."
        }
        6 -> {
            "Суббота – благоприятно отдыхать, соблюдать посты, уединиться, проводить глубокий внутренний анализ"
        }
        7 -> {
            "Воскресенье – благоприятно быть активным, вставать рано утром, реализовывать любые бизнес- проекты и важные дела"
        }
        10, 20, 30 -> {
            "ДЕНЬ НЕ РЕКОМЕНДУЕТСЯ\nЭНЕРГИЯ ДНЯ: \n" +
                    "НЕ РЕКОМЕНДУЕТСЯ\n" +
                    "Не желательно начинать новые проекты и события.\n"
        }
        else -> ""
    }
}