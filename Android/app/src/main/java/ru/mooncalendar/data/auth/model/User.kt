package ru.mooncalendar.data.auth.model

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.common.extension.toLocalDate
import java.time.LocalDate
import java.util.*

data class Advice(
    val parameter: String = "",
    val state: AdviceState
)

enum class AdviceState(val text: String) {
    Adverse("неблагоприятный"),
    Neutral("нейтральный"),
    Favorable("благоприятный"),
}

data class User(
    val id: String,
    val email: String,
    val password: String,
    val premium: Boolean = false,
    val premiumDate: String? = null,
    val birthday: String
){
    @SuppressLint("NewApi")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @SuppressLint("NewApi")
    fun isSubscription(): Boolean {
        val date = LocalDate.now()

        return premium && premiumDate != null
                && date <= simpleDateFormat.parse(premiumDate).toLocalDate().plusMonths(1)
    }

    @SuppressLint("NewApi")
    fun debitingFundsDate(): Date {
        val date = simpleDateFormat.parse(premiumDate).toLocalDate()
        return date.plusMonths(1).toDate()
    }

    @SuppressLint("NewApi")
    fun getMyDay(
        date: LocalDate
    ): Pair<Int, String> {

        val currentYear = date.year
        val currentMonth = date.month.value
        val currentDay = date.dayOfMonth

        val myMonth = getMyMonth(
            currentYear,
            currentMonth
        )

        var number = myMonth.first + currentDay
        var sum = 0

        while(number > 0){
            sum += number % 10
            number /=10
        }

        return sum to ""
    }

    fun getMyMonth(currentYear: Int, currentMonth: Int): Pair<Int, String> {
        val myYear = getMyYear(currentYear)

        var number = myYear.first + currentMonth
        var sum = 0

        while(number > 0){
            sum += number % 10
            number /=10
        }

        return sum to ""
    }

    @SuppressLint("NewApi")
    fun getMyYear(currentYear: Int): Pair<Int, String> {
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
            1 -> {
                "Год Солнца — это начало новой жизни.\n" +
                        "Когда приходит год Солнца, человеку необходимо открыть новое дело (начинается новая жизнь). Солнце хочет и может свернуть горы.\n" +
                        "В «плюсе»: Принятие решений. Стратегия. Начать что-то новое \n" +
                        "Когда человек в ресурсном состоянии, он может открытьсобственное прибыльное дело. \n" +
                        "В «минусе»:Эгоизм. Идеализм. Деспотизм.\n" +
                        "В негативеу человека происходит сжигание сердечной чакры (возможноощущение жжения в груди). Человек испытывает депрессию. Не знает куда себя деть свою энергию.\n" +
                        "\n" +
                        "Человеку следует смотреть на мир по-новому. \n" +
                        "Что нужно делать:\n" +
                        "- Открыть новое дело.\n" +
                        "- Принимать решения.\n" +
                        "- Нарабатывать лидерские качества.\n" +
                        "- Развивать стратегическое мышление.\n"
            }
            2 -> {
                "В этот год человеку нельзя принимать серьёзных решений.\n" +
                        "\n" +
                        "В «плюсе»:Реалистичность. Чувственность. Психология. Нежность. Дипломатичность.\n" +
                        "Можно построить крепкие отношения с людьми и второй половинкой.\n" +
                        "\n" +
                        "В «минусе»:Сомнение. Двойственность. Депрессивность. Будут разрывы отношений и депрессии (страдания).\n" +
                        "\n" +
                        "Что нужно делать:\n" +
                        "- учиться пониматьлюдей̆.Прийти к пониманию данности человека\n" +
                        "- Задавать вопросы:“А правильно ли я вас понял? А вам именно это нужно?”\n" +
                        "- Поход в театр 2 раза в месяц с особым вниманием к эмоциям актёров и проживанием (включением) актёров и их результатам.\n" +
                        "- Посмотреть историю происхождения эмоций и какие результаты этих эмоций.\n"
            }
            3 -> {
                "Год анализа и успеха (Полезный анализ).\n" +
                        "В «плюсе»:Стратегия, анализ, планирование \n" +
                        "\n" +
                        "В «минусе»:Принципиальность. Лень. Азарт. Категоричность. Это азарт и разрушения (Действия азартные). \n" +
                        "Что нужно делать:\n" +
                        "- Вести ежедневник. \n" +
                        "- Анализировать ситуации из жизни.\n" +
                        "- Планировать свой день и вечером проводить его анализ. - Контролировать деньги. \n"
            }
            4 -> {
                "Год мистики.\n" +
                        "\n" +
                        "В «плюсе»:Знания. Механизм постановки целей. Желание отдатьМеханизм реализации целей. \n" +
                        "позитивные мистические события, которые не поддаются здравому смыслу. Пример: внезапное получение гранта на обучение или премия на работе, и т.д.\n" +
                        "\n" +
                        "В «минусе»:Неудовлетворенность. Разрушение. Отчужденность. Апатия. Мошенничество.\n" +
                        "Отрицательные мистические события. Пример: выпишут штраф, аннулируют результаты труда, и т.д.\n" +
                        "\n" +
                        "Что нужно делать:\n" +
                        "- Получать знания и определять цели.\n" +
                        "- Тратить деньги на курсы и обучения, получать сертификаты.\n" +
                        "- Выписать 100-400 своих целей.\n" +
                        "- Освоить инструменты целеполагания.\n"
            }
            5 -> {
                "Год КОММУНИКАЦИИ\n" +
                        "Год, когда всё тайное становится явным (скелеты выпадают из шкафа). \n" +
                        "В «плюсе»:это проявление коммуникации.Деловая коммуникация. Коммуникации. Логика. Адекатное восприятие. Интеллект.\n" +
                        "\n" +
                        "В «минусе»:Непостоянство. Беспечность. Безумие. Экстремизм. Обидчивость. Полное разрушение логики (Человек будет говорить правильно/неправильно). Жизнь покажется нелогичной̆. \n" +
                        "Что нужно делать:\n" +
                        "- Прочитать книгу Д. Троцкого “Пока-я-не-Я”.\n" +
                        "- Ежедневно коммуницировать с 1-5 человеком выше себя по уровню успешности, компетенции и доходах в поставленной цели.\n" +
                        "- Разработать 30-секундную презентацию себя, чтобы к вам выстроилась очередь.\n" +
                        "- Проводить мастер-классы, передавать знания на публику.\n" +
                        "- Выписать 5 человек, которые обидели вас или обидели вы.\n" +
                        "- Позвонить и попросить прощения у них, поблагодарить их за то, что он преподал урок и был учителем для нас.\n" +
                        "- Простить долги и обязательно позвонить этим людям и сказать им, что вы простили им их долги и что вы всегда рады им в гостях на чай.\n" +
                        "- Масштабироваться (например проводите обучение: надо значит провести в другом месте обучение, или если готовите торты - значит начните печь еще печенье, открыли свою кофейню - значит настало время открыть еще одну и т.д\n"
            }
            6 -> {
                "ГОД ЛЮБВИ И УСПЕХА.\n" +
                        "В этом году могут сбыться все мечты и желания. \n" +
                        "В «плюсе»:год любви и успеха.Мудрость. Счастье. Завершение начатых дел. Внутренний̆ комфорт\n" +
                        "\n" +
                        "В «минусе»: год хронических заболеваний Сверхкомфорт. Противозаконность. Неразборчивость. Месть\n" +
                        "В этот год НУЖНО ДЕЛАТЬ:\n" +
                        "- Прочитать книгу Г. Чепмена “Пять языков любви”.\n" +
                        "- Благодарить Творца с утра и перед сном с конкретным перечислением за что.\n" +
                        "- Принимать кислую пищу на завтрак (Начинает вырабатываться окситоцин).\n" +
                        "- Пить тёплую воду (1 л на 30 кг).\n" +
                        "- Вода, баня, сауна, бассейн, верховая езда.\n" +
                        "- Созерцать природу, смотреть на прекрасное, вдохновение.\n" +
                        "- Медитация на любовь.\n" +
                        "- Дневник Успеха (каждый день по пяти пунктов, за что можешь себя похвалить). \n"
            }
            7 -> {
                "Год КРИЗИСА И ТРАНСФОРМАЦИИ.\n" +
                        "Задача - уйти от страданий и прийти в трансформацию.\n" +
                        "\n" +
                        "В «плюсе»:Гениальность. Божественный бензин. Кундалини. Внутренний реактор (Двигатель). Темпераментность (Секс. энергия).\n" +
                        "Будет трансформация сознания, расширение сознания, выход на новый уровень.\n" +
                        "В «минусе»:Хаос. Рассеянность. Непонимание ситуации. Сверхуверенность. Кризис.\n" +
                        "\n" +
                        "В этот год нельзя продавать/покупать недвижимость.\n" +
                        "В этом году необходимо фиксировать все деньги.\n" +
                        "В этом году ты отвечаешь за все свои неправильные действия за прошлые 9 лет.\n" +
                        "В этому году нельзя делать операции.\n" +
                        "\n" +
                        "Что нужно делать:\n" +
                        "- Поднимать уровень Кундалини.\n" +
                        "- Ходьба минимум 6 км в день со скоростью от 5 км/час.\n" +
                        "- Делать 10-минутный кардиокомплекс.\n" +
                        "- Плавание.\n" +
                        "- Лыжи.\n" +
                        "- Секс 45 минут.\n" +
                        "- Ходить в баню.\n" +
                        "- Потеть.\n" +
                        "- Медитаций.\n" +
                        "- Ещё больше поднимания энергии для тех, кто родился 10, 20, 30 числа. \n"
            }
            8 -> {
                "Надо работать над качеством работы/учебы. В этот год необходимо много учиться и много работать, т.е. инвестировать в обучение. \n" +
                        "В «плюсе»:Опыт. Повторение. Труд. Карма. Мудрость прошлых жизней. Контроль. Если человек учится и работает — это всё будет для него опытом на долгие годы. \n" +
                        "В «минусе»:Сомнение. Недоверие. Тотальный контроль. Человек попадает в зону ограничений.\n" +
                        "\n" +
                        "В этом году нельзя прожигать время в кайф.\n" +
                        "В этом году нельзя заключать брак. Семейные узы.\n" +
                        "Брак, заключенный в этот год, может быть расторгнут через 10-15 лет. Нельзя брать кредиты и расширять бизнес.\n" +
                        "\n" +
                        "Что нужно делать:\n" +
                        "- Трудиться 20 часов в день.\n" +
                        "- Нарабатывать навыки.\n" +
                        "- Стать профессионалом.\n"
            }
            9 -> {
                "Год, когда может быть разрушено всё то, что долго создавалось. В этот год человек может умереть (могут умереть близкие, заканчивается работа).\n" +
                        "\n" +
                        "В «плюсе»:Механическое действие. Идеи. Возможности. Действие. Эмоции. Служение. Понимание, что разрушения неизбежны (родившись, человек должен знать, что умрёт). \n" +
                        "В «минусе»:Ярость. Паранойя. Наивность. Разрушение. \n" +
                        "Если человек боится, его год смерти неизбежен. В этот год делать максимум жертвоприношений. \n" +
                        "\n" +
                        "Что нужно делать:\n" +
                        "- Действовать. \n" +
                        "-Следить за здоровьем тела.\n" +
                        "- Заниматься соревновательным спортом - придёт ощущение своих возможностей и уверенность в себе.\n" +
                        "- Пройти курсы по принятию решения и действия, например, пройти курс “Спарта”.\n"
            }
            else -> ""
        }
    }

    @SuppressLint("NewApi")
    fun getAdvice(
        date: LocalDate
    ): List<Advice> {

        return when(getMyDay(date).first){
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
                ),
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
            else -> listOf(
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
    )
}