package ru.mooncalendar.data.auth.model

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.database.DataSnapshot
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.common.extension.toLocalDate
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatement
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatementStatus
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionTime
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionType
import java.time.LocalDate
import java.util.*

data class Advice(
    val parameter: String = "",
    val state: AdviceState
)

enum class AdviceState(val text: String, val color: Color) {
    Adverse("неблагоприятный", Color.Red),
    Neutral("нейтральный", Color.Yellow),
    Favorable("благоприятный", Color.Green),
}

enum class UserRole {
    BASE_USER,
    ADMIN
}

data class User(
    val id: String,
    val email: String,
    val password: String,
    val premium: Boolean = false,
    val premiumDate: String? = null,
    val birthday: String,
    val userRole: UserRole = UserRole.BASE_USER
){
    @SuppressLint("NewApi")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @SuppressLint("NewApi")
    fun isSubscription(
        subscriptionStatement: SubscriptionStatement?,
        statusCheck: Boolean = true
    ): Boolean {

        if(premiumDate == null || premiumDate == "null")
            return false

        if(statusCheck){
            if(subscriptionStatement?.status == SubscriptionStatementStatus.WAITING)
                return false
        }

        val date = LocalDate.now()
        val subscriptionDate = simpleDateFormat.parse(premiumDate).toLocalDate()

        val finalDate = when(subscriptionStatement?.type?.time){
            SubscriptionTime.ONE_MONTH -> subscriptionDate.plusMonths(1)
            SubscriptionTime.ONE_YEAR -> subscriptionDate.plusYears(1)
            SubscriptionTime.UNLIMITED -> return premium
            else -> return false
        }

        return premium && date <= finalDate
    }

    @SuppressLint("NewApi")
    fun debitingFundsDate(subscriptionType: SubscriptionType): Date? {
        val subscriptionDate = simpleDateFormat.parse(premiumDate).toLocalDate()

        return when(subscriptionType.time){
            SubscriptionTime.ONE_MONTH -> subscriptionDate.plusMonths(1).toDate()
            SubscriptionTime.ONE_YEAR -> subscriptionDate.plusYears(1).toDate()
            SubscriptionTime.UNLIMITED -> null
        }
    }

    @SuppressLint("NewApi")
    fun getMyDay(
        date: LocalDate,
        num: Int? = null
    ): Pair<Int, AnnotatedString> {

        val currentYear = date.year
        val currentMonth = date.month.value
        val currentDay = date.dayOfMonth

        val myMonth = getMyMonth(
            currentYear,
            currentMonth
        )

        var number = num ?: (myMonth.first + currentDay)
        var sum = 0

        while(number > 0){
            sum += number % 10
            number /=10
        }

        return when(sum){
            1 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 1. ")
                }
                append("Используйте энергию дня правильно: не уходите в эгоизм. Важно оставаться в покое и выстраивать стратегию задуманного плана.")
            }
            2 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 2.")
                }
                append(" Будьте хорошим дипломатом. Может появиться желание разорвать отношения, но их необходимо налаживать. В ресурсном состоянии через энергию понимания удастся добиться идеальных договоренностей.")
            }
            3 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 3.")
                }
                append(" Есть вероятность азарта, но за этим могут последовать потери. Будет желание получения легкой выгоды. Поэтому важно все делать включив анализ")
            }
            4 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 4.")
                }
                append(" Важно быть на позитиве, чтобы были только положительные мистические события, иначе могут быть потери и неожиданные неприятности")
            }
            5 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 5.")
                }
                append(" Все возможности приходят через коммуникацию. Налаживайте связи, знакомьтесь и общайтесь!")
            }
            6 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 6.")
                }
                append(" Сегодня могут сбыться все ваши мечты! Также возможны обострения хронических заболеваний и повышенный эмоциональный фон, а страдания из-за отсутствия комфорта в любых аспектах.")
            }
            7 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 7.")
                }
                append(" Держите себя в дисциплине: служение, молитва, занятие йогой, обучение и задача от Творца!")
            }
            8 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 8. ")
                }
                append("Труд принесет финансовый результат. Могут быть сомнения, недоверие, желание все тотально проконтролировать.")
            }
            9 -> sum to buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("Сегодня ваш личный день 9. ")
                }
                append("Не ведитесь на эмоции, оставайтесь в покое, тогда можно обнаружить новые возможности")
            }
            else -> getMyDay(date, sum)
        }
    }

    fun getMyMonth(currentYear: Int, currentMonth: Int): Pair<Int, AnnotatedString> {
        val myYear = getMyYear(currentYear)

        var number = myYear.first + currentMonth
        var sum = 0

        while(number > 0){
            sum += number % 10
            number /=10
        }

        return sum to when(sum) {
            1 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 1 проходит под влиянием Солнца!")
                        }
                    }
                    append("\n\n")
                    append("● В «плюсе» - ЭГО ищет счастье, тогда необходимо открыть свое дело, создание чего-то нового, начало новой жизни.")
                    append("● В «минусе» - ЭГО страдает: жжение сердечной чакры, внутри возможно ощущение духоты. Человек не будет знать куда себя деть")
                }
            }
            2 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 2 проходит под влиянием Луны!")
                        }
                    }
                    append("\n\n")
                    append("● Можно налаживать отношения.")
                    append("● Когда человек в «плюсе» - включается дипломатии. Приходит желание создании новых отношения или укрепить старые.")
                    append("● В «минусе» нельзя принимать серьезные решения в отношениях. Так как может произойти их разрыв.")
                    append("● Возможны депрессии и неуверенность.")
                }
            }
            3 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 3 проходит под влиянием Юпитера!")
                        }
                    }
                    append("\n\n")
                    append("● В «плюсе» станет месяцем анализа и успеха.")
                    append("● В «минусе» включится азарт и разрушения.")
                }
            }
            4 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 1 проходит под влиянием Солнца!")
                        }
                    }
                    append("\n\n")
                    append("● Месяц мистики.")
                    append("● Возможны неизвестные мистические события.")
                    append("● В «плюсе» — это положительные события и мистика")
                    append("● В «минусе» - это отрицательные события, отрицательная Мистика")
                }
            }
            5 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 5 проходит под влиянием Меркурия!")
                        }
                    }
                    append("\n\n")
                    append("● Месяц коммуникаций, когда все тайное становится явным. Скелеты выпадают из шкафа. Человек будет говорить о. вещах, что «правильно и неправильно».")
                    append("● В «минусе» разрушается логика.")
                    append("● В «плюсе», как только скажет «правильно», пойдет трансформация.")
                }
            }
            6 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 6 проходит под влиянием Венеры!")
                        }
                    }
                    append("\n\n")
                    append("● В «плюсе»: развитие, успех и могут сбываться мечты.")
                    append("● В «минусе»: обострение хронических заболеваний. Стоит завершить все незаконченные дела.")
                }
            }
            7 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 7 проходит под влиянием Кету!")
                        }
                    }
                    append("\n\n")
                    append("● Месяц трансформации и кризиса. Нужна дисциплина.")
                    append("● В «плюсе»: трансформация сознания, где необходимо полностью изменить свое сознание.")
                    append("● В «минусе»: кризис. Кризис воспринимать как избавление от чего-то ненужного. Важно трансформироваться, уйти от страданий.")
                }
            }
            8 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 1 проходит под влиянием Солнца!")
                        }
                    }
                    append("\n\n")
                    append("● Месяц реализации кармы. Человек должен учиться и работать. Уходите от тотального контроля ситуации!")
                }
            }
            9 -> {
                buildAnnotatedString {
                    withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                            append("Ваш личный месяц 9 проходит под влиянием Марса!")
                        }
                    }
                    append("\n\n")
                    append("● Нужно принимать все, что происходит.")
                    append("● В «плюсе»: когда понимаешь, что смерть неизбежна и что может быть разрушение. Принимать ситуацию.")
                    append("● В «минусе»: это все равно произойдет. В этом месяце нужно проявлять волю к победе. Сабр.")
                }
            }
            else -> buildAnnotatedString {}
        }
    }

    @SuppressLint("NewApi")
    fun getMyYear(currentYear: Int): Pair<Int, AnnotatedString> {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = simpleDateFormat.parse(this.birthday).toLocalDate()

        var year = currentYear
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

        return number to when(number) {
            1 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 1 : СОЛНЦЕ")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД НАЧАЛА ВСЕГО НОВОГО")
                    }
                }

                append("\n")
                append(" Когда приходит год Солнца, человеку необходимо открыть новое дело (начинается новая жизнь). Солнце хочет и может свернуть горы.")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append("Принятие решений. Стратегия. Начать что-то новое \n" +
                        "Когда человек в ресурсном состоянии, он может открыть собственное прибыльное дело.\n")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append("Эгоизм. Идеализм. Деспотизм.")
                append(" В негативе у человека происходит сжигание сердечной чакры (возможно ощущение жжения в груди). Человек испытывает депрессию. Не знает куда себя деть свою энергию.")
                append(" Человеку следует смотреть на мир по-новому")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Открыть новое дело")
                append(" ● Принимать решения")
                append(" ● Нарабатывать лидерские качества")
                append(" ● Развивать стратегическое мышление.")
            }
            2 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 2: ЛУНЫ")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД ДИПЛОМАТИИ.")
                    }
                }

                append("\n")
                append(" В этот год человеку нельзя принимать серьёзных решений.")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append(" Реалистичность. Чувственность. Психология. Нежность. Дипломатичность. Можно построить крепкие отношения с людьми и второй половинкой.")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append(" Сомнение. Двойственность. Депрессивность. Будут разрывы отношений и депрессии (страдания).")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Учиться понимать людей̆. Прийти к пониманию данности человека")
                append(" ● Задавать вопросы: “А правильно ли я вас понял? А вам именно это нужно?”")
                append(" ● Поход в театр 2 раза в месяц с особым вниманием к эмоциям актёров и проживанием (включением) актёров и их результатам.")
                append(" ● Посмотреть историю происхождения эмоций и какие результаты этих эмоций.")
                append(" ● Посетить 5 различных религиозных конфессий (Христианство, Буддизм, Ислам, Иудаизм и др.).")
            }
            3 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 3: ЮПИТЕР")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД АНАЛИЗА И УСПЕХА")
                    }
                }

                append("\n")
                append(" ")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append("Стратегия, анализ, планирование")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append("Принципиальность. Лень. Азарт. Категоричность. Это азарт и разрушения (Действия азартные).")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Вести ежедневник.")
                append(" ● Анализировать ситуации из жизни.")
                append(" ● Планировать свой день и вечером проводить его анализ.")
                append(" ● Контролировать деньги.")
            }
            4 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 4: РАХУ")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД МИСТИКИ")
                    }
                }

                append("\n")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append("Знания. Механизм постановки целей. Желание отдать Механизм реализации целей. Позитивные мистические события, которые не поддаются здравому смыслу. Пример: внезапное получение гранта на обучение или премия на работе, и т.д.")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»:")
                }
                append(" Неудовлетворенность. Разрушение. Отчужденность. Апатия. Мошенничество. Отрицательные мистические события. Пример: выпишут штраф, аннулируют результаты труда, и т.д.")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Получать знания и определять цели.")
                append(" ● Тратить деньги на курсы и обучения, получать сертификаты.")
                append(" ● Выписать 100-400 своих целей.")
                append(" ● Освоить инструменты целеполагания.")
            }
            5 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 5: МЕРКУРИЙ")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД КОММУНИКАЦИИ")
                    }
                }

                append("\n")
                append(" Год, когда всё тайное становится явным (скелеты выпадают из шкафа).")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append("это проявление коммуникации. Деловая коммуникация. Коммуникации. Логика. Адекатное восприятие. Интеллект.")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append("Непостоянство. Беспечность. Безумие. Экстремизм. Обидчивость. Полное разрушение логики (Человек будет говорить правильно/неправильно). Жизнь покажется нелогичной̆.")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Прочитать книгу Д. Троцкого “Пока-я-не-Я”.")
                append(" ● Ежедневно коммуницировать с 1-5 человеком выше себя по уровню успешности, компетенции и доходах в поставленной цели.")
                append(" ● Разработать 30-секундную презентацию себя, чтобы к вам выстроилась очередь.")
                append(" ● Проводить мастер-классы, передавать знания на публику.")
                append(" ● Выписать 5 человек, которые обидели вас или обидели вы.")
                append(" ● Позвонить и попросить прощения у них, поблагодарить их за то, что он преподал урок и был учителем для нас.")
                append(" ● Простить долги и обязательно позвонить этим людям и сказать им, что вы простили им их долги и что вы всегда рады им в гостях на чай.")
                append(" ● Масштабироваться (например проводите обучение: надо значит провести в другом месте обучение, или если готовите торты - значит начните печь еще печенье, открыли свою кофейню - значит настало время открыть еще одну и т.д")
            }
            6 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 6: ВЕНЕРА")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД ЛЮБВИ И УСПЕХА.")
                    }
                }

                append("\n")
                append(" В этом году могут сбыться все мечты и желания. ")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append("год любви и успеха. Мудрость. Счастье. Завершение начатых дел. Внутренний̆ комфорт")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append("год хронических заболеваний Сверхкомфорт. Противозаконность. Неразборчивость. Месть")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Прочитать книгу Г. Чепмена “Пять языков любви”.")
                append(" ● Благодарить Творца с утра и перед сном с конкретным перечислением за что.")
                append(" ● Принимать кислую пищу на завтрак (Начинает вырабатываться окситоцин).")
                append(" ● Пить тёплую воду (1 л на 30 кг).")
                append(" ● Вода, баня, сауна, бассейн, верховая езда.")
                append(" ● Созерцать природу, смотреть на прекрасное, вдохновение.")
                append(" ● Медитация на любовь.")
                append(" ● Дневник Успеха (каждый̆ день по пяти пунктов, за что можешь себя похвалить).")
            }
            7 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 7: КЕТУ.")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД КРИЗИСА И ТРАНСФОРМАЦИИ.")
                    }
                }

                append("\n")
                append(" Задача - уйти от страданий и прийти в трансформацию.")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append("Гениальность. Божественный бензин. Кундалини. Внутренний реактор (Двигатель). Темпераментность (Секс. энергия).")

                append("\n")
                append(" Будет трансформация сознания, расширение сознания, выход на новый уровень.")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append(" Хаос. Рассеянность. Непонимание ситуации. Сверхуверенность. Кризис.")

                append(" В этот год нельзя продавать/покупать недвижимость.\n" +
                        " В этом году необходимо фиксировать все деньги.\n" +
                        " В этом году ты отвечаешь за все свои неправильные действия за прошлые 9 лет.\n" +
                        " В этому году нельзя делать операции.\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Поднимать уровень Кундалини.")
                append(" ● Ходьба минимум 6 км в день со скоростью от 5 км/час.")
                append(" ● Делать 10-минутный кардиокомплекс.")
                append(" ● Плавание.")
                append(" ● Лыжи.")
                append(" ● Секс 45 минут.")
                append(" ● Ходить в баню.")
                append(" ● Потеть.")
                append(" ● Медитаций.")
                append(" ● Ещё больше поднимания энергии для тех, кто родился 10, 20, 30 числа.")
            }
            8 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД 8: САТУРН")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("ГОД РЕАЛИЗАЦИИ КАРМЫ ЧЕЛОВЕКА")
                    }
                }

                append("\n")
                append("  Надо работать над качеством работы/учебы. В этот год необходимо много учиться и много работать, т. е. инвестировать в обучение. ")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append("Опыт. Повторение. Труд. Карма. Мудрость прошлых жизней̆. Контроль. Если человек учится и работает — это всё будет для него опытом на долгие годы. ")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append("Сомнение. Недоверие. Тотальный контроль. Человек попадает в зону ограничений.")

                append(" В этом году нельзя прожигать время в кайф.\n" +
                        "В этом году нельзя заключать брак. Семейные узы.\n" +
                        "Брак, заключённый̆ в этот год, может быть расторгнут через 10–15 лет. Нельзя брать кредиты и расширять бизнес.\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("Что нужно делать:")
                    }
                }

                append(" ● Трудиться 20 часов в день.")
                append(" ● Нарабатывать навыки.")
                append(" ● Стать профессионалом.")
            }
            9 -> buildAnnotatedString {
                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("")
                    }
                }

                append("\n")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("")
                    }
                }

                append("\n")
                append(" ")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «плюсе»: ")
                }
                append(" ")

                append("\n")
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                    append("  В «минусе»: ")
                }
                append(" ")

                append(" ")
                append(" ")

                withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)){
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)){
                        append("")
                    }
                }

                append(" ●")
            }
            else -> buildAnnotatedString {}
        }
    }
}

