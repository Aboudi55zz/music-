package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.InteractiveMusicalStaff
import com.example.ui.components.Oud2DCanvas
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily
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

    // Fullscreen Immersion & Panel state
    var isImmersionMode by remember { mutableStateOf(false) }
    var showSettingsPanel by remember { mutableStateOf(false) }
    var showStringsStrip by remember { mutableStateOf(true) }
    var showStaffView by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF130A05))
            .testTag("oud_screen")
    ) {
        // 1. THE OUD CANVAS TAKES THE ENTIRE SCREEN SPACE! (Edge-to-edge, full height & width)
        Oud2DCanvas(
            tuning = tuning,
            guideMaqam = guideMaqam,
            showMaqamGuides = showGuides,
            externalPulseTrigger = pulseTrigger,
            externalPluckedCourse = externalCourse,
            externalPressedQt = externalPressedQt,
            onPluckCourse = { courseIdx, qt, posRatio, isUp ->
                onPluckCourse(courseIdx, qt, false, posRatio, isUp)
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. IMMERSIVE MODE CONTROLS
        if (isImmersionMode) {
            // Minimalist Floating Exit Button (Top End)
            Surface(
                shape = CircleShape,
                color = Color(0xD01B0F07),
                border = BorderStroke(1.2.dp, Color(0x99E2B052)),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
            ) {
                IconButton(
                    onClick = { isImmersionMode = false },
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FullscreenExit,
                        contentDescription = "استعادة أدوات التحكم",
                        tint = Color(0xFFFFD56B)
                    )
                }
            }

            // Minimalist Floating Live Note Badge (Top Start)
            if (lastNote != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xD01B0F07),
                    border = BorderStroke(1.dp, Color(0x77E2B052)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "${lastNote.musicalNoteSymbol} ${lastNote.arabicName}  ${lastNote.clefSymbol} ${lastNote.westernSubscript} • ${lastNote.maqamPitchName}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily,
                        color = if (lastNote.isQuarterTone) QuarterToneAccent else Color(0xFFFFD56B),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        } else {
            // 3. NORMAL MODE: FLOATING TOP HUD (Live Note + Controls)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xD8180E07),
                border = BorderStroke(1.2.dp, Color(0x66E2B052)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Top Bar: Note HUD + Quick Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Live Note Readout Badge
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (lastNote?.isQuarterTone == true) {
                                QuarterToneAccent.copy(alpha = 0.22f)
                            } else {
                                Color(0xFF28180E).copy(alpha = 0.90f)
                            },
                            border = BorderStroke(
                                1.dp,
                                if (lastNote?.isQuarterTone == true) QuarterToneAccent else Color(0x88E2B052)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("oud_note_hud")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = lastNote?.let {
                                        "${it.musicalNoteSymbol} ${it.arabicName}  ${it.clefSymbol} ${it.westernSubscript}  •  ${it.maqamPitchName}"
                                    } ?: "♪ عود كامل الشاشة ♫",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CairoFontFamily,
                                    color = if (lastNote?.isQuarterTone == true) QuarterToneAccent else Color(0xFFFFE6A7),
                                    maxLines = 1
                                )
                                Text(
                                    text = lastNote?.let {
                                        val course = tuning.courses[it.courseIndex]
                                        "${course.courseCircledSymbol} الوتر ${course.courseNumber}: ${course.shortArabicName} (${course.coursePairSymbol}) • ${it.frequencyHz.roundToInt()} Hz"
                                    } ?: "${tuning.arabicName} • انقر أو اسحب للعزف",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFD6C2A3),
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Action Buttons Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Strum All button
                            IconButton(
                                onClick = onStrumAll,
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("oud_strum_all")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.GraphicEq,
                                    contentDescription = "ضربة ريشة شاملة",
                                    tint = Color(0xFFFFD56B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Play Maqam Scale (if active)
                            if (guideMaqam != null) {
                                IconButton(
                                    onClick = onPlayMaqamOnOud,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .testTag("oud_play_guide_maqam")
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingMaqam) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                        contentDescription = "عزف المقام",
                                        tint = if (isPlayingMaqam) Color(0xFFFF5252) else Color(0xFF4FD1CD),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Interactive Musical Staff Toggle Button
                            IconButton(
                                onClick = { showStaffView = !showStaffView },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("oud_toggle_staff")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MusicNote,
                                    contentDescription = "المدرج الموسيقي التفاعلي",
                                    tint = if (showStaffView) Color(0xFFFFD56B) else Color(0xFFE4BE84),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Settings / Tuning Panel Toggle
                            IconButton(
                                onClick = { showSettingsPanel = !showSettingsPanel },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (showSettingsPanel) Icons.Rounded.ExpandLess else Icons.Rounded.Tune,
                                    contentDescription = "خيارات الدوزان والمقام",
                                    tint = if (showSettingsPanel) Color(0xFFFFD56B) else Color(0xFFE4BE84),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Pure Fullscreen Immersion Mode Toggle
                            IconButton(
                                onClick = { isImmersionMode = true },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Fullscreen,
                                    contentDescription = "ملء الشاشة الكامل للعود",
                                    tint = Color(0xFFFFE6A7),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    // Interactive Musical Staff (Dynamically changes with played note!)
                    AnimatedVisibility(
                        visible = showStaffView,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        InteractiveMusicalStaff(
                            currentOudNote = lastNote,
                            guideScale = guideMaqam?.ascendingScale,
                            onNoteTapped = { tappedNote ->
                                val match = OudCatalog.findBestFingeringForNote(tappedNote, tuning)
                                if (match != null) {
                                    onPluckCourse(match.first, match.second, false, 0.21f, false)
                                }
                            },
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Expandable Settings (Tunings & Maqam Guides)
                    AnimatedVisibility(
                        visible = showSettingsPanel,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Tuning Selector + Guide Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OudTuning.entries.forEach { item ->
                                    FilterChip(
                                        selected = item == tuning,
                                        onClick = { onSelectTuning(item) },
                                        label = { Text(item.arabicName, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFE2B052),
                                            selectedLabelColor = Color(0xFF1B0F07)
                                        ),
                                        modifier = Modifier.testTag("oud_tuning_${item.name.lowercase()}")
                                    )
                                }

                                FilterChip(
                                    selected = showGuides,
                                    onClick = onToggleGuides,
                                    label = { Text("دليل المقام", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (showGuides) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF4FD1CD),
                                        selectedLabelColor = Color(0xFF1B0F07)
                                    ),
                                    modifier = Modifier.testTag("oud_toggle_guides")
                                )
                            }

                            // Maqam Fingerboard Guide Picker
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
                                    label = { Text("عزف حر", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("oud_guide_none")
                                )
                                allMaqamat.forEach { maqam ->
                                    val selected = guideMaqam?.id == maqam.id
                                    FilterChip(
                                        selected = selected,
                                        onClick = { onSelectGuideMaqam(maqam) },
                                        label = { Text(maqam.name.removePrefix("مقام "), fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFE2B052),
                                            selectedLabelColor = Color(0xFF1B0F07)
                                        ),
                                        modifier = Modifier.testTag("oud_guide_${maqam.id}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Legend overlay in top corner when guides are active
            if (showGuides && guideMaqam != null && !showSettingsPanel) {
                Surface(
                    color = Color(0xD81B0F07),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.8.dp, Color(0x66E2B052)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 70.dp, end = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        LegendRow(Color(0xFFE2B052), "الركوز")
                        LegendRow(Color(0xFF4FD1CD), "ربع تون 𝄳")
                    }
                }
            }

            // 4. FLOATING BOTTOM OPEN-COURSE STRIP (Aligned LTR)
            Surface(
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                color = Color(0xEB1A0E07),
                border = BorderStroke(1.dp, Color(0x55E2B052)),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(top = 4.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Header handle to collapse/expand
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStringsStrip = !showStringsStrip },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showStringsStrip) "الأوتار الستة المفتوحة والمسافات (نبر مباشر):" else "الأوتار الستة المفتوحة ♫ (إظهار)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily,
                            color = Color(0xFFE4BE84)
                        )
                        Icon(
                            imageVector = if (showStringsStrip) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color(0xFFE4BE84),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (showStringsStrip) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                // Interval Labels Between Courses
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    for (i in 0 until tuning.courses.lastIndex) {
                                        val intervalText = OudCatalog.intervalLabelBetween(
                                            tuning.courses[i],
                                            tuning.courses[i + 1]
                                        ).substringBefore(" ")
                                        Text(
                                            text = "↔ $intervalText",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = CairoFontFamily,
                                            color = Color(0xFFFFD56B),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                // 6 Open-Course Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    tuning.courses.forEachIndexed { idx, course ->
                                        val isLastCourse = lastNote?.courseIndex == idx
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .minimumInteractiveComponentSize()
                                                .clip(RoundedCornerShape(11.dp))
                                                .clickable { onPluckCourse(idx, 0, true, 0.21f, false) }
                                                .testTag("oud_open_string_${course.courseNumber}"),
                                            shape = RoundedCornerShape(11.dp),
                                            color = if (isLastCourse) {
                                                Color(0xFF503017)
                                            } else {
                                                Color(0xFF28180E).copy(alpha = 0.95f)
                                            },
                                            border = BorderStroke(
                                                width = if (isLastCourse) 1.8.dp else 0.9.dp,
                                                color = if (isLastCourse) Color(0xFFFFD56B) else Color(0x66E2B052)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(1.dp)
                                            ) {
                                                // Clef + Circled course number + Pair symbol (𝄢 ⑥ ⚬ / 𝄞 ① ⚬⚬)
                                                Text(
                                                    text = "${course.clefSymbol} ${course.courseCircledSymbol} ${course.coursePairSymbol}",
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = CairoFontFamily,
                                                    color = if (isLastCourse) Color(0xFFFFD56B) else Color(0xFFD6C2A3),
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1
                                                )
                                                // Solfege Note Name with note symbol (♪ دو / ♪ فا)
                                                Text(
                                                    text = "♪ ${course.openSolfegeName}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontFamily = CairoFontFamily,
                                                    color = if (isLastCourse) Color(0xFFFFFBE8) else Color(0xFFFBF4E8),
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1
                                                )
                                                // Western pitch with octave (C2, F2, A2, D3, G3, C4)
                                                Text(
                                                    text = course.openWesternName,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = CairoFontFamily,
                                                    color = Color(0xFFD6A85A),
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1
                                                )
                                                // Traditional Arabic string name (بَم، قرار فا، عشيران، دوكاه، نوى، كردان)
                                                Text(
                                                    text = course.shortArabicName,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = AmiriFontFamily,
                                                    color = Color(0xFFD6C2A3),
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
                }
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.5.sp,
            color = Color(0xFFFBF4E8)
        )
    }
}
