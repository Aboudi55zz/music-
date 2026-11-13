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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.OudFingerNote
import com.example.data.OudMaqamGuide
import com.example.data.OudTuning
import com.example.model.InstrumentTimbre
import com.example.model.JinsInfo
import com.example.model.Maqam
import com.example.model.MusicalNote
import com.example.ui.components.InstrumentSelectorBar
import com.example.ui.components.MaqamOudTutorialCard
import com.example.ui.components.MusicalStaffView
import com.example.ui.components.QuarterToneKeyboard
import com.example.ui.theme.QuarterToneAccent
import com.example.viewmodel.PlaybackUiState
import kotlin.math.roundToInt

private enum class StaffNotationMode(val label: String) {
    ASCENDING("السلم صعوداً"),
    DESCENDING("السلم هبوطاً"),
    MELODY("مثال لحني (تقسيمة)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaqamDetailScreen(
    maqam: Maqam,
    isFavorite: Boolean,
    selectedInstrument: InstrumentTimbre,
    playbackState: PlaybackUiState,
    oudTuning: OudTuning,
    lastOudNote: OudFingerNote?,
    oudPulseTrigger: Long,
    oudExternalCourse: Int?,
    oudExternalPressedQt: Int?,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSelectInstrument: (InstrumentTimbre) -> Unit,
    onTempoChange: (Float) -> Unit,
    onPlayNote: (MusicalNote) -> Unit,
    onPlaySequence: (List<MusicalNote>, String) -> Unit,
    onSelectOudTuning: (OudTuning) -> Unit,
    onPluckOudCourse: (Int, Int, Boolean, Float, Boolean) -> Unit,
    onPlayOudGuide: (OudMaqamGuide) -> Unit,
    onOpenInOud: () -> Unit,
    modifier: Modifier = Modifier
) {
    var notationMode by remember(maqam.id) { mutableStateOf(StaffNotationMode.ASCENDING) }
    var lastTappedNoteId by remember(maqam.id) { mutableStateOf<String?>(null) }

    val displayedNotes = when (notationMode) {
        StaffNotationMode.ASCENDING -> maqam.ascendingScale
        StaffNotationMode.DESCENDING -> maqam.descendingScale
        StaffNotationMode.MELODY -> maqam.melodyPhrase
    }

    val staffTag = "detail_${maqam.id}_${notationMode.name}"
    val isPlayingStaff = playbackState.isPlaying && playbackState.sourceTag == staffTag
    val activeStaffIndex = if (isPlayingStaff) playbackState.activeNoteIndex else -1

    val tonicNoteId = maqam.ascendingScale.firstOrNull()?.id
    val scaleNoteIds = remember(maqam.id) {
        (maqam.ascendingScale + maqam.descendingScale).map { it.id }.toSet()
    }
    val activeKeyboardNoteId = when {
        activeStaffIndex in displayedNotes.indices -> displayedNotes[activeStaffIndex].id
        else -> lastTappedNoteId
    }

    val familyColor = Color(maqam.family.accentHex)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = maqam.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${maqam.englishName} • ${maqam.family.arabicName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorite_toggle),
                            tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Metadata & Identity Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, familyColor.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = familyColor.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = maqam.family.arabicName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = familyColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                color = if (maqam.hasQuarterTone) {
                                    QuarterToneAccent.copy(alpha = 0.16f)
                                } else {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                },
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (maqam.hasQuarterTone) "يحتوي ربع تون 𝄳" else "خالٍ من الربع تون",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (maqam.hasQuarterTone) QuarterToneAccent else MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Text(
                            text = maqam.summary,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoStatPill(
                                label = "الركوز (القرار)",
                                value = maqam.tonicName,
                                modifier = Modifier.weight(1f)
                            )
                            InfoStatPill(
                                label = "الغمّاز",
                                value = maqam.ghammazName,
                                modifier = Modifier.weight(1f)
                            )
                            InfoStatPill(
                                label = "الجمع",
                                value = maqam.jamType,
                                modifier = Modifier.weight(0.9f)
                            )
                        }

                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            maqam.moodTags.forEach { tag ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "# $tag",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Interactive Musical Staff & Audio Player
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "النوتة الموسيقية والأمثلة الصوتية",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        // Instrument Timbre Selector
                        InstrumentSelectorBar(
                            selectedInstrument = selectedInstrument,
                            onSelectInstrument = onSelectInstrument
                        )

                        // Mode Tabs: Ascending / Descending / Melodic Phrase
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StaffNotationMode.entries.forEach { mode ->
                                FilterChip(
                                    selected = notationMode == mode,
                                    onClick = { notationMode = mode },
                                    label = { Text(mode.label) },
                                    modifier = Modifier.testTag("notation_mode_${mode.name.lowercase()}")
                                )
                            }
                        }

                        if (notationMode == StaffNotationMode.MELODY) {
                            Text(
                                text = maqam.melodyTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Custom Treble Clef Musical Staff Canvas
                        MusicalStaffView(
                            notes = displayedNotes,
                            activeNoteIndex = activeStaffIndex,
                            tonicNoteId = tonicNoteId,
                            onNoteClick = { note ->
                                lastTappedNoteId = note.id
                                onPlayNote(note)
                            }
                        )

                        Text(
                            text = "اضغط على أي نغمة في المدرج الموسيقي لسماعها منفردة.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Play / Stop + Tempo Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onPlaySequence(displayedNotes, staffTag) },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("detail_play_sequence"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPlayingStaff) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            ) {
                                Icon(
                                    imageVector = if (isPlayingStaff) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        isPlayingStaff -> "إيقاف العزف"
                                        notationMode == StaffNotationMode.MELODY -> "تشغيل المثال اللحني"
                                        notationMode == StaffNotationMode.DESCENDING -> "تشغيل السلم الهابط"
                                        else -> "تشغيل السلم الصاعد"
                                    }
                                )
                            }

                            TempoCycleButton(
                                tempoMultiplier = playbackState.tempoMultiplier,
                                onTempoChange = onTempoChange
                            )
                        }
                    }
                }
            }

            // 3. Detailed Degree-by-Degree Note Cards (Arabic Solfege, Maqam Pitch Name, Hz)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "درجات المقام وتسمياتها التراثية",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        maqam.ascendingScale.forEachIndexed { idx, note ->
                            val isHighlighted = activeStaffIndex == idx && notationMode == StaffNotationMode.ASCENDING
                            Card(
                                modifier = Modifier
                                    .width(108.dp)
                                    .clickable {
                                        lastTappedNoteId = note.id
                                        onPlayNote(note)
                                    }
                                    .testTag("degree_card_$idx"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isHighlighted -> MaterialTheme.colorScheme.primaryContainer
                                        note.isQuarterTone -> QuarterToneAccent.copy(alpha = 0.12f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                ),
                                border = BorderStroke(
                                    width = if (isHighlighted || idx == 0) 1.5.dp else 1.dp,
                                    color = if (note.isQuarterTone) QuarterToneAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = "الدرجة ${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = note.arabicName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (note.isQuarterTone) QuarterToneAccent else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = note.maqamPitchName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${note.westernName} • ${note.frequencyHz.roundToInt()}Hz",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Ajnas Breakdown (Root Jins & Branch Jins)
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LibraryMusic,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "البنية اللحنية: الأجناس المكونة للمقام",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        JinsAnalysisCard(
                            badge = "جنس الأصل (الأساس)",
                            jins = maqam.rootJins,
                            isPlaying = playbackState.isPlaying && playbackState.sourceTag == "root_jins_${maqam.id}",
                            onPlay = { onPlaySequence(maqam.rootJins.notes, "root_jins_${maqam.id}") },
                            testTag = "play_root_jins"
                        )

                        JinsAnalysisCard(
                            badge = "جنس الفرع (الجواب)",
                            jins = maqam.branchJins,
                            isPlaying = playbackState.isPlaying && playbackState.sourceTag == "branch_jins_${maqam.id}",
                            onPlay = { onPlaySequence(maqam.branchJins.notes, "branch_jins_${maqam.id}") },
                            testTag = "play_branch_jins"
                        )
                    }
                }
            }

            // 5. Embedded 2D Oud Tutorial (Explains & Plays How to Play This Maqam on Oud)
            item {
                MaqamOudTutorialCard(
                    maqam = maqam,
                    tuning = oudTuning,
                    lastOudNote = lastOudNote,
                    pulseTrigger = oudPulseTrigger,
                    externalCourse = oudExternalCourse,
                    externalPressedQt = oudExternalPressedQt,
                    playbackState = playbackState,
                    onSelectTuning = onSelectOudTuning,
                    onPluckCourse = onPluckOudCourse,
                    onPlayGuideSequence = onPlayOudGuide,
                    onOpenFullOud = onOpenInOud
                )
            }

            // 6. Interactive Mini Quarter-Tone Keyboard
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            text = "جرّب عزف المقام على البيانو",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    QuarterToneKeyboard(
                        highlightedNoteIds = scaleNoteIds,
                        tonicNoteId = tonicNoteId,
                        activeNoteId = activeKeyboardNoteId,
                        onNoteTap = { note ->
                            lastTappedNoteId = note.id
                            onPlayNote(note)
                        },
                        compactMode = true
                    )
                }
            }

            // 6. Detailed Explanation, Sayr & Famous Examples
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "الشرح الموسيقي والسمات",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = maqam.detailedExplanation,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "سَير المقام وأسلوب الأداء",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = maqam.sayrExplanation,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Text(
                            text = "أشهر الأمثلة والألحان",
                            style = MaterialTheme.typography.titleMedium
                        )
                        maqam.famousExamples.forEach { example ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Album,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = example.title,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = example.artist,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = example.style,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
private fun InfoStatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun JinsAnalysisCard(
    badge: String,
    jins: JinsInfo,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    testTag: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${jins.name} (على ${jins.rootNoteArabic})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onPlay,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag(testTag)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPlaying) "إيقاف" else "اسمع",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Text(
                text = jins.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "الأرباع: ${jins.arabicIntervals}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "الأبعاد: ${jins.westernIntervals}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TempoCycleButton(
    tempoMultiplier: Float,
    onTempoChange: (Float) -> Unit
) {
    val label = when {
        tempoMultiplier < 0.85f -> "بطيء"
        tempoMultiplier > 1.15f -> "سريع"
        else -> "متوسط"
    }
    OutlinedButton(
        onClick = {
            val next = when {
                tempoMultiplier < 0.85f -> 1.0f
                tempoMultiplier > 1.15f -> 0.72f
                else -> 1.3f
            }
            onTempoChange(next)
        },
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("tempo_cycle_button")
    ) {
        Icon(
            imageVector = Icons.Rounded.Speed,
            contentDescription = "سرعة العزف",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}
