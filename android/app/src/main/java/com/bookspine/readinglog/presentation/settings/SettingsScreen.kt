package com.bookspine.readinglog.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookspine.readinglog.R
import com.bookspine.readinglog.core.config.GameConfig
import com.bookspine.readinglog.core.di.AppViewModelFactory
import com.bookspine.readinglog.core.theme.AccentRed
import com.bookspine.readinglog.core.theme.BgBase
import com.bookspine.readinglog.core.theme.BgDeep
import com.bookspine.readinglog.core.theme.ParchmentBg
import com.bookspine.readinglog.core.theme.PrimaryGreen
import com.bookspine.readinglog.core.theme.SecondaryTeal
import com.bookspine.readinglog.core.theme.TextMuted
import com.bookspine.readinglog.core.theme.TextSecondary
import com.bookspine.readinglog.core.ui.BigStepper
import com.bookspine.readinglog.core.ui.ScreenBackground
import com.bookspine.readinglog.core.ui.ScreenHeader
import com.bookspine.readinglog.core.ui.SecondaryButton
import com.bookspine.readinglog.core.ui.SectionTitle
import com.bookspine.readinglog.domain.model.ThemeMode
import com.bookspine.readinglog.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.instance)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    ScreenBackground(
        overlay = listOf(
            BgBase.copy(alpha = 0.6f),
            BgBase.copy(alpha = 0.9f),
            BgDeep
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.settings),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    SectionTitle(text = stringResource(R.string.theme))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThemeTile(
                        label = stringResource(R.string.theme_forest),
                        swatches = listOf(BgBase, PrimaryGreen, AccentRed),
                        selected = state.themeMode == ThemeMode.FOREST,
                        onSelected = { viewModel.onThemeSelected(ThemeMode.FOREST) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeTile(
                        label = stringResource(R.string.theme_parchment),
                        swatches = listOf(ParchmentBg, PrimaryGreen, AccentRed),
                        selected = state.themeMode == ThemeMode.PARCHMENT,
                        onSelected = { viewModel.onThemeSelected(ThemeMode.PARCHMENT) },
                        modifier = Modifier.weight(1f)
                    )
                }

                SectionTitle(text = stringResource(R.string.yearly_goal))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val quickLabels = GOAL_STEPS.associateWith { step ->
                            stringResource(R.string.quick_step, step)
                        }
                        BigStepper(
                            value = state.goalTarget,
                            minValue = GameConfig.MIN_GOAL,
                            maxValue = GameConfig.MAX_GOAL,
                            decreaseDescription = stringResource(R.string.cd_decrease),
                            increaseDescription = stringResource(R.string.cd_increase),
                            onChange = viewModel::onGoalChanged,
                            quickSteps = GOAL_STEPS,
                            quickLabel = { step -> quickLabels[step].orEmpty() },
                            valueFontSize = 44.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.yearly_goal_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

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
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.haptics),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = state.hapticsEnabled,
                            onCheckedChange = viewModel::onHapticsChanged,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = PrimaryGreen,
                                checkedThumbColor = Color.White,
                                uncheckedTrackColor = SecondaryTeal
                            )
                        )
                    }
                }

                SecondaryButton(
                    label = stringResource(R.string.reset_data),
                    onClick = viewModel::onResetRequested,
                    icon = Icons.Default.Delete,
                    accent = AccentRed,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.settings_footer),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (state.resetDialogShown) {
        ConfirmResetDialog(
            onConfirm = viewModel::onResetConfirmed,
            onDismiss = viewModel::onResetDismissed
        )
    }
}

@Composable
private fun ThemeTile(
    label: String,
    swatches: List<Color>,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectionLabel = if (selected) {
        stringResource(R.string.state_selected)
    } else {
        stringResource(R.string.state_not_selected)
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) AccentRed else SecondaryTeal
        ),
        modifier = modifier
            .height(88.dp)
            .semantics { stateDescription = selectionLabel }
            .clickable(onClick = onSelected)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                swatches.forEach { swatch ->
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(swatch, CircleShape)
                    )
                }
            }
            Text(
                text = stringResource(R.string.theme_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

private val GOAL_STEPS = listOf(5, 10)
