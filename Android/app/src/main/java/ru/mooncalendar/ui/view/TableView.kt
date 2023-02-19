package ru.mooncalendar.ui.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mooncalendar.ui.theme.primaryText

@Composable
fun TableCell(
    text: String,
    width: Dp
) {
    Text(
        text = text,
        modifier = Modifier
            .border(1.dp, primaryText())
            .width(width)
            .padding(8.dp),
        color = primaryText(),
        textAlign = TextAlign.Center
    )
}