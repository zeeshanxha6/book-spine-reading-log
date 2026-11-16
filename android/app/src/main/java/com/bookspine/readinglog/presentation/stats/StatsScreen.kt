package com.bookspine.readinglog.presentation.stats

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.di.AppViewModelFactory
import com.bookspine.readinglog.core.theme.AccentRed
import com.bookspine.readinglog.core.theme.BgBase
import com.bookspine.readinglog.core.theme.BgDeep
import com.bookspine.readinglog.core.theme.PrimaryGreen
import com.bookspine.readinglog.core.theme.PrimaryGreenDeep
import com.bookspine.readinglog.core.theme.PrimaryGreenSoft
import com.bookspine.readinglog.core.theme.SecondaryTeal
import com.bookspine.readinglog.core.theme.TextMuted
import com.bookspine.readinglog.core.theme.TextPrimary
import com.bookspine.readinglog.core.theme.TextSecondary
import com.bookspine.readinglog.core.ui.ProgressTrack
import com.bookspine.readinglog.core.ui.ScreenBackground
import com.bookspine.readinglog.core.ui.ScreenHeader
import com.bookspine.readinglog.core.ui.SectionTitle
import com.bookspine.readinglog.core.ui.StatCard

@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = viewModel(factory = AppViewModelFactory.instance)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScreenBackground(
        overlay = listOf(
            BgBase.copy(alpha = 0.58f),
            BgBase.copy(alpha = 0.88f),
            BgDeep
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.stats),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SecondaryTeal.copy(alpha = 0.5f),
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(
                        text = state.year.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryGreenSoft,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SectionTitle(text = stringResource(R.string.pages_per_month))
                MonthChart(state = state)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            icon = Icons.Default.CheckCircle,
                            value = state.totalPages.toString(),
                            label = stringResource(R.string.stat_pages),
                            accent = PrimaryGreen
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            icon = Icons.Default.Star,
                            value = state.finishedBooks.toString(),
                            label = stringResource(R.string.stat_books),
                            accent = TextPrimary
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            icon = Icons.Default.Info,
                            value = state.averagePerMonth.toString(),
                            label = stringResource(R.string.stat_average),
                            accent = AccentRed
                        )
                    }
                }

                PaceCard(state = state)
            }
        }
    }
}

@Composable
private fun MonthChart(state: StatsUiState) {
    val letters = stringArrayResource(R.array.month_letters)
    val chartDescription = stringResource(
        R.string.cd_month_chart,
        letters.getOrElse(state.bestMonthIndex) { "" },
        state.pagesPerMonth.getOrElse(state.bestMonthIndex) { 0 }
    )
    val grow by animateFloatAsState(
        targetValue = if (state.hasData) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing)
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .semantics { contentDescription = chartDescription }
            ) {
                val count = 12
                val gap = 6.dp.toPx()
                val barWidth = (size.width - gap * (count - 1)) / count
                val maxValue = state.maxMonthPages.coerceAtLeast(1)
                val baseline = size.height - 2.dp.toPx()
                drawRect(
                    color = SecondaryTeal,
                    topLeft = Offset(0f, baseline),
                    size = Size(size.width, 1.dp.toPx())
                )
                for (index in 0 until count) {
                    val value = state.pagesPerMonth.getOrElse(index) { 0 }
                    val ratio = value.toFloat() / maxValue
                    val barHeight = baseline * ratio * grow
                    if (barHeight <= 0f) continue
                    val left = index * (barWidth + gap)
                    val best = index == state.bestMonthIndex
                    drawRoundRect(
                        brush = if (best) {
                            Brush.verticalGradient(listOf(AccentRed, AccentRed))
                        } else {
                            Brush.verticalGradient(listOf(PrimaryGreen, PrimaryGreenDeep))
                        },
                        topLeft = Offset(left, baseline - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                letters.forEach { letter ->
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaceCard(state: StatsUiState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SecondaryTeal),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(text = stringResource(R.string.pace))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        state.booksAheadOfPace > 0 -> stringResource(
                            R.string.pace_ahead,
                            state.booksAheadOfPace
                        )
                        state.booksAheadOfPace < 0 -> stringResource(
                            R.string.pace_behind,
                            -state.booksAheadOfPace
                        )
                        else -> stringResource(R.string.pace_on_track)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProgressTrack(progress = state.goalProgress, height = 6.dp)
            }
        }
    }
}
