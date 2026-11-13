package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NoteCatalog
import com.example.model.MusicalNote
import com.example.ui.theme.QuarterToneAccent
import com.example.ui.theme.QuarterToneHighlight

@Composable
fun QuarterToneKeyboard(
    highlightedNoteIds: Set<String>,
    tonicNoteId: String?,
    activeNoteId: String?,
    onNoteTap: (MusicalNote) -> Unit,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    val whiteKeys = NoteCatalog.keyboardWhiteNotes
    val upperBlackKeys = NoteCatalog.keyboardUpperKeys
    val quarterToneKeys = NoteCatalog.keyboardQuarterToneKeys

    val whiteKeyWidth = if (compactMode) 46.dp else 52.dp
    val totalHeight = if (compactMode) 156.dp else 192.dp
    val blackKeyWidth = if (compactMode) 30.dp else 34.dp
    val blackKeyHeight = if (compactMode) 92.dp else 112.dp
    val qtKeyWidth = if (compactMode) 28.dp else 32.dp
    val qtKeyHeight = if (compactMode) 68.dp else 82.dp

    val goldColor = MaterialTheme.colorScheme.primary
    val qtColor = if (isDark) QuarterToneHighlight else QuarterToneAccent
    val whiteKeyBase = if (isDark) Color(0xFFF6EFE2) else Color(0xFFFFFCF7)
    val blackKeyBase = Color(0xFF1F1914)

    val scrollState = rememberScrollState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    RoundedCornerShape(18.dp)
                )
                .padding(10.dp)
                .testTag("quarter_tone_keyboard")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                val totalWidth = whiteKeyWidth * whiteKeys.size
                Box(
                    modifier = Modifier
                        .width(totalWidth)
                        .height(totalHeight)
                ) {
                    // 1. White Natural Keys
                    Row(modifier = Modifier.matchParentSize()) {
                        whiteKeys.forEach { note ->
                            val isActive = note.id == activeNoteId
                            val isTonic = note.id == tonicNoteId
                            val inScale = note.id in highlightedNoteIds

                            val keyBg = when {
                                isActive -> goldColor
                                isTonic -> goldColor.copy(alpha = 0.34f)
                                inScale -> goldColor.copy(alpha = 0.16f)
                                else -> whiteKeyBase
                            }

                            Box(
                                modifier = Modifier
                                    .width(whiteKeyWidth)
                                    .fillMaxHeight()
                                    .padding(horizontal = 1.5.dp)
                                    .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp, topStart = 4.dp, topEnd = 4.dp))
                                    .background(keyBg)
                                    .border(
                                        width = if (isTonic || isActive) 2.dp else 1.dp,
                                        color = if (isTonic || isActive) goldColor else Color(0xFFCBBBA4),
                                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp, topStart = 4.dp, topEnd = 4.dp)
                                    )
                                    .clickable { onNoteTap(note) }
                                    .semantics { contentDescription = "مفتاح ${note.arabicName}" }
                                    .testTag("piano_key_${note.id}"),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    if (isTonic || inScale) {
                                        Box(
                                            modifier = Modifier
                                                .size(if (isTonic) 9.dp else 6.dp)
                                                .clip(CircleShape)
                                                .background(if (isTonic) goldColor else goldColor.copy(alpha = 0.7f))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = note.arabicName
                                            .replace(" قرار", "")
                                            .replace(" جواب", ""),
                                        fontSize = 11.sp,
                                        fontWeight = if (inScale || isTonic) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isActive) Color.White else Color(0xFF2B2016),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = note.westernName,
                                        fontSize = 9.sp,
                                        color = if (isActive) Color.White.copy(alpha = 0.9f) else Color(0xFF7B6B5A)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Standard Semitone Black Keys (Flats / Sharps)
                    upperBlackKeys.forEach { spec ->
                        val note = spec.note
                        val isActive = note.id == activeNoteId
                        val inScale = note.id in highlightedNoteIds
                        val isTonic = note.id == tonicNoteId
                        val xOffset = whiteKeyWidth * spec.positionIndex + (whiteKeyWidth - blackKeyWidth) / 2f

                        Box(
                            modifier = Modifier
                                .offset(x = xOffset, y = 0.dp)
                                .width(blackKeyWidth)
                                .height(blackKeyHeight)
                                .shadow(3.dp, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                                .background(
                                    when {
                                        isActive -> goldColor
                                        isTonic -> goldColor.copy(alpha = 0.85f)
                                        inScale -> Color(0xFF4B3721)
                                        else -> blackKeyBase
                                    }
                                )
                                .border(
                                    width = if (inScale || isActive) 1.5.dp else 0.5.dp,
                                    color = if (inScale || isActive) goldColor else Color(0xFF463B30),
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                                )
                                .clickable { onNoteTap(note) }
                                .semantics { contentDescription = "مفتاح ${note.arabicName}" }
                                .testTag("piano_key_${note.id}"),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                if (inScale) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(goldColor)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                                Text(
                                    text = note.westernName.dropLast(1),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (inScale || isActive) Color.White else Color(0xFFD5C7B3)
                                )
                            }
                        }
                    }

                    // 3. Dedicated Arabic Quarter-Tone (Half-Flat 𝄳) Keys
                    quarterToneKeys.forEach { spec ->
                        val note = spec.note
                        val isActive = note.id == activeNoteId
                        val isTonic = note.id == tonicNoteId
                        val inScale = note.id in highlightedNoteIds
                        val xOffset = whiteKeyWidth * spec.positionIndex + (whiteKeyWidth - qtKeyWidth) / 2f

                        Box(
                            modifier = Modifier
                                .offset(x = xOffset, y = 4.dp)
                                .width(qtKeyWidth)
                                .height(qtKeyHeight)
                                .shadow(4.dp, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isActive -> goldColor
                                        inScale || isTonic -> qtColor
                                        else -> Color(0xFF1F4E4D)
                                    }
                                )
                                .border(
                                    width = if (isTonic || inScale || isActive) 1.8.dp else 1.dp,
                                    color = if (isTonic) goldColor else Color(0xFF9DE6E3).copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onNoteTap(note) }
                                .semantics { contentDescription = "مفتاح ربع تون ${note.arabicName}" }
                                .testTag("piano_key_${note.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "𝄳",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = note.westernName.take(1),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.92f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                KeyboardLegendDot(color = goldColor, label = "درجة الأساس / نغمة المقام")
                KeyboardLegendDot(color = qtColor, label = "مفاتيح الربع تون (سيكاه 𝄳)")
            }
        }
    }
}

@Composable
private fun KeyboardLegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
