package com.bookspine.readinglog.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.theme.PrimaryGreen
import com.bookspine.readinglog.core.theme.PrimaryGreenDeep
import com.bookspine.readinglog.core.theme.SecondaryTeal
import com.bookspine.readinglog.core.theme.SurfaceAlt
import com.bookspine.readinglog.core.theme.TextPrimary

@Composable
fun BigStepper(
    value: Int,
    minValue: Int,
    maxValue: Int,
    decreaseDescription: String,
    increaseDescription: String,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    quickSteps: List<Int> = emptyList(),
    quickLabel: (Int) -> String = { step -> step.toString() },
    valueFontSize: TextUnit = 56.sp
) {
    val canDecrease = value > minValue
    val canIncrease = value < maxValue

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onChange((value - 1).coerceAtLeast(minValue)) },
                enabled = canDecrease,
                modifier = Modifier
                    .size(64.dp)
                    .alpha(if (canDecrease) 1f else 0.38f)
                    .background(SecondaryTeal, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_minus),
                    contentDescription = decreaseDescription,
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    (fadeIn(tween(150)) + scaleIn(initialScale = 0.9f))
                        .togetherWith(fadeOut(tween(120)))
                }
            ) { shown ->
                Text(
                    text = shown.toString(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = valueFontSize,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            IconButton(
                onClick = { onChange((value + 1).coerceAtMost(maxValue)) },
                enabled = canIncrease,
                modifier = Modifier
                    .size(64.dp)
                    .alpha(if (canIncrease) 1f else 0.38f)
                    .background(
                        brush = Brush.linearGradient(listOf(PrimaryGreen, PrimaryGreenDeep)),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = increaseDescription,
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        if (quickSteps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickSteps.forEach { step ->
                    val reachable = value + step <= maxValue
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceAlt,
                        border = BorderStroke(1.dp, SecondaryTeal),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .alpha(if (reachable) 1f else 0.38f)
                            .clickable(enabled = reachable) {
                                onChange((value + step).coerceAtMost(maxValue))
                            }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = quickLabel(step),
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
