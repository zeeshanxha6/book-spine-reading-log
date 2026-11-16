package com.bookspine.readinglog.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.bookspine.readinglog.core.theme.SurfaceAlt
import com.bookspine.readinglog.core.theme.TextMuted
import com.bookspine.readinglog.core.theme.TextPrimary
import com.bookspine.readinglog.core.theme.TextSecondary
import com.bookspine.readinglog.core.ui.BigStepper
import com.bookspine.readinglog.core.ui.GlassPanel
import com.bookspine.readinglog.core.ui.PrimaryButton
import com.bookspine.readinglog.core.ui.ProgressTrack
import com.bookspine.readinglog.core.ui.ScreenBackground
import com.bookspine.readinglog.core.ui.ScreenHeader
import com.bookspine.readinglog.core.ui.SecondaryButton
import com.bookspine.readinglog.core.ui.SectionTitle
import com.bookspine.readinglog.core.ui.StatCard
import com.bookspine.readinglog.domain.model.Book
import com.bookspine.readinglog.presentation.dialog.AddBookDialog

@Composable
fun GameScreen(
    onBack: () -> Unit,
    onRoundFinished: (Int, Boolean, String) -> Unit,
    viewModel: GameViewModel = viewModel(factory = AppViewModelFactory.instance)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { outcome ->
            onRoundFinished(outcome.pagesLogged, outcome.spineEarned, outcome.bookId)
        }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }

    GameContent(
        state = state,
        onBack = onBack,
        onBookSelected = viewModel::onBookSelected,
        onPendingChanged = viewModel::onPendingChanged,
        onCommit = viewModel::onCommitTapped,
        onFinishBook = viewModel::onFinishBookTapped,
        onAddBookOpened = viewModel::onAddBookOpened,
        onAddBookDismissed = viewModel::onAddBookDismissed,
        onAddTitleChanged = viewModel::onAddTitleChanged,
        onAddAuthorChanged = viewModel::onAddAuthorChanged,
        onAddPagesChanged = viewModel::onAddPagesChanged,
        onAddBookConfirmed = viewModel::onAddBookConfirmed
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onBack: () -> Unit,
    onBookSelected: (String) -> Unit,
    onPendingChanged: (Int) -> Unit,
    onCommit: () -> Unit,
    onFinishBook: () -> Unit,
    onAddBookOpened: () -> Unit,
    onAddBookDismissed: () -> Unit,
    onAddTitleChanged: (String) -> Unit,
    onAddAuthorChanged: (String) -> Unit,
    onAddPagesChanged: (Int) -> Unit,
    onAddBookConfirmed: () -> Unit
) {
    val book = state.selectedBook
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(state.confirmShown) {
        if (state.confirmShown && state.hapticsEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    ScreenBackground(
        overlay = listOf(
            BgBase.copy(alpha = 0.55f),
            BgBase.copy(alpha = 0.84f),
            BgDeep
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.log_pages),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SecondaryTeal.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = stringResource(
                            R.string.page_badge,
                            book?.pagesRead ?: 0,
                            book?.totalPages ?: 0
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryGreenSoft,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.libraryEmpty || book == null) {
                    EmptyLibrary(onAddBookOpened = onAddBookOpened)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.icon_pause),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        SectionTitle(text = stringResource(R.string.currently_reading))
                    }
                    PickerRow(
                        books = state.activeBooks.take(GameConfig.MAX_PICKER_BOOKS),
                        selectedId = state.selectedBookId,
                        onBookSelected = onBookSelected
                    )
                    ActiveBookCard(book = book)
                    Spacer(modifier = Modifier.height(2.dp))
                    val quickLabels = GameConfig.QUICK_STEPS.associateWith { step ->
                        stringResource(R.string.quick_step, step)
                    }
                    BigStepper(
                        value = state.pending,
                        minValue = 0,
                        maxValue = state.maxPending,
                        decreaseDescription = stringResource(R.string.cd_decrease),
                        increaseDescription = stringResource(R.string.cd_increase),
                        onChange = onPendingChanged,
                        quickSteps = GameConfig.QUICK_STEPS,
                        quickLabel = { step -> quickLabels[step].orEmpty() }
                    )
                }
            }

            BottomPanel(
                state = state,
                onCommit = onCommit,
                onFinishBook = onFinishBook,
                onAddBookOpened = onAddBookOpened
            )
        }

        AnimatedVisibility(
            visible = state.confirmShown,
            enter = scaleIn(initialScale = 0.6f) + fadeIn(tween(180)),
            exit = fadeOut(tween(180)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.cd_saved),
                tint = PrimaryGreenSoft,
                modifier = Modifier.size(36.dp)
            )
        }
    }

    if (state.addBookShown) {
        AddBookDialog(
            title = state.addTitle,
            author = state.addAuthor,
            pages = state.addPages,
            invalid = state.addInvalid,
            onTitleChanged = onAddTitleChanged,
            onAuthorChanged = onAddAuthorChanged,
            onPagesChanged = onAddPagesChanged,
            onConfirm = onAddBookConfirmed,
            onDismiss = onAddBookDismissed
        )
    }
}

@Composable
private fun EmptyLibrary(onAddBookOpened: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = stringResource(R.string.cd_empty_library),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(110.dp)
                .alpha(0.6f)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.library_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(
            label = stringResource(R.string.add_first_book),
            onClick = onAddBookOpened,
            icon = Icons.Default.Add
        )
    }
}

@Composable
private fun PickerRow(
    books: List<Book>,
    selectedId: String,
    onBookSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        books.forEach { item ->
            val selected = item.id == selectedId
            val selectionLabel = if (selected) {
                stringResource(R.string.state_selected)
            } else {
                stringResource(R.string.state_not_selected)
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) SurfaceAlt else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) AccentRed else SecondaryTeal
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(66.dp)
                    .semantics { stateDescription = selectionLabel }
                    .clickable { onBookSelected(item.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(width = 24.dp, height = 34.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ProgressTrack(progress = item.progress, height = 3.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveBookCard(book: Book) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = book.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 64.dp, height = 88.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProgressTrack(progress = book.progress)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.page_of, book.pagesRead, book.totalPages),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun BottomPanel(
    state: GameUiState,
    onCommit: () -> Unit,
    onFinishBook: () -> Unit,
    onAddBookOpened: () -> Unit
) {
    GlassPanel(topRadius = 24.dp, horizontalPadding = 16.dp, verticalPadding = 14.dp) {
        val visibleStats = listOf(
            state.todayPages > 0,
            state.streakDays > 0,
            state.remaining > 0
        ).count { it }
        if (visibleStats >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.todayPages > 0) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            icon = Icons.Default.CheckCircle,
                            value = state.todayPages.toString(),
                            label = stringResource(R.string.stat_today),
                            accent = PrimaryGreen,
                            compact = true
                        )
                    }
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
                if (state.remaining > 0) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            icon = Icons.Default.Star,
                            value = state.remaining.toString(),
                            label = stringResource(R.string.stat_left),
                            accent = TextPrimary,
                            compact = true
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.saving) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
        } else {
            PrimaryButton(
                label = stringResource(R.string.save_pages),
                onClick = onCommit,
                icon = Icons.Default.Check,
                enabled = state.canCommit,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (state.libraryEmpty) {
            SecondaryButton(
                label = stringResource(R.string.add_book),
                onClick = onAddBookOpened,
                icon = Icons.Default.Add,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            SecondaryButton(
                label = stringResource(R.string.finish_book),
                onClick = onFinishBook,
                icon = Icons.Default.CheckCircle,
                enabled = state.canFinishBook,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
