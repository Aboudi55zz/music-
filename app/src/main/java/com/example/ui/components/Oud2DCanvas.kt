package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OudCatalog
import com.example.data.OudTuning
import com.example.model.Maqam
import com.example.ui.theme.QuarterToneHighlight
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun Oud2DCanvas(
    tuning: OudTuning,
    guideMaqam: Maqam?,
    showMaqamGuides: Boolean,
    externalPulseTrigger: Long,
    externalPluckedCourse: Int?,
    externalPressedQt: Int?,
    onPluckCourse: (courseIndex: Int, quarterToneSteps: Int, pluckPosRatio: Float, isUpstroke: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val courses = tuning.courses
    val textMeasurer = rememberTextMeasurer()

    // Track per-course pluck timestamps for live standing-wave string vibration
    val pluckTimestamps = remember { mutableStateMapOf<Int, Long>() }
    // Track currently pressed position per course (via neck touch or external playback)
    val activePressedQt = remember { mutableStateMapOf<Int, Int>() }

    var animNowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Sync external sequence/strum trigger into string vibration
    LaunchedEffect(externalPulseTrigger, externalPluckedCourse) {
        if (externalPluckedCourse != null && externalPluckedCourse in courses.indices) {
            pluckTimestamps[externalPluckedCourse] = System.currentTimeMillis()
            if (externalPressedQt != null && externalPressedQt > 0) {
                activePressedQt[externalPluckedCourse] = externalPressedQt
            } else {
                activePressedQt.remove(externalPluckedCourse)
            }
        } else if (externalPulseTrigger > 0L && externalPluckedCourse == -1) {
            // Strum all courses
            val now = System.currentTimeMillis()
            courses.indices.forEach { idx ->
                pluckTimestamps[idx] = now + idx * 24L
                activePressedQt.remove(idx)
            }
        }
    }

    // Drive 60fps animation only while at least one string is still vibrating
    val hasVibratingStrings = pluckTimestamps.values.any { (animNowMs - it) < 1150L }
    LaunchedEffect(hasVibratingStrings, pluckTimestamps.size, externalPulseTrigger) {
        while (true) {
            val now = withFrameMillis { System.currentTimeMillis() }
            animNowMs = now
            val stillMoving = pluckTimestamps.values.any { (now - it) in -200L..1150L }
            if (!stillMoving) break
        }
    }

    // Per-pointer tracking for multi-touch (neck hold + soundbox strum) & smooth gliding
    val pointerHeldCourse = remember { mutableMapOf<PointerId, Pair<Int, Int>>() }
    val pointerLastStrumCourse = remember { mutableMapOf<PointerId, Int>() }
    val pointerLastX = remember { mutableMapOf<PointerId, Float>() }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = "عود عربي ثنائي الأبعاد بستة مسارات وأوتار حقيقية موضح عليها النوتات العربية والأجنبية والصولفيج"
                }
                .testTag("oud_2d_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(tuning, guideMaqam) {
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val nutY = h * 0.135f
                        val bridgeY = h * 0.835f
                        val scaleLen = bridgeY - nutY
                        val neckBottomY = nutY + scaleLen * 0.50f
                        val nutHalfW = min(w * 0.18f, 76.dp.toPx())
                        val bridgeHalfW = min(w * 0.28f, 118.dp.toPx())

                        fun stringX(courseIdx: Int, y: Float): Float {
                            val t = ((y - nutY) / scaleLen).coerceIn(0f, 1f)
                            val halfW = nutHalfW + (bridgeHalfW - nutHalfW) * t
                            val frac = courseIdx / 5f
                            return (w * 0.5f - halfW) + frac * (2f * halfW)
                        }

                        fun nearestCourse(x: Float, y: Float): Int {
                            var bestIdx = 0
                            var bestDist = Float.MAX_VALUE
                            for (i in 0 until 6) {
                                val dist = abs(x - stringX(i, y))
                                if (dist < bestDist) {
                                    bestDist = dist
                                    bestIdx = i
                                }
                            }
                            return bestIdx
                        }

                        fun yToQuarterTone(y: Float): Int {
                            val dispRatio = ((y - nutY) / scaleLen).toDouble()
                            return OudCatalog.quarterTonesFromDisplacementRatio(dispRatio)
                        }

                        fun bodyPluckRatio(y: Float): Float {
                            return ((bridgeY - y) / scaleLen).coerceIn(0.12f, 0.34f)
                        }

                        awaitEachGesture {
                            while (true) {
                                val event = awaitPointerEvent()
                                val now = System.currentTimeMillis()
                                var anyPressed = false

                                for (change in event.changes) {
                                    val pos = change.position
                                    val pid = change.id

                                    if (change.pressed) {
                                        anyPressed = true
                                        val isOnNeck = pos.y in (nutY * 0.82f)..neckBottomY
                                        val isOnBody = pos.y > neckBottomY && pos.y <= bridgeY * 1.08f
                                        val prevX = pointerLastX[pid] ?: pos.x
                                        val isUpstroke = pos.x < prevX - 1.5f
                                        pointerLastX[pid] = pos.x

                                        if (!change.previousPressed) {
                                            // Fresh touch down
                                            val courseIdx = nearestCourse(pos.x, pos.y)
                                            if (isOnNeck) {
                                                val qt = yToQuarterTone(pos.y)
                                                pointerHeldCourse[pid] = courseIdx to qt
                                                activePressedQt[courseIdx] = qt
                                                pluckTimestamps[courseIdx] = now
                                                animNowMs = now
                                                onPluckCourse(courseIdx, qt, 0.21f, false)
                                            } else if (isOnBody) {
                                                pointerLastStrumCourse[pid] = courseIdx
                                                val heldQt = activePressedQt[courseIdx] ?: 0
                                                pluckTimestamps[courseIdx] = now
                                                animNowMs = now
                                                onPluckCourse(courseIdx, heldQt, bodyPluckRatio(pos.y), false)
                                            }
                                        } else {
                                            // Dragging
                                            if (isOnNeck && pid in pointerHeldCourse) {
                                                val prev = pointerHeldCourse[pid]
                                                val courseIdx = nearestCourse(pos.x, pos.y)
                                                val qt = yToQuarterTone(pos.y)
                                                if (prev != null && (prev.first != courseIdx || prev.second != qt)) {
                                                    if (prev.first != courseIdx) {
                                                        activePressedQt.remove(prev.first)
                                                    }
                                                    pointerHeldCourse[pid] = courseIdx to qt
                                                    activePressedQt[courseIdx] = qt
                                                    pluckTimestamps[courseIdx] = now
                                                    animNowMs = now
                                                    onPluckCourse(courseIdx, qt, 0.21f, false)
                                                }
                                            } else if (isOnBody) {
                                                val courseIdx = nearestCourse(pos.x, pos.y)
                                                val lastStrummed = pointerLastStrumCourse[pid]
                                                if (lastStrummed != courseIdx) {
                                                    pointerLastStrumCourse[pid] = courseIdx
                                                    val heldQt = activePressedQt[courseIdx] ?: 0
                                                    pluckTimestamps[courseIdx] = now
                                                    animNowMs = now
                                                    onPluckCourse(courseIdx, heldQt, bodyPluckRatio(pos.y), isUpstroke)
                                                }
                                            }
                                        }
                                        change.consume()
                                    } else if (change.previousPressed) {
                                        // Finger lifted
                                        val held = pointerHeldCourse.remove(pid)
                                        if (held != null) {
                                            activePressedQt.remove(held.first)
                                        }
                                        pointerLastStrumCourse.remove(pid)
                                        pointerLastX.remove(pid)
                                    }
                                }

                                if (!anyPressed) break
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val cx = w * 0.5f

                val pegboxTop = h * 0.015f
                val nutY = h * 0.135f
                val bridgeY = h * 0.835f
                val scaleLen = bridgeY - nutY
                // Authentic luthier proportions: neck-body joint at Perfect 5th (~1/3 scale), mir'a at Octave (1/2 scale)
                val bodyTopY = nutY + scaleLen * 0.333f
                val neckBottomY = nutY + scaleLen * 0.50f
                val bodyBottomY = h * 0.98f

                val nutHalfW = min(w * 0.18f, 76.dp.toPx())
                val bridgeHalfW = min(w * 0.28f, 118.dp.toPx())
                val neckJoinHalfW = nutHalfW + (bridgeHalfW - nutHalfW) * 0.50f
                val bellyHalfW = min(w * 0.46f, 200.dp.toPx())

                fun stringCenterX(courseIdx: Int, y: Float): Float {
                    val t = ((y - nutY) / scaleLen).coerceIn(0f, 1f)
                    val halfW = nutHalfW + (bridgeHalfW - nutHalfW) * t
                    val frac = courseIdx / 5f
                    return (cx - halfW) + frac * (2f * halfW)
                }

                fun qtToY(qt: Int): Float {
                    return nutY + scaleLen * OudCatalog.nutDisplacementRatio(qt.toDouble()).toFloat()
                }

                // 1. Pear-Shaped Soundbox (القصعة)
                val bodyPath = Path().apply {
                    moveTo(cx - neckJoinHalfW * 0.92f, bodyTopY + 12f)
                    cubicTo(
                        cx - bellyHalfW * 0.62f, bodyTopY - 8f,
                        cx - bellyHalfW * 1.05f, h * 0.58f,
                        cx - bellyHalfW * 0.98f, h * 0.74f
                    )
                    cubicTo(
                        cx - bellyHalfW * 0.92f, h * 0.91f,
                        cx - bellyHalfW * 0.42f, bodyBottomY,
                        cx, bodyBottomY
                    )
                    cubicTo(
                        cx + bellyHalfW * 0.42f, bodyBottomY,
                        cx + bellyHalfW * 0.92f, h * 0.91f,
                        cx + bellyHalfW * 0.98f, h * 0.74f
                    )
                    cubicTo(
                        cx + bellyHalfW * 1.05f, h * 0.58f,
                        cx + bellyHalfW * 0.62f, bodyTopY - 8f,
                        cx + neckJoinHalfW * 0.92f, bodyTopY + 12f
                    )
                    close()
                }

                // Subtle outer walnut drop shadow / rim
                drawPath(
                    path = bodyPath,
                    color = Color(0xFF3E2210),
                    style = Stroke(width = 5.dp.toPx())
                )
                drawPath(
                    path = bodyPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE4BE84),
                            Color(0xFFC88D4B),
                            Color(0xFF935624)
                        ),
                        center = Offset(cx, h * 0.66f),
                        radius = bellyHalfW * 1.25f
                    )
                )
                // Inner ornamental purfling line
                drawPath(
                    path = bodyPath,
                    color = Color(0xFF4B2811),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Subtle cedar wood grain vertical lines inside soundbox
                val grainCount = 14
                for (g in -grainCount / 2..grainCount / 2) {
                    val gx = cx + g * (bellyHalfW * 0.11f)
                    drawLine(
                        color = Color(0xFF8B5324).copy(alpha = 0.13f),
                        start = Offset(gx, bodyTopY + 14.dp.toPx()),
                        end = Offset(gx, bodyBottomY - 14.dp.toPx()),
                        strokeWidth = 1f
                    )
                }

                // 2. Three Ornate Shamsiyya Rosettes (الشمسيات الثلاث)
                val mainRosetteR = min(bellyHalfW * 0.25f, h * 0.082f)
                drawOudRosette(
                    center = Offset(cx, h * 0.615f),
                    radius = mainRosetteR,
                    isMain = true
                )
                val sideRosetteR = mainRosetteR * 0.44f
                drawOudRosette(
                    center = Offset(cx - bellyHalfW * 0.48f, h * 0.545f),
                    radius = sideRosetteR,
                    isMain = false
                )
                drawOudRosette(
                    center = Offset(cx + bellyHalfW * 0.48f, h * 0.545f),
                    radius = sideRosetteR,
                    isMain = false
                )

                // 3. Pickguard (الرقمة) between main rosette and bridge
                val rqPath = Path().apply {
                    val rTop = h * 0.715f
                    val rBot = h * 0.805f
                    moveTo(cx - bridgeHalfW * 0.52f, rTop)
                    cubicTo(
                        cx + bridgeHalfW * 0.8f, rTop - 4.dp.toPx(),
                        cx + bridgeHalfW * 0.92f, rBot + 3.dp.toPx(),
                        cx - bridgeHalfW * 0.42f, rBot
                    )
                    cubicTo(
                        cx - bridgeHalfW * 0.72f, (rTop + rBot) * 0.5f,
                        cx - bridgeHalfW * 0.72f, rTop,
                        cx - bridgeHalfW * 0.52f, rTop
                    )
                    close()
                }
                drawPath(
                    path = rqPath,
                    color = Color(0xFF2D190D).copy(alpha = 0.85f)
                )
                drawPath(
                    path = rqPath,
                    color = Color(0xFFE0B15A).copy(alpha = 0.55f),
                    style = Stroke(width = 1.dp.toPx())
                )

                // 4. Bridge (الفرس)
                val bridgeH = 8.dp.toPx()
                drawRoundRect(
                    color = Color(0xFF28160B),
                    topLeft = Offset(cx - bridgeHalfW * 1.18f, bridgeY - bridgeH * 0.5f),
                    size = Size(bridgeHalfW * 2.36f, bridgeH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawLine(
                    color = Color(0xFFF3E4CA),
                    start = Offset(cx - bridgeHalfW * 1.08f, bridgeY - 1f),
                    end = Offset(cx + bridgeHalfW * 1.08f, bridgeY - 1f),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // 5. Pegbox (البنجق) & 11 Wooden Tuning Pegs (المفاتيح)
                val pegTopHalfW = nutHalfW * 0.62f
                val pegboxPath = Path().apply {
                    moveTo(cx - pegTopHalfW, pegboxTop)
                    lineTo(cx + pegTopHalfW, pegboxTop)
                    lineTo(cx + nutHalfW * 1.05f, nutY)
                    lineTo(cx - nutHalfW * 1.05f, nutY)
                    close()
                }
                drawPath(
                    path = pegboxPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF3A2112), Color(0xFF21120A))
                    )
                )
                drawPath(
                    path = pegboxPath,
                    color = Color(0xFFD6A85A).copy(alpha = 0.45f),
                    style = Stroke(width = 1.dp.toPx())
                )
                // Tuning pegs (5 on left, 6 on right = 11 real pegs for 11 real strings!)
                drawTuningPegs(cx, pegboxTop, nutY, pegTopHalfW, nutHalfW)

                // 6. Fretless Fingerboard (الزند)
                val neckPath = Path().apply {
                    moveTo(cx - nutHalfW * 1.06f, nutY)
                    lineTo(cx + nutHalfW * 1.06f, nutY)
                    lineTo(cx + neckJoinHalfW * 1.06f, neckBottomY)
                    lineTo(cx - neckJoinHalfW * 1.06f, neckBottomY)
                    close()
                }
                drawPath(
                    path = neckPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF21150E), Color(0xFF2E1D13), Color(0xFF24160E))
                    )
                )
                drawPath(
                    path = neckPath,
                    color = Color(0xFFE0B15A).copy(alpha = 0.55f),
                    style = Stroke(width = 1.2.dp.toPx())
                )

                // Authentic logarithmically spaced acoustic landmark lines (Whole tone 4, Sikah 7, 4th 10, 5th 14, 6th 18, Octave 24)
                for (qtMark in listOf(4, 7, 10, 14, 18, 24)) {
                    val ly = qtToY(qtMark)
                    val t = (ly - nutY) / (neckBottomY - nutY)
                    val hw = nutHalfW * 1.02f + (neckJoinHalfW - nutHalfW) * t
                    val isMajorLandmark = qtMark == 10 || qtMark == 14 || qtMark == 24
                    drawLine(
                        color = if (qtMark == 7) {
                            QuarterToneHighlight.copy(alpha = 0.28f)
                        } else {
                            Color(0xFFE2B052).copy(alpha = if (isMajorLandmark) 0.26f else 0.14f)
                        },
                        start = Offset(cx - hw, ly),
                        end = Offset(cx + hw, ly),
                        strokeWidth = if (isMajorLandmark) 1.2.dp.toPx() else 0.8.dp.toPx()
                    )
                }

                // Ivory Nut (الأنف)
                drawRoundRect(
                    color = Color(0xFFF6EAD4),
                    topLeft = Offset(cx - nutHalfW * 1.08f, nutY - 2.5.dp.toPx()),
                    size = Size(nutHalfW * 2.16f, 4.5.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )

                // 7. Maqam Fingerboard Markers (مواقع نغمات المقام المختار على الزند مع أسماء النغمات)
                if (showMaqamGuides && guideMaqam != null) {
                    courses.forEachIndexed { cIdx, course ->
                        val markers = OudCatalog.getMaqamMarkersForCourse(cIdx, course, guideMaqam)
                        markers.forEach { marker ->
                            val my = qtToY(marker.quarterToneStepsFromNut)
                            val mx = stringCenterX(cIdx, my)
                            val dotColor = when {
                                marker.isTonic -> Color(0xFFE2B052)
                                marker.fingerNote.isQuarterTone -> QuarterToneHighlight
                                else -> Color(0xFFF3E4CA).copy(alpha = 0.82f)
                            }
                            val radius = if (marker.isTonic || marker.fingerNote.isQuarterTone) 4.dp.toPx() else 3.dp.toPx()
                            drawCircle(
                                color = dotColor.copy(alpha = 0.30f),
                                radius = radius + 2.dp.toPx(),
                                center = Offset(mx, my)
                            )
                            drawCircle(
                                color = dotColor,
                                radius = radius,
                                center = Offset(mx, my)
                            )

                            // Compact note label right beside/above non-nut markers (Arabic Solfege + Western)
                            if (marker.quarterToneStepsFromNut > 0) {
                                val markerLabel = "${marker.fingerNote.arabicName}\n${marker.fingerNote.westernShort}"
                                val measured = textMeasurer.measure(
                                    text = markerLabel,
                                    style = TextStyle(
                                        color = if (marker.isTonic || marker.fingerNote.isQuarterTone) {
                                            Color(0xFF1A0E07)
                                        } else {
                                            Color(0xFFF6EAD4)
                                        },
                                        fontSize = 6.5.sp,
                                        lineHeight = 7.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 2
                                )
                                val pillW = (measured.size.width + 4.dp.toPx()).coerceAtMost(24.dp.toPx())
                                val pillH = measured.size.height + 2.dp.toPx()
                                val pillTop = (my - pillH * 0.5f).coerceIn(nutY + 2.dp.toPx(), neckBottomY - pillH)
                                drawRoundRect(
                                    color = if (marker.isTonic) {
                                        Color(0xFFE2B052).copy(alpha = 0.94f)
                                    } else if (marker.fingerNote.isQuarterTone) {
                                        QuarterToneHighlight.copy(alpha = 0.92f)
                                    } else {
                                        Color(0xFF28180E).copy(alpha = 0.88f)
                                    },
                                    topLeft = Offset(mx - pillW * 0.5f, pillTop),
                                    size = Size(pillW, pillH),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                )
                                drawText(
                                    textLayoutResult = measured,
                                    topLeft = Offset(
                                        mx - measured.size.width * 0.5f,
                                        pillTop + 1.dp.toPx()
                                    )
                                )
                            }
                        }
                    }
                }

                // 8. Draw All 11 Real Strings (6 Courses: 1 single copper-wound bass + 2 wound bass pairs + 3 nylon pairs) with Live Vibration!
                courses.forEachIndexed { cIdx, course ->
                    val topX = stringCenterX(cIdx, nutY)
                    val botX = stringCenterX(cIdx, bridgeY)
                    val pressedQt = activePressedQt[cIdx]
                    val vibStartY = if (pressedQt != null && pressedQt > 0) qtToY(pressedQt) else nutY

                    val pluckTime = pluckTimestamps[cIdx]
                    val elapsedMs = if (pluckTime != null) animNowMs - pluckTime else 9999L
                    val isVibrating = elapsedMs in 0L..1250L
                    val vibAmp = if (isVibrating) {
                        val sec = elapsedMs / 1000.0
                        val envelope = exp(-sec * (if (course.isWoundBass) 3.4 else 4.4))
                        val wave = sin(sec * (24.0 + cIdx * 5.5))
                        ((if (course.isWoundBass) 5.2f else 4.0f).dp.toPx() * envelope * wave).toFloat()
                    } else 0f

                    val stringColor = when {
                        isVibrating -> Color(0xFFFFE6A7)
                        cIdx == 0 -> Color(0xFFD6A06B) // Single copper/bronze wound Bam bass
                        course.isWoundBass -> Color(0xFFD9C39E) // Wound silver/bronze bass courses (F2, A2)
                        else -> Color(0xFFF6EFE2).copy(alpha = 0.92f) // Clear nylon treble courses (D3, G3, C4)
                    }

                    val strokePx = (course.thicknessDp * 0.56f).dp.toPx()
                    val nutPairSpread = 1.35.dp.toPx()
                    val bridgePairSpread = 1.95.dp.toPx()
                    val offsets = if (course.isDoubleCourse) listOf(-1f, 1f) else listOf(0f)
                    offsets.forEach { side ->
                        val sx = topX + side * nutPairSpread
                        val ex = botX + side * bridgePairSpread

                        // Upper pegbox string segment
                        drawLine(
                            color = stringColor.copy(alpha = 0.65f),
                            start = Offset(cx + (cIdx - 2.5f) * 4.dp.toPx() + side * 0.6.dp.toPx(), pegboxTop + 7.dp.toPx()),
                            end = Offset(sx, nutY),
                            strokeWidth = strokePx * 0.85f
                        )

                        if (!isVibrating) {
                            drawLine(
                                color = stringColor,
                                start = Offset(sx, nutY),
                                end = Offset(ex, bridgeY),
                                strokeWidth = strokePx
                            )
                        } else {
                            // Unvibrating upper portion (if pressed on fingerboard)
                            val tPress = (vibStartY - nutY) / (bridgeY - nutY)
                            val pressX = sx + (ex - sx) * tPress
                            if (vibStartY > nutY + 2f) {
                                drawLine(
                                    color = stringColor,
                                    start = Offset(sx, nutY),
                                    end = Offset(pressX, vibStartY),
                                    strokeWidth = strokePx
                                )
                            }
                            // Vibrating lower segment (standing wave curve)
                            val midX = (pressX + ex) * 0.5f + vibAmp
                            val midY = (vibStartY + bridgeY) * 0.5f
                            val vibPath = Path().apply {
                                moveTo(pressX, vibStartY)
                                quadraticTo(midX, midY, ex, bridgeY)
                            }
                            drawPath(
                                path = vibPath,
                                color = stringColor,
                                style = Stroke(width = strokePx + 0.4.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Ghost blur shadow for realistic string oscillation
                            val ghostPath = Path().apply {
                                moveTo(pressX, vibStartY)
                                quadraticTo((pressX + ex) * 0.5f - vibAmp * 0.7f, midY, ex, bridgeY)
                            }
                            drawPath(
                                path = ghostPath,
                                color = Color(0xFFE2B052).copy(alpha = 0.35f),
                                style = Stroke(width = strokePx, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // 9. Per-String Open Note Badge Directly Below Bridge (Traditional Arabic + Solfege + Western)
                    val badgeTopY = bridgeY + 7.dp.toPx()
                    val maxBadgeW = (bridgeHalfW * 2f / 5.3f).coerceAtMost(44.dp.toPx())
                    val solfegeLayout = textMeasurer.measure(
                        text = course.openSolfegeName,
                        style = TextStyle(
                            color = if (isVibrating) Color(0xFFFFE6A7) else Color(0xFFF6EAD4),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1
                    )
                    val westernLayout = textMeasurer.measure(
                        text = course.openWesternName,
                        style = TextStyle(
                            color = Color(0xFFE2B052),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1
                    )
                    val arabicLayout = textMeasurer.measure(
                        text = course.shortArabicName,
                        style = TextStyle(
                            color = Color(0xFFD6C2A3),
                            fontSize = 6.5.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val badgeH = solfegeLayout.size.height + westernLayout.size.height + arabicLayout.size.height + 5.dp.toPx()
                    val badgeLeft = botX - maxBadgeW * 0.5f
                    drawRoundRect(
                        color = if (isVibrating) Color(0xFF422512).copy(alpha = 0.96f) else Color(0xFF1F120A).copy(alpha = 0.90f),
                        topLeft = Offset(badgeLeft, badgeTopY),
                        size = Size(maxBadgeW, badgeH),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                    drawRoundRect(
                        color = if (isVibrating) Color(0xFFE2B052) else Color(0xFFE2B052).copy(alpha = 0.42f),
                        topLeft = Offset(badgeLeft, badgeTopY),
                        size = Size(maxBadgeW, badgeH),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                        style = Stroke(width = if (isVibrating) 1.4.dp.toPx() else 0.8.dp.toPx())
                    )
                    val line1Y = badgeTopY + 2.dp.toPx()
                    val line2Y = line1Y + solfegeLayout.size.height
                    val line3Y = line2Y + westernLayout.size.height
                    drawText(
                        textLayoutResult = solfegeLayout,
                        topLeft = Offset(botX - solfegeLayout.size.width * 0.5f, line1Y)
                    )
                    drawText(
                        textLayoutResult = westernLayout,
                        topLeft = Offset(botX - westernLayout.size.width * 0.5f, line2Y)
                    )
                    drawText(
                        textLayoutResult = arabicLayout,
                        topLeft = Offset(
                            botX - min(arabicLayout.size.width.toFloat(), maxBadgeW - 2.dp.toPx()) * 0.5f,
                            line3Y
                        )
                    )

                    // Active pressed finger glow + floating 3-system note callout
                    if (pressedQt != null) {
                        val py = qtToY(pressedQt)
                        val px = stringCenterX(cIdx, py)
                        drawCircle(
                            color = Color(0xFFE2B052).copy(alpha = 0.35f),
                            radius = 10.dp.toPx(),
                            center = Offset(px, py)
                        )
                        drawCircle(
                            color = Color(0xFFE2B052),
                            radius = 5.5.dp.toPx(),
                            center = Offset(px, py)
                        )

                        val pressedNote = OudCatalog.noteAtPosition(cIdx, course, pressedQt)
                        val calloutText = "${pressedNote.arabicName} • ${pressedNote.maqamPitchName} • ${pressedNote.westernName}"
                        val calloutLayout = textMeasurer.measure(
                            text = calloutText,
                            style = TextStyle(
                                color = Color(0xFF1A0E07),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1
                        )
                        val calloutW = calloutLayout.size.width + 12.dp.toPx()
                        val calloutH = calloutLayout.size.height + 6.dp.toPx()
                        val calloutX = (px - calloutW * 0.5f).coerceIn(4.dp.toPx(), w - calloutW - 4.dp.toPx())
                        val calloutY = (py - calloutH - 10.dp.toPx()).coerceAtLeast(4.dp.toPx())
                        drawRoundRect(
                            color = if (pressedNote.isQuarterTone) QuarterToneHighlight else Color(0xFFE2B052),
                            topLeft = Offset(calloutX, calloutY),
                            size = Size(calloutW, calloutH),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                        drawRoundRect(
                            color = Color(0xFF1F120A),
                            topLeft = Offset(calloutX, calloutY),
                            size = Size(calloutW, calloutH),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawText(
                            textLayoutResult = calloutLayout,
                            topLeft = Offset(calloutX + 6.dp.toPx(), calloutY + 3.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawOudRosette(
    center: Offset,
    radius: Float,
    isMain: Boolean
) {
    // Deep acoustic soundhole
    drawCircle(
        color = Color(0xFF1A0E07),
        radius = radius,
        center = center
    )
    // Ornate carved golden wood frame
    drawCircle(
        color = Color(0xFFE2B052),
        radius = radius,
        center = center,
        style = Stroke(width = if (isMain) 1.8.dp.toPx() else 1.2.dp.toPx())
    )
    drawCircle(
        color = Color(0xFF6B3C1A),
        radius = radius * 0.84f,
        center = center,
        style = Stroke(width = if (isMain) 1.2.dp.toPx() else 0.9.dp.toPx())
    )

    // Geometric 8-pointed Arabesque rosette lattice
    val spokes = if (isMain) 12 else 8
    for (i in 0 until spokes) {
        val angle = (2.0 * PI * i / spokes).toFloat()
        val x2 = center.x + cos(angle) * radius * 0.88f
        val y2 = center.y + sin(angle) * radius * 0.88f
        drawLine(
            color = Color(0xFFD6A85A).copy(alpha = 0.78f),
            start = center,
            end = Offset(x2, y2),
            strokeWidth = if (isMain) 1.0.dp.toPx() else 0.7.dp.toPx()
        )
    }

    // Inner star polygon
    val starPts = if (isMain) 8 else 6
    val starPath = Path()
    for (i in 0..starPts) {
        val a = (2.0 * PI * i / starPts).toFloat()
        val r = if (i % 2 == 0) radius * 0.62f else radius * 0.36f
        val px = center.x + cos(a) * r
        val py = center.y + sin(a) * r
        if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
    }
    starPath.close()
    drawPath(
        path = starPath,
        color = Color(0xFFE4BE84).copy(alpha = 0.85f),
        style = Stroke(width = if (isMain) 1.0.dp.toPx() else 0.7.dp.toPx())
    )
    drawCircle(
        color = Color(0xFFE2B052).copy(alpha = 0.75f),
        radius = radius * 0.18f,
        center = center
    )
}

private fun DrawScope.drawTuningPegs(
    cx: Float,
    pegboxTop: Float,
    nutY: Float,
    topHalfW: Float,
    nutHalfW: Float
) {
    val leftCount = 5
    val rightCount = 6
    val pegColor = Color(0xFF4B2B17)
    val pegHighlight = Color(0xFFD6A85A)
    val pegLen = 9.dp.toPx()
    val pegHeadDist = 11.dp.toPx()
    val pegLift = 2.dp.toPx()
    val pegRadius = 3.2.dp.toPx()
    val pegStroke = 2.2.dp.toPx()

    for (i in 0 until leftCount) {
        val t = (i + 0.6f) / leftCount
        val py = pegboxTop + t * (nutY - pegboxTop - 4.dp.toPx())
        val edgeX = cx - (topHalfW + (nutHalfW - topHalfW) * t)
        drawLine(
            color = pegColor,
            start = Offset(edgeX, py),
            end = Offset(edgeX - pegLen, py - pegLift),
            strokeWidth = pegStroke,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = pegColor,
            radius = pegRadius,
            center = Offset(edgeX - pegHeadDist, py - pegLift)
        )
        drawCircle(
            color = pegHighlight.copy(alpha = 0.5f),
            radius = 1.2.dp.toPx(),
            center = Offset(edgeX - pegHeadDist, py - pegLift)
        )
    }

    for (i in 0 until rightCount) {
        val t = (i + 0.5f) / rightCount
        val py = pegboxTop + t * (nutY - pegboxTop - 3.dp.toPx())
        val edgeX = cx + (topHalfW + (nutHalfW - topHalfW) * t)
        drawLine(
            color = pegColor,
            start = Offset(edgeX, py),
            end = Offset(edgeX + pegLen, py - pegLift),
            strokeWidth = pegStroke,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = pegColor,
            radius = pegRadius,
            center = Offset(edgeX + pegHeadDist, py - pegLift)
        )
        drawCircle(
            color = pegHighlight.copy(alpha = 0.5f),
            radius = 1.2.dp.toPx(),
            center = Offset(edgeX + pegHeadDist, py - pegLift)
        )
    }
}
