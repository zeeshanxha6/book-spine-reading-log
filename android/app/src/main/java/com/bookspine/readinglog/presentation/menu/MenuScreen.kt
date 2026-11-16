package com.bookspine.readinglog.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.core.di.AppViewModelFactory
import com.bookspine.readinglog.core.theme.AccentRed
import com.bookspine.readinglog.core.theme.BgBase
import com.bookspine.readinglog.core.theme.BgDeep
import com.bookspine.readinglog.core.theme.PrimaryGreen
import com.bookspine.readinglog.core.theme.PrimaryGreenSoft
import com.bookspine.readinglog.core.theme.SecondaryTeal
import com.bookspine.readinglog.core.theme.TextPrimary
import com.bookspine.readinglog.core.theme.TextSecondary
import com.bookspine.readinglog.core.ui.BookSpine
import com.bookspine.readinglog.core.ui.GlassPanel
import com.bookspine.readinglog.core.ui.PrimaryButton
import com.bookspine.readinglog.core.ui.ScreenBackground
import com.bookspine.readinglog.core.ui.SecondaryButton
import com.bookspine.readinglog.core.ui.StatCard
import com.bookspine.readinglog.core.ui.WoodShelfRail
import com.bookspine.readinglog.domain.model.Book

@Composable
fun MenuScreen(
    onStart: () -> Unit,
    onOpenShelf: (String) -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.instance)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var sheetShown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        sheetShown = true
    }

    ScreenBackground(
        overlay = listOf(
            BgBase.copy(alpha = 0.35f),
            BgBase.copy(alpha = 0.72f),
            BgDeep
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ShelfZone(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            AnimatedVisibility(
                visible = sheetShown,
                enter = slideInVertically { full -> full / 4 } + fadeIn(tween(200))
            ) {
                MenuSheet(
                    state = state,
                    onStart = onStart,
                    onOpenShelf = onOpenShelf,
                    onOpenStats = onOpenStats,
                    onOpenSettings = onOpenSettings
                )
            }
        }
    }
}

@Composable
private fun ShelfZone(state: MenuUiState, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        val shelfMaxWidth = (maxWidth - 32.dp).coerceAtMost(380.dp)
        val frame = 12.dp
        val gap = 4.dp
        val cols = GameConfig.SHELF_COLS
        val spineWidth = (shelfMaxWidth - frame * 2 - gap * (cols - 1)) / cols
        val shelfWidth = spineWidth * cols + gap * (cols - 1) + frame * 2
        val spineHeight = 104.dp

        if (state.shelfEmpty) {
            Column(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = stringResource(R.string.cd_empty_shelf),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(96.dp)
                        .alpha(0.55f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.shelf_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        } else {
            val rows = state.visibleSpines.chunked(cols)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                rows.forEach { row ->
                    ShelfRow(
                        books = row,
                        spineWidth = spineWidth,
                        spineHeight = spineHeight,
                        shelfWidth = shelfWidth,
                        frame = frame,
                        gap = gap
                    )
                    WoodShelfRail(modifier = Modifier.width(shelfWidth))
                    Spacer(modifier = Modifier.height(14.dp))
                }
                if (state.extraSpines > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryTeal.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = stringResource(R.string.shelf_more, state.extraSpines),
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryGreenSoft,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelfRow(
    books: List<Book>,
    spineWidth: Dp,
    spineHeight: Dp,
    shelfWidth: Dp,
    frame: Dp,
    gap: Dp
) {
    Row(
        modifier = Modifier
            .width(shelfWidth)
            .padding(horizontal = frame),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.Bottom
    ) {
        books.forEach { book ->
            BookSpine(
                title = book.title,
                variant = book.spineVariant,
                colorIndex = book.colorIndex,
                width = spineWidth,
                height = spineHeight,
                description = stringResource(R.string.cd_spine, book.title)
            )
        }
    }
}

@Composable
private fun MenuSheet(
    state: MenuUiState,
    onStart: () -> Unit,
    onOpenShelf: (String) -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit
) {
    GlassPanel {
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(
                R.string.menu_subtitle,
                state.finishedCount,
                state.goalTarget
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoalRing(
                progress = state.goalProgress,
                percent = state.goalPercent,
                description = stringResource(
                    R.string.cd_goal_ring,
                    state.finishedCount,
                    state.goalTarget,
                    state.goalPercent
                )
            )
            if (state.finishedCount > 0 && state.totalPages > 0) {
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.Star,
                        value = state.finishedCount.toString(),
                        label = stringResource(R.string.stat_books),
                        accent = PrimaryGreen,
                        compact = true
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.CheckCircle,
                        value = state.totalPages.toString(),
                        label = stringResource(R.string.stat_pages),
                        accent = TextPrimary,
                        compact = true
                    )
                }
                if (state.streakDays > 0) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            icon = Icons.Default.DateRange,
                            value = state.streakDays.toString(),
                            label = stringResource(R.string.stat_streak),
                            accent = AccentRed,
                            compact = true
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            label = stringResource(R.string.start_logging),
            onClick = onStart,
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                label = stringResource(R.string.shelf),
                onClick = { onOpenShelf(state.recentFinishedId) },
                icon = Icons.Default.Home,
                enabled = state.recentFinishedId.isNotEmpty(),
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                label = stringResource(R.string.stats),
                onClick = onOpenStats,
                icon = Icons.Default.Info,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.cd_settings),
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun GoalRing(progress: Float, percent: Int, description: String) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(900, easing = FastOutSlowInEasing)
    )
    Box(
        modifier = Modifier
            .size(72.dp)
            .semantics { stateDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = SecondaryTeal,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(PrimaryGreen, PrimaryGreenSoft, PrimaryGreen)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Text(
            text = stringResource(R.string.goal_percent, percent),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
