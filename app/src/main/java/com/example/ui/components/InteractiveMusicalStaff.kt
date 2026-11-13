package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OudCatalog
import com.example.data.OudFingerNote
import com.example.model.Accidental
import com.example.model.MusicalNote
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.QuarterToneAccent
import kotlin.math.roundToInt

enum class StaffClef(val symbol: String, val arabicName: String, val baseStep: Int) {
    TREBLE("𝄞", "مفتاح صول", 30), // Line 1 = E4 (Diatonic step 30)
    BASS("𝄢", "مفتاح فا", 18)     // Line 1 = G2 (Diatonic step 18)
}

enum class StaffClefMode(val label: String) {
    AUTO("تلقائي"),
    TREBLE("مفتاح صول 𝄞"),
    BASS("مفتاح فا 𝄢")
}

@Composable
fun InteractiveMusicalStaff(
    currentOudNote: OudFingerNote? = null,
    currentMusicalNote: MusicalNote? = null,
    guideScale: List<MusicalNote>? = null,
    onNoteTapped: ((MusicalNote) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    var clefMode by remember { mutableStateOf(StaffClefMode.AUTO) }
    var showScaleView by remember { mutableStateOf(false) }

    // Resolve active note details
    val activeAccidental = currentOudNote?.accidental ?: currentMusicalNote?.accidental ?: Accidental.NATURAL
    val isQuarterTone = activeAccidental == Accidental.HALF_FLAT || activeAccidental == Accidental.HALF_SHARP

    val diatonicStep = remember(currentOudNote, currentMusicalNote) {
        when {
            currentOudNote != null -> {
                val letter = currentOudNote.westernShort.firstOrNull { it in 'A'..'G' } ?: 'C'
                val letterIndex = when (letter) {
                    'C' -> 0; 'D' -> 1; 'E' -> 2; 'F' -> 3; 'G' -> 4; 'A' -> 5; 'B' -> 6; else -> 0
                }
                currentOudNote.octaveNumber * 7 + letterIndex
            }
            currentMusicalNote != null -> {
                val letter = currentMusicalNote.westernName.firstOrNull { it in 'A'..'G' } ?: 'C'
                val octave = currentMusicalNote.westernName.firstOrNull { it.isDigit() }?.digitToInt() ?: 4
                val letterIndex = when (letter) {
                    'C' -> 0; 'D' -> 1; 'E' -> 2; 'F' -> 3; 'G' -> 4; 'A' -> 5; 'B' -> 6; else -> 0
                }
                octave * 7 + letterIndex
            }
            else -> 28 // Middle C (C4) default
        }
    }

    // Determine current clef
    val currentClef = when (clefMode) {
        StaffClefMode.AUTO -> if (diatonicStep < 25) StaffClef.BASS else StaffClef.TREBLE
        StaffClefMode.TREBLE -> StaffClef.TREBLE
        StaffClefMode.BASS -> StaffClef.BASS
    }

    // Note pulse animation on note change
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(currentOudNote, currentMusicalNote) {
        pulseAnim.snapTo(1.24f)
        pulseAnim.animateTo(1f, animationSpec = tween(280))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_musical_staff"),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xEE1A0E08),
        border = BorderStroke(1.2.dp, Color(0x66E2B052)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Top Controls Bar: Title + Clef Selection Chips + Scale Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFFFFD56B),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "المدرج الموسيقي (Musical Staff)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily,
                        color = Color(0xFFFFE6A7)
                    )
                }

                // Clef and Scale mode chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StaffClefMode.entries.forEach { mode ->
                        FilterChip(
                            selected = clefMode == mode,
                            onClick = { clefMode = mode },
                            label = { Text(mode.label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE2B052),
                                selectedLabelColor = Color(0xFF1B0F07),
                                containerColor = Color(0xFF2B180F)
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }

                    if (guideScale != null && guideScale.isNotEmpty()) {
                        FilterChip(
                            selected = showScaleView,
                            onClick = { showScaleView = !showScaleView },
                            label = { Text(if (showScaleView) "السلم" else "نغمة حية", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4FD1CD),
                                selectedLabelColor = Color(0xFF1B0F07),
                                containerColor = Color(0xFF2B180F)
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }

            // 2. The Interactive 5-Line Musical Staff Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF24140A))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(currentClef, guideScale) {
                            detectTapGestures { offset ->
                                if (onNoteTapped != null) {
                                    val lineSpacing = 13.dp.toPx()
                                    val staffCenterY = size.height * 0.52f
                                    val line1Y = staffCenterY + lineSpacing * 2f
                                    val stepOffset = Math.round((line1Y - offset.y) / (lineSpacing * 0.5f))
                                    val tappedStep = currentClef.baseStep + stepOffset
                                    val octave = Math.floorDiv(tappedStep, 7)
                                    val letterIdx = ((tappedStep % 7) + 7) % 7
                                    val letter = when (letterIdx) {
                                        0 -> "C"; 1 -> "D"; 2 -> "E"; 3 -> "F"; 4 -> "G"; 5 -> "A"; else -> "B"
                                    }
                                    val solfege = when (letterIdx) {
                                        0 -> "دو"; 1 -> "ري"; 2 -> "مي"; 3 -> "فا"; 4 -> "صول"; 5 -> "لا"; else -> "سي"
                                    }
                                    val tappedNote = MusicalNote(
                                        id = "staff_tap_${letter}$octave",
                                        arabicName = solfege,
                                        maqamPitchName = solfege,
                                        westernName = "$letter$octave",
                                        diatonicStep = tappedStep,
                                        accidental = Accidental.NATURAL,
                                        quarterToneOffset = (octave - 4) * 24 + when (letterIdx) {
                                            0 -> 0; 1 -> 4; 2 -> 8; 3 -> 10; 4 -> 14; 5 -> 18; else -> 22
                                        }
                                    )
                                    onNoteTapped(tappedNote)
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val lineSpacing = 13.dp.toPx()
                    val staffCenterY = h * 0.52f
                    val line1Y = staffCenterY + lineSpacing * 2f
                    val line5Y = staffCenterY - lineSpacing * 2f
                    val staffStart = 20.dp.toPx()
                    val staffEnd = w - 20.dp.toPx()

                    // Draw Initial and Final Measure Barlines
                    drawLine(
                        color = Color(0xFFD6C2A3).copy(alpha = 0.55f),
                        start = Offset(staffStart, line5Y),
                        end = Offset(staffStart, line1Y),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFD6C2A3).copy(alpha = 0.55f),
                        start = Offset(staffEnd, line5Y),
                        end = Offset(staffEnd, line1Y),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Draw 5 Parallel Staff Lines
                    for (lineIdx in 0..4) {
                        val ly = line1Y - lineIdx * lineSpacing
                        drawLine(
                            color = Color(0xFFE2B052).copy(alpha = 0.55f),
                            start = Offset(staffStart, ly),
                            end = Offset(staffEnd, ly),
                            strokeWidth = 1.3.dp.toPx()
                        )
                    }

                    // Draw Clef Symbol (Treble 𝄞 or Bass 𝄢)
                    val clefText = currentClef.symbol
                    val clefLayout = textMeasurer.measure(
                        text = clefText,
                        style = TextStyle(
                            color = Color(0xFFFFD56B),
                            fontSize = if (currentClef == StaffClef.TREBLE) 44.sp else 34.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    )
                    val clefOffsetY = if (currentClef == StaffClef.TREBLE) {
                        staffCenterY - clefLayout.size.height * 0.52f
                    } else {
                        staffCenterY - clefLayout.size.height * 0.58f
                    }
                    drawText(
                        textLayoutResult = clefLayout,
                        topLeft = Offset(staffStart + 10.dp.toPx(), clefOffsetY)
                    )

                    // Draw Notes
                    if (showScaleView && guideScale != null && guideScale.isNotEmpty()) {
                        // Render full scale across staff
                        val noteCount = guideScale.size
                        val availableW = staffEnd - (staffStart + 56.dp.toPx())
                        val noteSpacing = (availableW / (noteCount + 1)).coerceAtLeast(24.dp.toPx())

                        guideScale.forEachIndexed { idx, scaleNote ->
                            val nx = staffStart + 56.dp.toPx() + (idx + 1) * noteSpacing
                            val letter = scaleNote.westernName.firstOrNull { it in 'A'..'G' } ?: 'C'
                            val octave = scaleNote.westernName.firstOrNull { it.isDigit() }?.digitToInt() ?: 4
                            val letterIndex = when (letter) {
                                'C' -> 0; 'D' -> 1; 'E' -> 2; 'F' -> 3; 'G' -> 4; 'A' -> 5; 'B' -> 6; else -> 0
                            }
                            val noteStep = octave * 7 + letterIndex
                            val stepOffset = noteStep - currentClef.baseStep
                            val ny = line1Y - stepOffset * (lineSpacing * 0.5f)

                            val isActive = (currentMusicalNote?.westernName == scaleNote.westernName) ||
                                (currentOudNote != null && currentOudNote.arabicName == scaleNote.arabicName)

                            drawStaffNote(
                                noteX = nx,
                                noteY = ny,
                                stepOffset = stepOffset,
                                line1Y = line1Y,
                                lineSpacing = lineSpacing,
                                accidental = scaleNote.accidental,
                                isQuarterTone = scaleNote.isQuarterTone,
                                isActive = isActive,
                                pulseScale = if (isActive) pulseAnim.value else 1f,
                                textMeasurer = textMeasurer
                            )
                        }
                    } else {
                        // Render single active note in center of staff
                        val noteX = staffStart + (staffEnd - staffStart) * 0.54f
                        val stepOffset = diatonicStep - currentClef.baseStep
                        val noteY = line1Y - stepOffset * (lineSpacing * 0.5f)

                        drawStaffNote(
                            noteX = noteX,
                            noteY = noteY,
                            stepOffset = stepOffset,
                            line1Y = line1Y,
                            lineSpacing = lineSpacing,
                            accidental = activeAccidental,
                            isQuarterTone = isQuarterTone,
                            isActive = true,
                            pulseScale = pulseAnim.value,
                            textMeasurer = textMeasurer
                        )
                    }
                }
            }

            // 3. Bottom Note Info Banner (Real-time Feedback)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isQuarterTone) QuarterToneAccent.copy(alpha = 0.18f) else Color(0xFF28180E),
                border = BorderStroke(1.dp, if (isQuarterTone) QuarterToneAccent else Color(0x77E2B052)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val solfege = currentOudNote?.arabicName ?: currentMusicalNote?.arabicName ?: "دو"
                    val maqamPitch = currentOudNote?.maqamPitchName ?: currentMusicalNote?.maqamPitchName ?: "راست"
                    val western = currentOudNote?.westernSubscript ?: currentMusicalNote?.westernName ?: "C4"
                    val hz = currentOudNote?.frequencyHz?.roundToInt() ?: currentMusicalNote?.frequencyHz?.roundToInt() ?: 261

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isQuarterTone) QuarterToneAccent else Color(0xFFFFD56B))
                        )
                        Column {
                            Text(
                                text = "♪ $solfege  $western  •  $maqamPitch",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily,
                                color = if (isQuarterTone) QuarterToneAccent else Color(0xFFFFE6A7)
                            )
                            currentOudNote?.let {
                                Text(
                                    text = "الوتر ${it.courseIndex + 1} • ~${hz}Hz • ${currentClef.arabicName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = AmiriFontFamily,
                                    color = Color(0xFFD6C2A3)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF140A05),
                        border = BorderStroke(0.8.dp, Color(0x66E2B052))
                    ) {
                        Text(
                            text = "${currentClef.symbol} $western",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily,
                            color = Color(0xFFFFD56B),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper to render an authentic musical note head, stem, ledger lines, and accidental on the staff.
 */
private fun DrawScope.drawStaffNote(
    noteX: Float,
    noteY: Float,
    stepOffset: Int,
    line1Y: Float,
    lineSpacing: Float,
    accidental: Accidental,
    isQuarterTone: Boolean,
    isActive: Boolean,
    pulseScale: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val headW = lineSpacing * 1.35f * pulseScale
    val headH = lineSpacing * 0.96f * pulseScale
    val ledgerHalfW = headW * 0.95f

    // 1. Draw Ledger Lines if note is below or above the 5 staff lines
    if (stepOffset <= -2) {
        // Below staff (Middle C = -2, A3 = -4, etc.)
        for (s in -2 downTo stepOffset step 2) {
            val ly = line1Y - s * (lineSpacing * 0.5f)
            drawLine(
                color = Color(0xFFE2B052).copy(alpha = 0.85f),
                start = Offset(noteX - ledgerHalfW, ly),
                end = Offset(noteX + ledgerHalfW, ly),
                strokeWidth = 1.6.dp.toPx()
            )
        }
    } else if (stepOffset >= 10) {
        // Above staff (A5 = 10, C6 = 12, etc.)
        for (s in 10..stepOffset step 2) {
            val ly = line1Y - s * (lineSpacing * 0.5f)
            drawLine(
                color = Color(0xFFE2B052).copy(alpha = 0.85f),
                start = Offset(noteX - ledgerHalfW, ly),
                end = Offset(noteX + ledgerHalfW, ly),
                strokeWidth = 1.6.dp.toPx()
            )
        }
    }

    // 2. Accidental Sign (e.g. 𝄳 half-flat, ♭, ♯, 𝄲, ♮)
    if (accidental != Accidental.NATURAL && accidental.symbol.isNotEmpty()) {
        val accText = accidental.symbol
        val accLayout = textMeasurer.measure(
            text = accText,
            style = TextStyle(
                color = if (isQuarterTone) QuarterToneAccent else Color(0xFFFFD56B),
                fontSize = if (accidental == Accidental.HALF_FLAT) 26.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CairoFontFamily
            )
        )
        val accX = noteX - headW * 0.65f - accLayout.size.width - 2.dp.toPx()
        val accY = noteY - accLayout.size.height * 0.50f
        drawText(
            textLayoutResult = accLayout,
            topLeft = Offset(accX, accY)
        )
    }

    // 3. Active Note Glow Halo
    if (isActive) {
        drawCircle(
            color = if (isQuarterTone) QuarterToneAccent.copy(alpha = 0.28f) else Color(0xFFFFD56B).copy(alpha = 0.28f),
            radius = headW * 1.25f,
            center = Offset(noteX, noteY)
        )
    }

    // 4. Oval Note Head (tilted -18 degrees like classical engraving)
    val headColor = when {
        !isActive -> Color(0xFFD6C2A3).copy(alpha = 0.65f)
        isQuarterTone -> QuarterToneAccent
        else -> Color(0xFFFFE6A7)
    }

    rotate(degrees = -18f, pivot = Offset(noteX, noteY)) {
        drawOval(
            color = headColor,
            topLeft = Offset(noteX - headW * 0.5f, noteY - headH * 0.5f),
            size = Size(headW, headH)
        )
        drawOval(
            color = Color(0xFF1B0F07),
            topLeft = Offset(noteX - headW * 0.5f, noteY - headH * 0.5f),
            size = Size(headW, headH),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }

    // 5. Note Stem (points UP from right if below line 3, points DOWN from left if on/above line 3)
    val stemUp = stepOffset < 4
    val stemLength = lineSpacing * 3.3f
    val stemStroke = 1.8.dp.toPx()
    val stemColor = headColor

    if (stemUp) {
        val stemX = noteX + headW * 0.44f
        val stemStartY = noteY - 1f
        val stemEndY = stemStartY - stemLength
        drawLine(
            color = stemColor,
            start = Offset(stemX, stemStartY),
            end = Offset(stemX, stemEndY),
            strokeWidth = stemStroke,
            cap = StrokeCap.Round
        )
    } else {
        val stemX = noteX - headW * 0.44f
        val stemStartY = noteY + 1f
        val stemEndY = stemStartY + stemLength
        drawLine(
            color = stemColor,
            start = Offset(stemX, stemStartY),
            end = Offset(stemX, stemEndY),
            strokeWidth = stemStroke,
            cap = StrokeCap.Round
        )
    }
}
