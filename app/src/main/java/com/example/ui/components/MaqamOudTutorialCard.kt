package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.OpenInFull
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OudCatalog
import com.example.data.OudFingerNote
import com.example.data.OudMaqamGuide
import com.example.data.OudTuning
import com.example.model.Maqam
import com.example.ui.theme.QuarterToneAccent
import com.example.viewmodel.PlaybackUiState

@Composable
fun MaqamOudTutorialCard(
    maqam: Maqam,
    tuning: OudTuning,
    lastOudNote: OudFingerNote?,
    pulseTrigger: Long,
    externalCourse: Int?,
    externalPressedQt: Int?,
    playbackState: PlaybackUiState,
    onSelectTuning: (OudTuning) -> Unit,
    onPluckCourse: (Int, Int, Boolean, Float, Boolean) -> Unit,
    onPlayGuideSequence: (OudMaqamGuide) -> Unit,
    onOpenFullOud: () -> Unit,
    modifier: Modifier = Modifier
) {
    val guide = remember(maqam.id, tuning) {
        OudCatalog.buildMaqamOudGuide(maqam, tuning)
    }
    val tutorialTag = "detail_oud_${maqam.id}"
    val isPlayingTutorial = playbackState.isPlaying && playbackState.sourceTag == tutorialTag
    val activeStepIdx = if (isPlayingTutorial) playbackState.activeNoteIndex else -1
    val activeStep = if (activeStepIdx in guide.steps.indices) guide.steps[activeStepIdx] else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("maqam_oud_tutorial_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "طريقة عزف ${maqam.name} على العود",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "مواضع الأوتار والأصابع والريشة خطوة بخطوة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenFullOud,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("detail_open_in_oud")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInFull,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("شاشة العود", style = MaterialTheme.typography.labelMedium)
                }
            }

            // Tuning selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OudTuning.entries.forEach { item ->
                    FilterChip(
                        selected = item == tuning,
                        onClick = { onSelectTuning(item) },
                        label = { Text(item.arabicName) },
                        modifier = Modifier.testTag("detail_oud_tuning_${item.name.lowercase()}")
                    )
                }
            }

            // Explanation & Risha Technique
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "خطة العزف على الأوتار:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = guide.summaryTip,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "أسلوب الريشة: ${guide.rishaTip}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Live Coaching Banner
            Surface(
                color = if (activeStep?.oudNote?.isQuarterTone == true || lastOudNote?.isQuarterTone == true) {
                    QuarterToneAccent.copy(alpha = 0.16f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val coachingText = when {
                    activeStep != null ->
                        "الخطوة ${activeStep.stepNumber}: ${activeStep.oudNote.arabicName} • ${activeStep.oudNote.maqamPitchName} (${activeStep.oudNote.westernName})  |  ${activeStep.course.shortArabicName} • ${activeStep.fingerName}"
                    lastOudNote != null ->
                        "النغمة: ${lastOudNote.arabicName} • ${lastOudNote.maqamPitchName} (${lastOudNote.westernName})  |  وتر ${tuning.courses[lastOudNote.courseIndex].shortArabicName}"
                    else ->
                        "اضغط «عزف وشرح الخطوات» أو انقر على أي وتر أو خطوة بالأسفل"
                }
                Text(
                    text = coachingText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            // Embedded Interactive 2D Oud Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(4.dp)
            ) {
                Oud2DCanvas(
                    tuning = tuning,
                    guideMaqam = maqam,
                    showMaqamGuides = true,
                    externalPulseTrigger = pulseTrigger,
                    externalPluckedCourse = externalCourse,
                    externalPressedQt = externalPressedQt,
                    onPluckCourse = { courseIdx, qt, posRatio, isUp ->
                        onPluckCourse(courseIdx, qt, false, posRatio, isUp)
                    }
                )
            }

            // Aligned 6 Open-String Note Reference & Pluck Bar (Arabic + Solfege + Western)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tuning.courses.forEachIndexed { idx, course ->
                        val isLastCourse = lastOudNote?.courseIndex == idx
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onPluckCourse(idx, 0, true, 0.21f, false) }
                                .testTag("detail_oud_open_string_${course.courseNumber}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isLastCourse) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            },
                            border = BorderStroke(
                                width = if (isLastCourse) 1.4.dp else 0.8.dp,
                                color = if (isLastCourse) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = course.shortArabicName,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    text = course.openSolfegeName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    text = course.openWesternName,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Guided Step-by-Step Playback Button
            Button(
                onClick = { onPlayGuideSequence(guide) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlayingTutorial) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("detail_play_oud_tutorial")
            ) {
                Icon(
                    imageVector = if (isPlayingTutorial) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPlayingTutorial) "إيقاف شرح العود" else "عزف وشرح الخطوات على العود"
                )
            }

            // Step-by-Step Fingering Cards
            Text(
                text = "خطوات الأصابع والأوتار (اضغط على أي خطوة لسماعها):",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                guide.steps.forEachIndexed { idx, step ->
                    val isCurrent = idx == activeStepIdx
                    val isQt = step.oudNote.isQuarterTone
                    Card(
                        modifier = Modifier
                            .width(126.dp)
                            .clickable {
                                onPluckCourse(step.courseIndex, step.quarterToneSteps, true, 0.21f, false)
                            }
                            .testTag("oud_step_card_$idx"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                isQt -> QuarterToneAccent.copy(alpha = 0.14f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            }
                        ),
                        border = BorderStroke(
                            width = if (isCurrent || idx == 0) 1.6.dp else 1.dp,
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isQt -> QuarterToneAccent
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "خطوة ${step.stepNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${step.oudNote.arabicName} (${step.oudNote.westernName})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isQt) QuarterToneAccent else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                text = step.oudNote.maqamPitchName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                text = "الوتر ${step.course.courseNumber}: ${step.course.shortArabicName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                text = step.fingerName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                text = step.positionLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
