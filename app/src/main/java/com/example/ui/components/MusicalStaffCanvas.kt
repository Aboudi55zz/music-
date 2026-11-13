package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Accidental
import com.example.model.MusicalNote
import com.example.ui.theme.QuarterToneAccent
import com.example.ui.theme.QuarterToneHighlight
import com.example.ui.theme.StaffLineDark
import com.example.ui.theme.StaffLineLight
import com.example.ui.theme.StaffPaperDark
import com.example.ui.theme.StaffPaperLight

@Composable
fun MusicalStaffView(
    notes: List<MusicalNote>,
    activeNoteIndex: Int,
    tonicNoteId: String?,
    onNoteClick: (MusicalNote) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val paperColor = if (isDark) StaffPaperDark else StaffPaperLight
    val staffLineColor = if (isDark) StaffLineDark else StaffLineLight
    val defaultInkColor = MaterialTheme.colorScheme.onSurface
    val primaryGold = MaterialTheme.colorScheme.primary
    val quarterToneColor = if (isDark) QuarterToneHighlight else QuarterToneAccent
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)

    val scrollState = rememberScrollState()
    val noteColumnWidthDp = 64.dp
    val clefWidthDp = 58.dp
    val density = LocalDensity.current

    // Auto-scroll smoothly as playback advances
    LaunchedEffect(activeNoteIndex) {
        if (activeNoteIndex >= 0) {
            val targetPx = with(density) {
                ((clefWidthDp + noteColumnWidthDp * activeNoteIndex) - 100.dp)
                    .toPx()
                    .coerceAtLeast(0f)
                    .toInt()
            }
            scrollState.animateScrollTo(targetPx)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(paperColor)
                .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(vertical = 12.dp)
                .testTag("musical_staff_view")
        ) {
            val totalContentWidthDp = clefWidthDp + (noteColumnWidthDp * notes.size.coerceAtLeast(1)) + 16.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column(modifier = Modifier.width(totalContentWidthDp)) {
                    // Staff Canvas (Clef + 5 lines + Notes + Accidentals)
                    Canvas(
                        modifier = Modifier
                            .width(totalContentWidthDp)
                            .height(150.dp)
                    ) {
                        // Staff vertical layout:
                        // Top line (F5 = diatonicStep 10) at y = 36f ..
                        val lineSpacing = size.height / 11.5f
                        val halfStep = lineSpacing / 2f
                        // E4 (diatonicStep = 2) is bottom staff line (line index 4 from top)
                        val bottomLineY = size.height * 0.68f

                        fun stepToY(diatonicStep: Int): Float {
                            return bottomLineY - (diatonicStep - 2) * halfStep
                        }

                        // Draw 5 staff lines: E4(2), G4(4), B4(6), D5(8), F5(10)
                        for (step in listOf(2, 4, 6, 8, 10)) {
                            val y = stepToY(step)
                            drawLine(
                                color = staffLineColor,
                                start = Offset(12f, y),
                                end = Offset(size.width - 12f, y),
                                strokeWidth = 2.2f
                            )
                        }

                        // Draw Treble Clef on the left
                        drawTrebleClef(
                            centerX = 44f,
                            g4Y = stepToY(4),
                            lineSpacing = lineSpacing,
                            color = primaryGold
                        )

                        val clefOffsetPx = clefWidthDp.toPx()
                        val colWidthPx = noteColumnWidthDp.toPx()

                        // Draw each note
                        notes.forEachIndexed { index, note ->
                            val centerX = clefOffsetPx + colWidthPx * index + colWidthPx / 2f
                            val noteY = stepToY(note.diatonicStep)
                            val isActive = index == activeNoteIndex
                            val isTonic = tonicNoteId != null && note.id == tonicNoteId

                            val noteColor = when {
                                isActive -> primaryGold
                                note.isQuarterTone -> quarterToneColor
                                isTonic -> primaryGold
                                else -> defaultInkColor
                            }

                            // Active halo behind note
                            if (isActive) {
                                drawCircle(
                                    color = primaryGold.copy(alpha = 0.28f),
                                    radius = lineSpacing * 1.45f,
                                    center = Offset(centerX, noteY)
                                )
                            }

                            // Ledger lines below E4 (step <= 0: C4=0, A3=-2)
                            if (note.diatonicStep <= 0) {
                                var ledgerStep = 0
                                while (ledgerStep >= note.diatonicStep) {
                                    val ly = stepToY(ledgerStep)
                                    drawLine(
                                        color = staffLineColor,
                                        start = Offset(centerX - lineSpacing * 0.9f, ly),
                                        end = Offset(centerX + lineSpacing * 0.9f, ly),
                                        strokeWidth = 2.4f
                                    )
                                    ledgerStep -= 2
                                }
                            }

                            // Ledger lines above F5 (step >= 12)
                            if (note.diatonicStep >= 12) {
                                var ledgerStep = 12
                                while (ledgerStep <= note.diatonicStep) {
                                    val ly = stepToY(ledgerStep)
                                    drawLine(
                                        color = staffLineColor,
                                        start = Offset(centerX - lineSpacing * 0.9f, ly),
                                        end = Offset(centerX + lineSpacing * 0.9f, ly),
                                        strokeWidth = 2.4f
                                    )
                                    ledgerStep += 2
                                }
                            }

                            // Draw Accidental before the note head
                            if (note.accidental != Accidental.NATURAL) {
                                drawAccidental(
                                    accidental = note.accidental,
                                    x = centerX - lineSpacing * 1.15f,
                                    y = noteY,
                                    unit = lineSpacing,
                                    color = if (note.isQuarterTone) quarterToneColor else noteColor
                                )
                            }

                            // Draw Note Head (oval tilted slightly)
                            val headW = lineSpacing * 1.12f
                            val headH = lineSpacing * 0.82f
                            val isOpenHead = note.durationBeats >= 1.5f
                            rotate(degrees = -18f, pivot = Offset(centerX, noteY)) {
                                if (isOpenHead) {
                                    drawOval(
                                        color = noteColor,
                                        topLeft = Offset(centerX - headW / 2f, noteY - headH / 2f),
                                        size = Size(headW, headH),
                                        style = Stroke(width = 3.6f)
                                    )
                                } else {
                                    drawOval(
                                        color = noteColor,
                                        topLeft = Offset(centerX - headW / 2f, noteY - headH / 2f),
                                        size = Size(headW, headH)
                                    )
                                }
                            }

                            // Draw Stem & Flag (if < 2.0 beats)
                            if (note.durationBeats < 2.0f) {
                                val stemUp = note.diatonicStep < 6
                                val stemLen = lineSpacing * 2.8f
                                val stemX = if (stemUp) centerX + headW * 0.44f else centerX - headW * 0.44f
                                val stemEndY = if (stemUp) noteY - stemLen else noteY + stemLen

                                drawLine(
                                    color = noteColor,
                                    start = Offset(stemX, noteY),
                                    end = Offset(stemX, stemEndY),
                                    strokeWidth = 3.0f,
                                    cap = StrokeCap.Round
                                )

                                // Eighth note flag for short notes (0.5f beats)
                                if (note.durationBeats <= 0.6f) {
                                    val flagPath = Path().apply {
                                        moveTo(stemX, stemEndY)
                                        if (stemUp) {
                                            cubicTo(
                                                stemX + lineSpacing * 0.85f,
                                                stemEndY + lineSpacing * 0.45f,
                                                stemX + lineSpacing * 0.9f,
                                                stemEndY + lineSpacing * 1.25f,
                                                stemX + lineSpacing * 0.35f,
                                                stemEndY + lineSpacing * 1.65f
                                            )
                                        } else {
                                            cubicTo(
                                                stemX + lineSpacing * 0.85f,
                                                stemEndY - lineSpacing * 0.45f,
                                                stemX + lineSpacing * 0.9f,
                                                stemEndY - lineSpacing * 1.25f,
                                                stemX + lineSpacing * 0.35f,
                                                stemEndY - lineSpacing * 1.65f
                                            )
                                        }
                                    }
                                    drawPath(
                                        path = flagPath,
                                        color = noteColor,
                                        style = Stroke(width = 3.2f, cap = StrokeCap.Round)
                                    )
                                }
                            }

                            // Dot for dotted notes (e.g. 1.5f beats)
                            if (note.durationBeats == 1.5f) {
                                drawCircle(
                                    color = noteColor,
                                    radius = lineSpacing * 0.16f,
                                    center = Offset(centerX + headW * 0.82f, noteY)
                                )
                            }
                        }
                    }

                    // Interactive clickable note label strip right underneath the staff
                    Row(
                        modifier = Modifier
                            .width(totalContentWidthDp)
                            .padding(start = clefWidthDp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        notes.forEachIndexed { index, note ->
                            val isActive = index == activeNoteIndex
                            val bgTint = when {
                                isActive -> primaryGold.copy(alpha = 0.22f)
                                note.isQuarterTone -> quarterToneColor.copy(alpha = 0.14f)
                                else -> Color.Transparent
                            }
                            Column(
                                modifier = Modifier
                                    .width(noteColumnWidthDp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bgTint)
                                    .clickable { onNoteClick(note) }
                                    .padding(vertical = 6.dp, horizontal = 2.dp)
                                    .semantics {
                                        contentDescription = "عزف نغمة ${note.arabicName}"
                                    }
                                    .testTag("staff_note_$index"),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = formatShortArabicNote(note),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isActive || note.isQuarterTone) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        isActive -> primaryGold
                                        note.isQuarterTone -> quarterToneColor
                                        else -> defaultInkColor
                                    },
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = note.westernName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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

private fun formatShortArabicNote(note: MusicalNote): String {
    val base = note.arabicName
        .replace(" قرار", "")
        .replace(" جواب", "")
        .replace(" نصف بيمول", " 𝄳")
        .replace(" بيمول", " ♭")
        .replace(" دييز", " ♯")
    return base
}

private fun DrawScope.drawTrebleClef(
    centerX: Float,
    g4Y: Float,
    lineSpacing: Float,
    color: Color
) {
    val path = Path().apply {
        // Start inside G4 spiral
        moveTo(centerX + lineSpacing * 0.15f, g4Y + lineSpacing * 0.25f)
        cubicTo(
            centerX - lineSpacing * 0.45f, g4Y + lineSpacing * 0.25f,
            centerX - lineSpacing * 0.55f, g4Y - lineSpacing * 0.65f,
            centerX + lineSpacing * 0.1f, g4Y - lineSpacing * 0.7f
        )
        cubicTo(
            centerX + lineSpacing * 0.85f, g4Y - lineSpacing * 0.7f,
            centerX + lineSpacing * 0.85f, g4Y + lineSpacing * 0.85f,
            centerX - lineSpacing * 0.15f, g4Y + lineSpacing * 0.85f
        )
        cubicTo(
            centerX - lineSpacing * 1.0f, g4Y + lineSpacing * 0.85f,
            centerX - lineSpacing * 0.95f, g4Y - lineSpacing * 1.25f,
            centerX + lineSpacing * 0.2f, g4Y - lineSpacing * 2.15f
        )
        cubicTo(
            centerX + lineSpacing * 0.55f, g4Y - lineSpacing * 2.55f,
            centerX + lineSpacing * 0.18f, g4Y - lineSpacing * 3.15f,
            centerX - lineSpacing * 0.05f, g4Y - lineSpacing * 2.85f
        )
        lineTo(centerX + lineSpacing * 0.05f, g4Y + lineSpacing * 1.75f)
        cubicTo(
            centerX + lineSpacing * 0.08f, g4Y + lineSpacing * 2.25f,
            centerX - lineSpacing * 0.48f, g4Y + lineSpacing * 2.25f,
            centerX - lineSpacing * 0.52f, g4Y + lineSpacing * 1.85f
        )
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawAccidental(
    accidental: Accidental,
    x: Float,
    y: Float,
    unit: Float,
    color: Color
) {
    when (accidental) {
        Accidental.FLAT -> {
            val stemTop = y - unit * 1.35f
            val stemBottom = y + unit * 0.45f
            drawLine(
                color = color,
                start = Offset(x - unit * 0.18f, stemTop),
                end = Offset(x - unit * 0.18f, stemBottom),
                strokeWidth = 2.6f,
                cap = StrokeCap.Round
            )
            val loop = Path().apply {
                moveTo(x - unit * 0.18f, y - unit * 0.15f)
                cubicTo(
                    x + unit * 0.42f, y - unit * 0.35f,
                    x + unit * 0.42f, y + unit * 0.25f,
                    x - unit * 0.18f, y + unit * 0.45f
                )
            }
            drawPath(loop, color = color, style = Stroke(width = 2.6f, cap = StrokeCap.Round))
        }

        Accidental.HALF_FLAT -> {
            // Traditional Arabic Sikah / Mirrored Flat (reversed b loop on left of stem + tick)
            val stemTop = y - unit * 1.35f
            val stemBottom = y + unit * 0.45f
            drawLine(
                color = color,
                start = Offset(x + unit * 0.18f, stemTop),
                end = Offset(x + unit * 0.18f, stemBottom),
                strokeWidth = 2.8f,
                cap = StrokeCap.Round
            )
            val loop = Path().apply {
                moveTo(x + unit * 0.18f, y - unit * 0.15f)
                cubicTo(
                    x - unit * 0.42f, y - unit * 0.35f,
                    x - unit * 0.42f, y + unit * 0.25f,
                    x + unit * 0.18f, y + unit * 0.45f
                )
            }
            drawPath(loop, color = color, style = Stroke(width = 2.8f, cap = StrokeCap.Round))
        }

        Accidental.SHARP -> {
            val h = unit * 0.95f
            val w = unit * 0.38f
            // Two vertical lines
            drawLine(color, Offset(x - w * 0.45f, y - h), Offset(x - w * 0.45f, y + h), strokeWidth = 2.4f)
            drawLine(color, Offset(x + w * 0.45f, y - h * 1.1f), Offset(x + w * 0.45f, y + h * 0.9f), strokeWidth = 2.4f)
            // Two slanted crossbars
            drawLine(color, Offset(x - w, y - h * 0.25f), Offset(x + w, y - h * 0.45f), strokeWidth = 3.2f)
            drawLine(color, Offset(x - w, y + h * 0.4f), Offset(x + w, y + h * 0.2f), strokeWidth = 3.2f)
        }

        Accidental.HALF_SHARP -> {
            val h = unit * 0.9f
            val w = unit * 0.34f
            drawLine(color, Offset(x, y - h), Offset(x, y + h), strokeWidth = 2.5f)
            drawLine(color, Offset(x - w, y - h * 0.25f), Offset(x + w, y - h * 0.45f), strokeWidth = 3.0f)
            drawLine(color, Offset(x - w, y + h * 0.4f), Offset(x + w, y + h * 0.2f), strokeWidth = 3.0f)
        }

        Accidental.NATURAL -> Unit
    }
}
