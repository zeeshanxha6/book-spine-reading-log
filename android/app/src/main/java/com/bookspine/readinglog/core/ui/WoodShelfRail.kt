package com.bookspine.readinglog.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bookspine.readinglog.core.theme.WoodDark
import com.bookspine.readinglog.core.theme.WoodLight
import com.bookspine.readinglog.core.theme.WoodMid

@Composable
fun WoodShelfRail(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(WoodDark, WoodLight, WoodMid)),
                    shape = RoundedCornerShape(3.dp)
                )
        ) {}
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .height(2.dp)
                .background(Color(0x55000000), RoundedCornerShape(2.dp))
        ) {}
    }
}
