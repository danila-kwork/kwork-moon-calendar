package ru.mooncalendar.data.subscriptionStatement.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.database.DataSnapshot
import ru.mooncalendar.screens.PayType

data class SubscriptionStatement(
    var id: String = "",
    var userId: String = "",
    val numberCard: String?,
    var status: SubscriptionStatementStatus = SubscriptionStatementStatus.WAITING,
    val type: SubscriptionType,
    val qiwiBillId: String?,
    val payTyp: PayType
)

enum class SubscriptionStatementStatus(val text: String) {
    WAITING("Ожитайте подверждения"),
    PAID("Успешно")
}

enum class SubscriptionTime {
    ONE_MONTH,
    ONE_YEAR,
    UNLIMITED
}

enum class SubscriptionType(
    val title: String,
    val desc: String,
    val priceRu: Pair<Int, String>,
    val priceKz: Pair<Int, String>,
    val time: SubscriptionTime,
    val color: Color,
) {
    LITE_MIN(
        "Lite",
        "*благоприятные/нейтральные/неблагоприятные дни для заключения сделок, крупных покупок, принятия важных решений",
        210 to "рублей/месяц", //1200
        1200 to "тенге/месяц", //1200
        SubscriptionTime.ONE_MONTH,
        color = Color(0xFFCD7F32)
    ),
    LITE_MAX(
        "Lite подписка на год",
        "*благоприятные/нейтральные/неблагоприятные дни для заключения сделок, крупных покупок, принятия важных решений",
        1500 to "рублей/год\nВЫГОДА: до 40%!", // 8900
        8900 to "тенге/год\nВЫГОДА: до 40%!", // 8900
        SubscriptionTime.ONE_YEAR,
        color = Color(0xFFCD7F32)
    ),
    SILVER_MIN(
        "Silver",
        "* ОБЩИЙ и ЛИЧНЫЙ календарь 2023 с благоприятными/нейтральными/неблагоприятными днями для заключения сделок, крупных покупок, принятия важных решений\n" +
            "\n" +
            "* Аффирмации на каждый день \n" +
            "\n" +
            "* Шагомер\n" +
            "\n" +
            "* Рекомендации по личному году/месяцу",
        402 to "рублей/месяц", //2400 тенге
        2400 to "тенге/месяц", //2400 тенге
        SubscriptionTime.ONE_MONTH,
        color = Color(0xFFD8D8D8)
    ),
    SILVER_MAX(
        "Silver подписка на год",
        "* ОБЩИЙ и ЛИЧНЫЙ календарь 2023 с благоприятными/нейтральными/неблагоприятными днями для заключения сделок, крупных покупок, принятия важных решений\n" +
                "\n" +
                "* Аффирмации на каждый день \n" +
                "\n" +
                "* Шагомер\n" +
                "\n" +
                "* Рекомендации по личному году/месяцу",
        2670 to "рублей/год\nВЫГОДА: до 50%!",
        15900 to "тенге/год\nВЫГОДА: до 50%!",
        SubscriptionTime.ONE_YEAR,
        color = Color(0xFFD8D8D8)
    ),
    GOLD(
        "Gold",
        "* ОБЩИЙ и ЛИЧНЫЙ календарь 2023 с благоприятными/нейтральными/неблагоприятными днями для заключения сделок, крупных покупок, принятия важных решений\n" +
            "\n" +
            "* Аффирмации на каждый день \n" +
            "\n" +
            "* Шагомер\n" +
            "\n" +
            "* Рекомендации по личному году/месяцу\n" +
            "\n" +
            "* ЛИЧНАЯ КОНСУЛЬТАЦИЯ в два этапа от профессионального мастера Сюцай Лунары Канаш с полным разбором",
        7044 to "рублей/месяц",
        42000 to "тенге/месяц",
        SubscriptionTime.UNLIMITED,
        color = Color(0xFFFED838)
    )
}

fun DataSnapshot.mapSubscriptionStatement() : SubscriptionStatement? {
    return try {
        SubscriptionStatement(
            id = this.child("id").value.toString(),
            userId = this.child("userId").value.toString(),
            numberCard = this.child("numberCard").value.toString(),
            status = enumValueOf(this.child("status").value.toString()),
            type = enumValueOf(this.child("type").value.toString()),
            payTyp = enumValueOf<PayType>(this.child("payTyp").value.toString()),
            qiwiBillId = if(this.child("qiwiBillId").value != null)
                this.child("qiwiBillId").value.toString()
            else null
        )
    }catch (e:Exception){
        null
    }
}

data class SubscriptionTableRow(
    val text: String,
    val liteSubscription: Boolean,
    val silverSubscription: Boolean,
    val goldSubscription: Boolean,
)

val subscriptionTableRows = listOf(
    SubscriptionTableRow(
        text = "Рекомендации",
        liteSubscription = true,
        silverSubscription = true,
        goldSubscription = true,
    ),
    SubscriptionTableRow(
        text = "Личный месяц",
        liteSubscription = false,
        silverSubscription = true,
        goldSubscription = true
    ),
    SubscriptionTableRow(
        text = "Личный год",
        liteSubscription = false,
        silverSubscription = true,
        goldSubscription = true
    ),
    SubscriptionTableRow(
        text = "Аффирмация",
        liteSubscription = false,
        silverSubscription = true,
        goldSubscription = true
    ),
    SubscriptionTableRow(
        text = "Шагомер",
        liteSubscription = false,
        silverSubscription = true,
        goldSubscription = true
    ),
    SubscriptionTableRow(
        text = "Консультация",
        liteSubscription = false,
        silverSubscription = false,
        goldSubscription = true
    )
)