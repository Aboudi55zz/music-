package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.NoteCatalog
import com.example.model.MusicalNote
import com.example.model.TheoryTopic
import com.example.ui.components.MusicalStaffView
import com.example.ui.theme.QuarterToneAccent
import com.example.viewmodel.PlaybackUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TheoryScreen(
    topics: List<TheoryTopic>,
    playbackState: PlaybackUiState,
    onPlayNote: (MusicalNote) -> Unit,
    onPlayDemo: (List<MusicalNote>, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("theory_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoStories,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "قواعد ونظريات المقامات",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Text(
                    text = "دليل مبسط لفهم الربع تون، الأجناس، أسماء الدرجات التراثية، وكيفية بناء السلم العربي مع أمثلة مسموعة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive Traditional Degree Chart
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "خريطة الدرجات التراثية التفاعلية",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "اضغط على أي درجة لسماع ترددها الصوتي الصحيح ومعرفة اسمها العربي والغربي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NoteCatalog.traditionalDegreeChart.forEach { note ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (note.isQuarterTone) {
                                    QuarterToneAccent.copy(alpha = 0.14f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (note.isQuarterTone) QuarterToneAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .clickable { onPlayNote(note) }
                                    .testTag("theory_degree_${note.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = note.maqamPitchName,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (note.isQuarterTone) QuarterToneAccent else MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${note.arabicName} (${note.westernName})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Theory Topics with Musical Staff Demos
        items(topics, key = { it.id }) { topic ->
            val tag = "theory_${topic.id}"
            val isPlayingThis = playbackState.isPlaying && playbackState.sourceTag == tag
            val activeIndex = if (isPlayingThis) playbackState.activeNoteIndex else -1

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = topic.subtitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = topic.body,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        topic.keyPoints.forEach { point ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(top = 3.dp)
                                        .size(16.dp)
                                )
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    if (topic.demoNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        topic.demoTitle?.let { demoTitle ->
                            Text(
                                text = demoTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        MusicalStaffView(
                            notes = topic.demoNotes,
                            activeNoteIndex = activeIndex,
                            tonicNoteId = topic.demoNotes.firstOrNull()?.id,
                            onNoteClick = onPlayNote
                        )
                        Button(
                            onClick = { onPlayDemo(topic.demoNotes, tag) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingThis) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("theory_play_${topic.id}")
                        ) {
                            Icon(
                                imageVector = if (isPlayingThis) Icons.Rounded.Stop else Icons.Rounded.GraphicEq,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isPlayingThis) "إيقاف المثال" else "استمع للمثال التطبيقي")
                        }
                    }
                }
            }
        }
    }
}
