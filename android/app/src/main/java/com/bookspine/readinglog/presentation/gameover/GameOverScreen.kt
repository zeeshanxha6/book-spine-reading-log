package com.bookspine.readinglog.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.bookspine.readinglog.core.theme.TextPrimary
import com.bookspine.readinglog.core.theme.TextSecondary
import com.bookspine.readinglog.core.ui.BookSpine
import com.bookspine.readinglog.core.ui.PrimaryButton
import com.bookspine.readinglog.core.ui.ScreenBackground
import com.bookspine.readinglog.core.ui.SecondaryButton
import com.bookspine.readinglog.core.ui.StatCard
import com.bookspine.readinglog.core.ui.WoodShelfRail

@Composable
fun GameOverScreen(
    pagesLogged: Int,
    spineEarned: Boolean,
    bookId: String,
    onLogAgain: () -> Unit,
    onMenu: () -> Unit,
    viewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory.instance)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(bookId, pagesLogged, spineEarned) {
        viewModel.bind(bookId, pagesLogged, spineEarned)
    }

    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(bookId) {
        revealed = true
    }

    val transition = rememberInfiniteTransition()
    val shimmer by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val heroScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow)
    )
    val spineDrop by animateFloatAsState(
        targetValue = if (revealed) 0f else -18f,
        animationSpec = tween(220, easing = FastOutSlowInEasing)
    )

    ScreenBackground(
        overlay = listOf(
            BgDeep.copy(alpha = 0.86f),
            BgBase.copy(alpha = 0.94f),
            BgDeep
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(
                    if (state.spineEarned) R.drawable.sprite_5 else R.drawable.sprite_2
                ),
                contentDescription = stringResource(R.string.cd_result_hero),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(if (state.spineEarned) 120.dp else 92.dp)
                    .alpha(if (state.spineEarned) shimmer else 1f)
                    .graphicsLayer {
                        scaleX = heroScale
                        scaleY = heroScale
                    }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(
                    if (state.spineEarned) R.string.result_spine_earned
                    else R.string.result_pages_logged
                ),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (state.spineEarned) {
                    stringResource(R.string.result_on_shelf, state.bookTitle)
                } else {
                    stringResource(R.string.result_keep_going, state.bookTitle)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.CheckCircle,
                        value = stringResource(R.string.result_pages_value, state.pagesLogged),
                        label = stringResource(R.string.stat_pages),
                        accent = PrimaryGreen
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.DateRange,
                        value = state.streakDays.toString(),
                        label = stringResource(R.string.stat_streak),
                        accent = AccentRed
                    )
                }
                if (state.spineEarned) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            icon = Icons.Default.Star,
                            value = stringResource(
                                R.string.result_goal_value,
                                state.finishedCount,
                                state.goalTarget
                            ),
                            label = stringResource(R.string.stat_goal),
                            accent = TextPrimary
                        )
                    }
                }
            }

            if (state.spineEarned && state.bookTitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(22.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BookSpine(
                        title = state.bookTitle,
                        variant = state.spineVariant,
                        colorIndex = state.spineColorIndex,
                        width = 56.dp,
                        height = 104.dp,
                        description = stringResource(R.string.cd_spine, state.bookTitle),
                        modifier = Modifier.graphicsLayer { translationY = spineDrop }
                    )
                    WoodShelfRail(modifier = Modifier.width(140.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                label = stringResource(R.string.log_again),
                onClick = onLogAgain,
                icon = Icons.Default.Refresh,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                label = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
