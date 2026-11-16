package com.bookspine.readinglog.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.core.theme.AccentRed
import com.bookspine.readinglog.core.theme.PrimaryGreen
import com.bookspine.readinglog.core.ui.BigStepper
import com.bookspine.readinglog.core.ui.SectionTitle

@Composable
fun AddBookDialog(
    title: String,
    author: String,
    pages: Int,
    invalid: Boolean,
    onTitleChanged: (String) -> Unit,
    onAuthorChanged: (String) -> Unit,
    onPagesChanged: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(52.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.add_book),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChanged,
                    singleLine = true,
                    isError = invalid,
                    label = {
                        Text(
                            text = stringResource(R.string.field_title),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (invalid) {
                    Text(
                        text = stringResource(R.string.error_title_required),
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentRed
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = onAuthorChanged,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.field_author),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                SectionTitle(text = stringResource(R.string.field_pages))
                Spacer(modifier = Modifier.height(8.dp))
                val quickLabels = PAGE_STEPS.associateWith { step ->
                    stringResource(R.string.quick_step, step)
                }
                BigStepper(
                    value = pages,
                    minValue = GameConfig.MIN_BOOK_PAGES,
                    maxValue = GameConfig.MAX_BOOK_PAGES,
                    decreaseDescription = stringResource(R.string.cd_decrease),
                    increaseDescription = stringResource(R.string.cd_increase),
                    onChange = onPagesChanged,
                    quickSteps = PAGE_STEPS,
                    quickLabel = { step -> quickLabels[step].orEmpty() },
                    valueFontSize = 36.sp
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

private val PAGE_STEPS = listOf(10, 50, 100)
