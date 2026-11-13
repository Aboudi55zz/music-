package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.InstrumentTimbre
import com.example.model.Maqam
import com.example.model.MusicalNote
import com.example.ui.components.InstrumentSelectorBar
import com.example.ui.components.MusicalStaffView
import com.example.ui.components.QuarterToneKeyboard
import com.example.viewmodel.PlaybackUiState

@Composable
fun KeyboardStudioScreen(
    allMaqamat: List<Maqam>,
    selectedMaqam: Maqam,
    selectedInstrument: InstrumentTimbre,
    activeNoteId: String?,
    playbackState: PlaybackUiState,
    onSelectMaqam: (Maqam) -> Unit,
    onSelectInstrument: (InstrumentTimbre) -> Unit,
    onNoteTap: (MusicalNote) -> Unit,
    onPlayScale: () -> Unit,
    onPlayMelody: () -> Unit,
    onToggleDrone: (MusicalNote) -> Unit,
    modifier: Modifier = Modifier
) {
    val tonicNote = selectedMaqam.ascendingScale.first()
    val highlightedIds = remember(selectedMaqam.id) {
        (selectedMaqam.ascendingScale + selectedMaqam.descendingScale).map { it.id }.toSet()
    }
    val isPlayingScale = playbackState.isPlaying && playbackState.sourceTag == "studio_scale_${selectedMaqam.id}"
    val isPlayingMelody = playbackState.isPlaying && playbackState.sourceTag == "studio_melody_${selectedMaqam.id}"

    val activeStaffIndex = when {
        isPlayingScale -> playbackState.activeNoteIndex
        else -> -1
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("keyboard_studio_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Piano,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "استوديو العزف بربع التون",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Text(
                    text = "اختر أي مقام لإضاءة نغماته ومفاتيح الربع تون (𝄳) على البيانو، واعزف بحرية أو مع قرار مستمر.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Maqam Selector Chips
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المقام المختار: ${selectedMaqam.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = Color(selectedMaqam.family.accentHex).copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = selectedMaqam.family.arabicName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(selectedMaqam.family.accentHex),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allMaqamat.forEach { maqam ->
                            val selected = maqam.id == selectedMaqam.id
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectMaqam(maqam) },
                                label = { Text(maqam.name.removePrefix("مقام ")) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("studio_maqam_chip_${maqam.id}")
                            )
                        }
                    }

                    InstrumentSelectorBar(
                        selectedInstrument = selectedInstrument,
                        onSelectInstrument = onSelectInstrument
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onPlayScale,
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("studio_play_scale"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingScale) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (isPlayingScale) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPlayingScale) "إيقاف" else "السلم")
                        }

                        OutlinedButton(
                            onClick = onPlayMelody,
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("studio_play_melody")
                        ) {
                            Icon(
                                imageVector = if (isPlayingMelody) Icons.Rounded.Stop else Icons.Rounded.GraphicEq,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPlayingMelody) "إيقاف" else "تقسيمة")
                        }

                        OutlinedButton(
                            onClick = { onToggleDrone(tonicNote) },
                            border = BorderStroke(
                                1.dp,
                                if (playbackState.isDroneActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("studio_drone_toggle")
                        ) {
                            Icon(
                                imageVector = if (playbackState.isDroneActive) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                                contentDescription = null,
                                tint = if (playbackState.isDroneActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "قرار",
                                color = if (playbackState.isDroneActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Playable 2-Octave Quarter-Tone Keyboard
        item {
            QuarterToneKeyboard(
                highlightedNoteIds = highlightedIds,
                tonicNoteId = tonicNote.id,
                activeNoteId = activeNoteId,
                onNoteTap = onNoteTap,
                compactMode = false
            )
        }

        // Synchronized Musical Staff for the Selected Maqam
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "النوتة المرجعية لـ ${selectedMaqam.name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                MusicalStaffView(
                    notes = selectedMaqam.ascendingScale,
                    activeNoteIndex = activeStaffIndex,
                    tonicNoteId = tonicNote.id,
                    onNoteClick = onNoteTap
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
