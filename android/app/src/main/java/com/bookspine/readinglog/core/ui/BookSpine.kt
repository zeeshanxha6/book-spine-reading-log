package com.bookspine.readinglog.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.theme.SpineColors
import com.bookspine.readinglog.core.theme.TextPrimary

@Composable
fun BookSpine(
    title: String,
    variant: Int,
    colorIndex: Int,
    width: Dp,
    height: Dp,
    description: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val art = when (variant % 3) {
        0 -> R.drawable.sprite_1
        1 -> R.drawable.sprite_2
        else -> R.drawable.sprite_3
    }
    val tint = SpineColors[colorIndex.mod(SpineColors.size)]
    val base = modifier
        .size(width = width, height = height)
        .sizeIn(minWidth = 20.dp, minHeight = 48.dp)
        .semantics { contentDescription = description }
    val clickModifier = if (onClick != null) base.clickable(onClick = onClick) else base

    Box(modifier = clickModifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(art),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            colorFilter = ColorFilter.tint(tint, BlendMode.Modulate),
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(1.dp)
                .fillMaxHeight()
                .background(Color(0x1AFFFFFF))
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(2.dp)
                .fillMaxHeight()
                .background(Color(0x55000000))
        )
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = TextPrimary,
            modifier = Modifier
                .requiredWidth(height - 18.dp)
                .graphicsLayer { rotationZ = -90f }
        )
    }
}
