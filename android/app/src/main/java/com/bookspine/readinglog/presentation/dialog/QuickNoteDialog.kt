package com.bookspine.readinglog.presentation.dialog

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.theme.AccentRed
import com.bookspine.readinglog.core.theme.PrimaryGreen
import com.bookspine.readinglog.core.theme.SecondaryTeal
import com.bookspine.readinglog.core.theme.SurfaceAlt
import com.bookspine.readinglog.core.ui.BigStepper
import com.bookspine.readinglog.core.ui.SectionTitle

@Composable
fun QuickNoteDialog(
    text: String,
    isQuote: Boolean,
    page: Int,
    maxPage: Int,
    invalid: Boolean,
    onTextChanged: (String) -> Unit,
    onKindChanged: (Boolean) -> Unit,
    onPageChanged: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = stringResource(R.string.add_note),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    isError = invalid,
                    minLines = 3,
                    label = {
                        Text(
                            text = stringResource(R.string.field_note),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (invalid) {
                    Text(
                        text = stringResource(R.string.error_note_required),
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentRed
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KindChip(
                        label = stringResource(R.string.kind_quote),
                        selected = isQuote,
                        onSelected = { onKindChanged(true) },
                        modifier = Modifier.weight(1f)
                    )
                    KindChip(
                        label = stringResource(R.string.kind_note),
                        selected = !isQuote,
                        onSelected = { onKindChanged(false) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                SectionTitle(text = stringResource(R.string.field_page))
                Spacer(modifier = Modifier.height(6.dp))
                BigStepper(
                    value = page,
                    minValue = 1,
                    maxValue = maxPage,
                    decreaseDescription = stringResource(R.string.cd_decrease),
                    increaseDescription = stringResource(R.string.cd_increase),
                    onChange = onPageChanged,
                    valueFontSize = 34.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryGreen
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
private fun KindChip(
    label: String,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectionLabel = if (selected) {
        stringResource(R.string.state_selected)
    } else {
        stringResource(R.string.state_not_selected)
    }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) SurfaceAlt else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) AccentRed else SecondaryTeal
        ),
        modifier = modifier
            .height(48.dp)
            .semantics { stateDescription = selectionLabel }
            .clickable(onClick = onSelected)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
