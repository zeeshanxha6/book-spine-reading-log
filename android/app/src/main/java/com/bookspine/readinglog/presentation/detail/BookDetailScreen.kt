package com.bookspine.readinglog.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.di.AppViewModelFactory
import com.bookspine.readinglog.core.theme.AccentRed
import com.bookspine.readinglog.core.theme.BgBase
import com.bookspine.readinglog.core.theme.BgDeep
import com.bookspine.readinglog.core.theme.PrimaryGreen
import com.bookspine.readinglog.core.theme.SecondaryTeal
import com.bookspine.readinglog.core.theme.TextMuted
import com.bookspine.readinglog.core.theme.TextSecondary
import com.bookspine.readinglog.core.ui.PrimaryButton
import com.bookspine.readinglog.core.ui.ProgressTrack
import com.bookspine.readinglog.core.ui.ScreenBackground
import com.bookspine.readinglog.core.ui.ScreenHeader
import com.bookspine.readinglog.core.ui.SecondaryButton
import com.bookspine.readinglog.core.ui.SectionTitle
import com.bookspine.readinglog.domain.model.Note
import com.bookspine.readinglog.presentation.dialog.QuickNoteDialog

@Composable
fun BookDetailScreen(
    bookId: String,
    onBack: () -> Unit,
    viewModel: BookDetailViewModel = viewModel(factory = AppViewModelFactory.instance)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(bookId) {
        viewModel.bind(bookId)
    }

    ScreenBackground(
        overlay = listOf(
            BgBase.copy(alpha = 0.6f),
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
                title = stringResource(R.string.book_detail),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(26.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                HeroCard(state = state)
                Spacer(modifier = Modifier.height(14.dp))
                SectionTitle(text = stringResource(R.string.notes_and_quotes))
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (state.notes.isEmpty()) {
                EmptyNotes(
                    onAddNote = viewModel::onNoteDialogOpened,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.notes, key = { note -> note.id }) { note ->
                        NoteCard(note = note)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                PrimaryButton(
                    label = stringResource(R.string.add_note),
                    onClick = viewModel::onNoteDialogOpened,
                    icon = Icons.Default.Edit,
                    height = 56.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (state.noteDialogShown) {
        QuickNoteDialog(
            text = state.noteText,
            isQuote = state.noteIsQuote,
            page = state.notePage,
            maxPage = state.totalPages.coerceAtLeast(1),
            invalid = state.noteInvalid,
            onTextChanged = viewModel::onNoteTextChanged,
            onKindChanged = viewModel::onNoteKindChanged,
            onPageChanged = viewModel::onNotePageChanged,
            onConfirm = viewModel::onNoteConfirmed,
            onDismiss = viewModel::onNoteDialogDismissed
        )
    }
}

@Composable
private fun HeroCard(state: BookDetailUiState) {
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
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = state.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 80.dp, height = 110.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = state.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (state.finished) {
                        AccentRed.copy(alpha = 0.22f)
                    } else {
                        PrimaryGreen.copy(alpha = 0.22f)
                    }
                ) {
                    Text(
                        text = stringResource(
                            if (state.finished) R.string.status_finished
                            else R.string.status_reading
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.finished) AccentRed else PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                ProgressTrack(progress = state.progress, height = 8.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.detail_meta,
                        state.totalPages,
                        state.pagesRead
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun EmptyNotes(onAddNote: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(width = 40.dp, height = 90.dp)
                .alpha(0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.notes_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        SecondaryButton(
            label = stringResource(R.string.add_note),
            onClick = onAddNote,
            icon = Icons.Default.Add
        )
    }
}

@Composable
private fun NoteCard(note: Note) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SecondaryTeal),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(if (note.isQuote) AccentRed else PrimaryGreen)
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = note.text,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        if (note.isQuote) R.string.note_kind_quote
                        else R.string.note_kind_note,
                        note.page
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}