fun DataSnapshot.mapUser(): User {
    return User(
        id = this.child("id").value.toString(),
        email = this.child("email").value.toString(),
        password = this.child("password").value.toString(),
        premium = this.child("premium").value.toString().toBoolean(),
        premiumDate = this.child("premiumDate").value.toString(),
        birthday = this.child("birthday").value.toString(),
        userRole = enumValueOf(this.child("userRole").value.toString()),
    )
}

@SuppressLint("NewApi")
fun getAdvice(
    date: LocalDate
): List<Advice> {

    var day = date.dayOfMonth

    if(day % 10 == 0){
        return listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Adverse
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Adverse
            ),
            Advice(
                "Начало проекта",
                AdviceState.Adverse
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Adverse
            )
        )
    }

    var sum = 0

    while(day > 0){
        sum += day % 10
        day /=10
    }

    return when(sum){
        1 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Neutral
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Neutral
            ),
            Advice(
                "Начало проекта",
                AdviceState.Favorable
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Neutral
            )
        )
        2 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Neutral
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Neutral
            ),
            Advice(
                "Начало проекта",
                AdviceState.Neutral
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Neutral
            ),
        )
        3 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Favorable
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Favorable
            ),
            Advice(
                "Начало проекта",
                AdviceState.Favorable
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Favorable
            ),
        )
        4 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Neutral
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Neutral
            ),
            Advice(
                "Начало проекта",
                AdviceState.Neutral
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Neutral
            ),
        )
        5 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Neutral
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Neutral
            ),
            Advice(
                "Начало проекта",
                AdviceState.Neutral
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Neutral
            ),
        )
        6 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Favorable
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Favorable
            ),
            Advice(
                "Начало проекта",
                AdviceState.Favorable
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Favorable
            ),
        )
        7 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Neutral
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Neutral
            ),
            Advice(
                "Начало проекта",
                AdviceState.Neutral
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Neutral
            ),
        )
        8 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Favorable
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Favorable
            ),
            Advice(
                "Начало проекта",
                AdviceState.Favorable
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Favorable
            ),
        )
        9 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Neutral
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Neutral
            ),
            Advice(
                "Начало проекта",
                AdviceState.Neutral
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Neutral
            ),
        )
        10, 20, 30 -> listOf(
            Advice(
                "Деловые сделки",
                AdviceState.Adverse
            ),
            Advice(
                "Регистрация брака",
                AdviceState.Adverse
            ),
            Advice(
                "Начало проекта",
                AdviceState.Adverse
            ),
            Advice(
                "Медицинские операции",
                AdviceState.Adverse
            ),
        )
        else -> emptyList()
    }
}