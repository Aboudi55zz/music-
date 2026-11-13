package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OudCatalog
import com.example.data.OudFingerNote
import com.example.data.OudTuning
import com.example.model.Maqam
import com.example.ui.components.Oud2DCanvas
import com.example.ui.theme.QuarterToneAccent
import com.example.viewmodel.PlaybackUiState
import kotlin.math.roundToInt

@Composable
fun OudScreen(
    allMaqamat: List<Maqam>,
    tuning: OudTuning,
    guideMaqam: Maqam?,
    showGuides: Boolean,
    lastNote: OudFingerNote?,
    pulseTrigger: Long,
    externalCourse: Int?,
    externalPressedQt: Int?,
    playbackState: PlaybackUiState,
    onSelectTuning: (OudTuning) -> Unit,
    onSelectGuideMaqam: (Maqam?) -> Unit,
    onToggleGuides: () -> Unit,
    onPluckCourse: (Int, Int, Boolean, Float, Boolean) -> Unit,
    onStrumAll: () -> Unit,
    onPlayMaqamOnOud: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlayingMaqam = guideMaqam != null &&
        playbackState.isPlaying &&
        playbackState.sourceTag == "oud_scale_${guideMaqam.id}"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("oud_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Header & Live Note HUD
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "العود العربي (٦ مسارات • ١١ وتراً)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${tuning.arabicName}: ${tuning.shortSummary}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Live Note Readout Badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (lastNote?.isQuarterTone == true) {
                    QuarterToneAccent.copy(alpha = 0.16f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                },
                border = BorderStroke(
                    1.dp,
                    if (lastNote?.isQuarterTone == true) QuarterToneAccent else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("oud_note_hud")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lastNote?.let {
                            "${it.arabicName} • ${it.maqamPitchName} (${it.westernName})"
                        } ?: "اضغط وتراً للعزف",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (lastNote?.isQuarterTone == true) QuarterToneAccent else MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = lastNote?.let {
                            val course = tuning.courses[it.courseIndex]
                            "الوتر ${course.courseNumber}: ${course.shortArabicName} • ${it.frequencyHz.roundToInt()} Hz"
                        } ?: "عربي • صولفيج • أجنبي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 2. Tuning Selector + Guide Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OudTuning.entries.forEach { item ->
                FilterChip(
                    selected = item == tuning,
                    onClick = { onSelectTuning(item) },
                    label = { Text(item.arabicName) },
                    modifier = Modifier.testTag("oud_tuning_${item.name.lowercase()}")
                )
            }

            FilterChip(
                selected = showGuides,
                onClick = onToggleGuides,
                label = { Text("دليل المقام") },
                leadingIcon = {
                    Icon(
                        imageVector = if (showGuides) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("oud_toggle_guides")
            )
        }

        // 3. Maqam Fingerboard Guide Picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = guideMaqam == null,
                onClick = { onSelectGuideMaqam(null) },
                label = { Text("عزف حر") },
                modifier = Modifier.testTag("oud_guide_none")
            )
            allMaqamat.forEach { maqam ->
                val selected = guideMaqam?.id == maqam.id
                FilterChip(
                    selected = selected,
                    onClick = { onSelectGuideMaqam(maqam) },
                    label = { Text(maqam.name.removePrefix("مقام ")) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("oud_guide_${maqam.id}")
                )
            }
        }

        // 4. Main 2D Interactive Oud Stage
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(470.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Oud2DCanvas(
                    tuning = tuning,
                    guideMaqam = guideMaqam,
                    showMaqamGuides = showGuides,
                    externalPulseTrigger = pulseTrigger,
                    externalPluckedCourse = externalCourse,
                    externalPressedQt = externalPressedQt,
                    onPluckCourse = { courseIdx, qt, posRatio, isUp ->
                        onPluckCourse(courseIdx, qt, false, posRatio, isUp)
                    }
                )

                // Legend overlay in corner if guides are visible
                if (showGuides && guideMaqam != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            LegendRow(Color(0xFFE2B052), "الركوز")
                            LegendRow(Color(0xFF4FD1CD), "ربع تون 𝄳")
                        }
                    }
                }
            }
        }

        // 5. Six Open-Course String Buttons & Real Musical Intervals Between Them (Aligned LTR)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until tuning.courses.lastIndex) {
                        val intervalText = OudCatalog.intervalLabelBetween(
                            tuning.courses[i],
                            tuning.courses[i + 1]
                        ).substringBefore(" ")
                        Text(
                            text = intervalText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    tuning.courses.forEachIndexed { idx, course ->
                        val isLastCourse = lastNote?.courseIndex == idx
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onPluckCourse(idx, 0, true, 0.21f, false) }
                                .testTag("oud_open_string_${course.courseNumber}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLastCourse) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                width = if (isLastCourse) 1.5.dp else 1.dp,
                                color = if (isLastCourse) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                Text(
                                    text = course.shortArabicName,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    text = course.openSolfegeName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    text = course.openWesternName,
                                    fontSize = 10.sp,
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
        }

        // 6. Action Buttons (Strum All / Play Guide Maqam Scale)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onStrumAll,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .testTag("oud_strum_all")
            ) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("ضربة ريشة شاملة", style = MaterialTheme.typography.labelLarge)
            }

            if (guideMaqam != null) {
                Button(
                    onClick = onPlayMaqamOnOud,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlayingMaqam) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("oud_play_guide_maqam")
                ) {
                    Icon(
                        imageVector = if (isPlayingMaqam) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlayingMaqam) "إيقاف" else "عزف ${guideMaqam.name.removePrefix("مقام ")}",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }
            }
        }

        // Helpful playing tip
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "تلميح: اضغط على الرقبة (الزند) لعزف النغمات والربع تون، أو اسحب إصبعك أفقياً فوق الشمسيات للضرب بالريشة؛ ويمكنك الضغط بإصبع على الرقبة والضرب بالآخر!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp
        )
    }
}
