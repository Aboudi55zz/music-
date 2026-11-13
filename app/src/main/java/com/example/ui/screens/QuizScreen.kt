package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.InstrumentTimbre
import com.example.model.MusicalNote
import com.example.ui.components.InstrumentSelectorBar
import com.example.ui.components.MusicalStaffView
import com.example.viewmodel.PlaybackUiState
import com.example.viewmodel.QuizDifficulty
import com.example.viewmodel.QuizQuestion

@Composable
fun QuizScreen(
    question: QuizQuestion?,
    difficulty: QuizDifficulty,
    selectedAnswerId: String?,
    currentStreak: Int,
    bestStreak: Int,
    totalCorrect: Int,
    selectedInstrument: InstrumentTimbre,
    playbackState: PlaybackUiState,
    onSelectDifficulty: (QuizDifficulty) -> Unit,
    onSelectInstrument: (InstrumentTimbre) -> Unit,
    onPlayQuestion: () -> Unit,
    onSubmitAnswer: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onOpenMaqamDetail: (String) -> Unit,
    onPlayNote: (MusicalNote) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlayingQuestion = playbackState.isPlaying && playbackState.sourceTag == "quiz_audio"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Stats
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Hearing,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تحدي تمييز المقامات بالسمع",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuizStatBadge(
                        icon = Icons.Rounded.LocalFireDepartment,
                        label = "السلسلة",
                        value = currentStreak.toString(),
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    QuizStatBadge(
                        icon = Icons.Rounded.EmojiEvents,
                        label = "أفضل سلسلة",
                        value = bestStreak.toString(),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    QuizStatBadge(
                        icon = Icons.Rounded.CheckCircle,
                        label = "إجابات صحيحة",
                        value = totalCorrect.toString(),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuizDifficulty.entries.forEach { diff ->
                        FilterChip(
                            selected = difficulty == diff,
                            onClick = { onSelectDifficulty(diff) },
                            label = { Text(diff.label) },
                            modifier = Modifier.testTag("quiz_diff_${diff.name.lowercase()}")
                        )
                    }
                }
            }
        }

        if (question != null) {
            // Audio Prompt Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.GraphicEq,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = if (question.playMelodyInsteadOfScale) {
                                "استمع للجملة اللحنية وحدد المقام"
                            } else {
                                "استمع للسلم الموسيقي وحدد المقام"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )

                        InstrumentSelectorBar(
                            selectedInstrument = selectedInstrument,
                            onSelectInstrument = onSelectInstrument
                        )

                        Button(
                            onClick = onPlayQuestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("quiz_play_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingQuestion) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (isPlayingQuestion) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlayingQuestion) "إيقاف الصوت" else "تشغيل المقام المجهول"
                            )
                        }
                    }
                }
            }

            // 4 Answer Options
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    question.options.forEach { option ->
                        val isRevealed = selectedAnswerId != null
                        val isTarget = option.id == question.targetMaqam.id
                        val isChosen = option.id == selectedAnswerId

                        val containerColor = when {
                            !isRevealed -> MaterialTheme.colorScheme.surface
                            isTarget -> Color(0xFF1D6F6E).copy(alpha = 0.22f)
                            isChosen && !isTarget -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                        }
                        val borderColor = when {
                            !isRevealed -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            isTarget -> Color(0xFF1D6F6E)
                            isChosen && !isTarget -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isRevealed) { onSubmitAnswer(option.id) }
                                .testTag("quiz_option_${option.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            border = BorderStroke(if (isRevealed && (isTarget || isChosen)) 2.dp else 1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = option.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${option.family.arabicName} • الركوز: ${option.tonicName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isRevealed && isTarget) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "إجابة صحيحة",
                                        tint = Color(0xFF1D6F6E)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Answer Reveal + Staff + Next Question
            if (selectedAnswerId != null) {
                val isCorrect = selectedAnswerId == question.targetMaqam.id
                val target = question.targetMaqam
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.5.dp,
                            if (isCorrect) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "أحسنت! الإجابة صحيحة: ${target.name}" else "الإجابة الصحيحة هي: ${target.name}",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isCorrect) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = target.summary,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            MusicalStaffView(
                                notes = target.ascendingScale,
                                activeNoteIndex = if (isPlayingQuestion && !question.playMelodyInsteadOfScale) playbackState.activeNoteIndex else -1,
                                tonicNoteId = target.ascendingScale.firstOrNull()?.id,
                                onNoteClick = onPlayNote
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenMaqamDetail(target.id) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("quiz_open_detail")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("شرح المقام")
                                }

                                Button(
                                    onClick = onNextQuestion,
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("quiz_next_button")
                                ) {
                                    Text("السؤال التالي")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.NavigateNext,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizStatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
