package com.example.data

import com.example.model.Accidental
import com.example.model.Maqam
import com.example.model.MaqamFamily
import com.example.model.MusicalNote
import kotlin.math.abs
import kotlin.math.pow

data class OudCourse(
    val courseNumber: Int, // 6 (bass) down to 1 (treble)
    val traditionalName: String,
    val shortArabicName: String,
    val openSolfegeName: String,
    val openArabicName: String,
    val openWesternName: String,
    val openQuarterToneOffset: Int, // relative to C4 = 0
    val isDoubleCourse: Boolean,
    val thicknessDp: Float
) {
    val openFrequencyHz: Double
        get() = 440.0 * 2.0.pow((openQuarterToneOffset - 18) / 24.0)

    val isWoundBass: Boolean
        get() = courseNumber >= 4
}

enum class OudTuning(
    val arabicName: String,
    val shortSummary: String,
    val courses: List<OudCourse> // Ordered Left -> Right (Bass 6 -> Treble 1)
) {
    ARABIC_STANDARD(
        arabicName = "الوزن العربي القياسي",
        shortSummary = "دو - فا - لا - ري - صول - دو",
        courses = listOf(
            OudCourse(6, "البَم (قرار الدو)", "بَم", "دو", "دو₂", "C2", -48, isDoubleCourse = false, thicknessDp = 3.4f),
            OudCourse(5, "قرار الجهاركاه", "قرار فا", "فا", "فا₂", "F2", -38, isDoubleCourse = true, thicknessDp = 2.8f),
            OudCourse(4, "العُشيران", "عُشيران", "لا", "لا₂", "A2", -30, isDoubleCourse = true, thicknessDp = 2.4f),
            OudCourse(3, "الدوكاه", "دوكاه", "ري", "ري₃", "D3", -20, isDoubleCourse = true, thicknessDp = 1.9f),
            OudCourse(2, "النوى", "نوى", "صول", "صول₃", "G3", -10, isDoubleCourse = true, thicknessDp = 1.6f),
            OudCourse(1, "الكردان (الشرارة)", "كردان", "دو", "دو₄", "C4", 0, isDoubleCourse = true, thicknessDp = 1.3f)
        )
    ),
    IRAQI_TUNING(
        arabicName = "الوزن العراقي",
        shortSummary = "ري - صول - لا - ري - صول - دو",
        courses = listOf(
            OudCourse(6, "البَم (قرار الري)", "بَم", "ري", "ري₂", "D2", -44, isDoubleCourse = false, thicknessDp = 3.4f),
            OudCourse(5, "اليَكاه", "يَكاه", "صول", "صول₂", "G2", -34, isDoubleCourse = true, thicknessDp = 2.8f),
            OudCourse(4, "العُشيران", "عُشيران", "لا", "لا₂", "A2", -30, isDoubleCourse = true, thicknessDp = 2.4f),
            OudCourse(3, "الدوكاه", "دوكاه", "ري", "ري₃", "D3", -20, isDoubleCourse = true, thicknessDp = 1.9f),
            OudCourse(2, "النوى", "نوى", "صول", "صول₃", "G3", -10, isDoubleCourse = true, thicknessDp = 1.6f),
            OudCourse(1, "الكردان", "كردان", "دو", "دو₄", "C4", 0, isDoubleCourse = true, thicknessDp = 1.3f)
        )
    ),
    SHARQI_HIGH(
        arabicName = "الوزن التركي / المرتفع",
        shortSummary = "مي - لا - سي - مي - لا - ري",
        courses = listOf(
            OudCourse(6, "البَم المرتفع", "بَم", "مي", "مي₂", "E2", -40, isDoubleCourse = false, thicknessDp = 3.4f),
            OudCourse(5, "العُشيران", "عُشيران", "لا", "لا₂", "A2", -30, isDoubleCourse = true, thicknessDp = 2.8f),
            OudCourse(4, "القوشت", "قوشت", "سي", "سي₂", "B2", -26, isDoubleCourse = true, thicknessDp = 2.4f),
            OudCourse(3, "البوسليك", "بوسليك", "مي", "مي₃", "E3", -16, isDoubleCourse = true, thicknessDp = 1.9f),
            OudCourse(2, "الحسيني", "حسيني", "لا", "لا₃", "A3", -6, isDoubleCourse = true, thicknessDp = 1.6f),
            OudCourse(1, "المحيّر", "محيّر", "ري", "ري₄", "D4", 4, isDoubleCourse = true, thicknessDp = 1.3f)
        )
    )
}

data class OudFingerNote(
    val courseIndex: Int,
    val quarterToneStepsFromNut: Int,
    val absoluteQuarterToneOffset: Int,
    val arabicName: String,      // Solfege: دو، ري، مي𝄳...
    val maqamPitchName: String,  // Traditional Arabic: راست، دوكاه، سيكاه، يكاه، عشيران...
    val westernName: String,     // Western with octave: C4, D3, E𝄳3...
    val westernShort: String,    // Western without octave: C, D, E𝄳...
    val accidental: Accidental,
    val frequencyHz: Double
) {
    val isQuarterTone: Boolean
        get() = accidental == Accidental.HALF_FLAT || accidental == Accidental.HALF_SHARP
}

data class OudNeckMarker(
    val quarterToneStepsFromNut: Int,
    val fingerNote: OudFingerNote,
    val isTonic: Boolean
)

data class OudFingeringStep(
    val stepNumber: Int,
    val note: MusicalNote,
    val courseIndex: Int,
    val course: OudCourse,
    val quarterToneSteps: Int,
    val fingerName: String,
    val positionLabel: String,
    val oudNote: OudFingerNote
)

data class OudMaqamGuide(
    val maqam: Maqam,
    val tuning: OudTuning,
    val steps: List<OudFingeringStep>,
    val summaryTip: String,
    val rishaTip: String
)

object OudCatalog {
    // Full octave from nut (0) to mir'a octave midpoint (24 = half the vibrating string length)
    const val MAX_NECK_QUARTER_TONES = 24

    /**
     * Exact acoustic vibrating-string displacement ratio from the nut (0.0 at nut, 0.5 at octave, 1.0 at bridge).
     * L(q) = L0 * 2^(-q/24)  =>  displacementRatio(q) = 1 - 2^(-q/24).
     */
    fun nutDisplacementRatio(quarterToneSteps: Double): Double {
        val clamped = quarterToneSteps.coerceIn(0.0, MAX_NECK_QUARTER_TONES.toDouble())
        return 1.0 - 2.0.pow(-clamped / 24.0)
    }

    /**
     * Exact inverse acoustic law converting a physical position ratio along the scale length (nut->bridge)
     * into quarter-tone steps from the nut.
     */
    fun quarterTonesFromDisplacementRatio(displacementRatio: Double): Int {
        val safeRatio = displacementRatio.coerceIn(0.0, 0.5)
        val qt = -24.0 * kotlin.math.log2(1.0 - safeRatio)
        return qt.toInt().let { floorQt ->
            if ((qt - floorQt) >= 0.5) floorQt + 1 else floorQt
        }.coerceIn(0, MAX_NECK_QUARTER_TONES)
    }

    fun intervalLabelBetween(lowerCourse: OudCourse, upperCourse: OudCourse): String {
        val qtDiff = upperCourse.openQuarterToneOffset - lowerCourse.openQuarterToneOffset
        return when (qtDiff) {
            2 -> "+١/٢ ثانية صغيرة"
            4 -> "+٢ ثانية كبيرة"
            6 -> "+٣ ثالثة صغيرة"
            8 -> "+٣ ثالثة كبيرة"
            10 -> "+٤ رابعة تامة"
            12 -> "+٤# رابعة زائدة"
            14 -> "+٥ خامسة تامة"
            else -> "+${qtDiff / 2.0} بُعد"
        }
    }

    private data class PitchLabels(
        val solfege: String,
        val western: String,
        val traditional: String
    )

    private val pitchClassInfo = mapOf(
        0 to PitchLabels("دو", "C", "راست"),
        1 to PitchLabels("دو𝄲", "C𝄲", "تيك راست"),
        2 to PitchLabels("ري♭", "D♭", "زيركولة"),
        3 to PitchLabels("ري𝄳", "D𝄳", "نيم زيركولة"),
        4 to PitchLabels("ري", "D", "دوكاه"),
        5 to PitchLabels("ري𝄲", "D𝄲", "نيم كردي"),
        6 to PitchLabels("مي♭", "E♭", "كرد"),
        7 to PitchLabels("مي𝄳", "E𝄳", "سيكاه"),
        8 to PitchLabels("مي", "E", "بوسليك"),
        9 to PitchLabels("مي𝄲", "E𝄲", "تيك بوسليك"),
        10 to PitchLabels("فا", "F", "جهاركاه"),
        11 to PitchLabels("فا𝄲", "F𝄲", "تيك جهاركاه"),
        12 to PitchLabels("فا♯", "F♯", "حجاز"),
        13 to PitchLabels("صول𝄳", "G𝄳", "نيم حجاز"),
        14 to PitchLabels("صول", "G", "نوى"),
        15 to PitchLabels("صول𝄲", "G𝄲", "تيك نوى"),
        16 to PitchLabels("لا♭", "A♭", "حصار"),
        17 to PitchLabels("لا𝄳", "A𝄳", "تيك حصار"),
        18 to PitchLabels("لا", "A", "حسيني"),
        19 to PitchLabels("لا𝄲", "A𝄲", "تيك حسيني"),
        20 to PitchLabels("سي♭", "B♭", "عجم"),
        21 to PitchLabels("سي𝄳", "B𝄳", "أوج"),
        22 to PitchLabels("سي", "B", "ماهور"),
        23 to PitchLabels("سي𝄲", "B𝄲", "تيك ماهور")
    )

    private fun traditionalDegreeName(absOffset: Int, baseTraditional: String): String {
        return when (absOffset) {
            -48 -> "بَم (قرار راست)"
            -44 -> "بَم (قرار دوكاه)"
            -40 -> "بَم (قرار بوسليك)"
            -38 -> "قرار جهاركاه"
            -34 -> "قرار يكاه"
            -30 -> "عُشيران (أوكتاف منخفض)"
            -26 -> "قوشت قرار"
            -24 -> "راست قرار"
            -20 -> "دوكاه قرار"
            -18 -> "كرد قرار"
            -17 -> "سيكاه قرار"
            -16 -> "بوسليك قرار"
            -14 -> "جهاركاه قرار"
            -12 -> "حجاز قرار"
            -10 -> "يكاه"
            -8 -> "نيم حصار قرار"
            -6 -> "عُشيران"
            -4 -> "عجم عشيران"
            -3 -> "عراق"
            -2 -> "قوشت"
            24 -> "كردان"
            26 -> "شهناز"
            28 -> "محيّر"
            30 -> "سنبلة"
            31 -> "بزرك"
            32 -> "جواب بوسليك"
            34 -> "ماهوران"
            36 -> "جواب حجاز"
            38 -> "سهم"
            else -> when {
                absOffset < -24 -> "$baseTraditional (بَم)"
                absOffset < 0 -> "$baseTraditional قرار"
                absOffset >= 24 -> "جواب $baseTraditional"
                else -> baseTraditional
            }
        }
    }

    fun noteAtPosition(
        courseIndex: Int,
        course: OudCourse,
        quarterToneStepsFromNut: Int
    ): OudFingerNote {
        val steps = quarterToneStepsFromNut.coerceIn(0, MAX_NECK_QUARTER_TONES)
        val absOffset = course.openQuarterToneOffset + steps
        val pitchClass = ((absOffset % 24) + 24) % 24
        val octave = 4 + Math.floorDiv(absOffset, 24)
        val info = pitchClassInfo[pitchClass] ?: PitchLabels("نغمة", "N", "درجة")

        val accidental = when (pitchClass) {
            3, 7, 13, 17, 21 -> Accidental.HALF_FLAT
            1, 5, 9, 11, 15, 19, 23 -> Accidental.HALF_SHARP
            2, 6, 16, 20 -> Accidental.FLAT
            12 -> Accidental.SHARP
            else -> Accidental.NATURAL
        }

        val freq = 440.0 * 2.0.pow((absOffset - 18) / 24.0)
        return OudFingerNote(
            courseIndex = courseIndex,
            quarterToneStepsFromNut = steps,
            absoluteQuarterToneOffset = absOffset,
            arabicName = info.solfege,
            maqamPitchName = traditionalDegreeName(absOffset, info.traditional),
            westernName = "${info.western}$octave",
            westernShort = info.western,
            accidental = accidental,
            frequencyHz = freq
        )
    }

    fun getMaqamMarkersForCourse(
        courseIndex: Int,
        course: OudCourse,
        maqam: Maqam?
    ): List<OudNeckMarker> {
        if (maqam == null) return emptyList()
        val scalePitchClasses = maqam.ascendingScale.map { ((it.quarterToneOffset % 24) + 24) % 24 }.toSet()
        val tonicPitchClass = maqam.ascendingScale.firstOrNull()?.let { ((it.quarterToneOffset % 24) + 24) % 24 }

        val markers = mutableListOf<OudNeckMarker>()
        for (qt in 0..MAX_NECK_QUARTER_TONES) {
            val absOffset = course.openQuarterToneOffset + qt
            val pc = ((absOffset % 24) + 24) % 24
            if (pc in scalePitchClasses) {
                val note = noteAtPosition(courseIndex, course, qt)
                markers.add(
                    OudNeckMarker(
                        quarterToneStepsFromNut = qt,
                        fingerNote = note,
                        isTonic = pc == tonicPitchClass
                    )
                )
            }
        }
        return markers
    }

    fun buildMaqamOudGuide(maqam: Maqam, tuning: OudTuning): OudMaqamGuide {
        val courses = tuning.courses
        val scale = maqam.ascendingScale
        val steps = mutableListOf<OudFingeringStep>()

        var previousCourseIdx = 1

        scale.forEachIndexed { index, note ->
            val targetOffsets = listOf(
                note.quarterToneOffset - 24, // Real sounding register of Arabic Oud (1 octave below written treble)
                note.quarterToneOffset - 48,
                note.quarterToneOffset
            )

            data class Candidate(val cIdx: Int, val qt: Int, val score: Int)
            var best: Candidate? = null

            for ((octavePenaltyIdx, targetOff) in targetOffsets.withIndex()) {
                for (cIdx in courses.indices) {
                    val c = courses[cIdx]
                    val qt = targetOff - c.openQuarterToneOffset
                    if (qt in 0..MAX_NECK_QUARTER_TONES) {
                        var score = octavePenaltyIdx * 45
                        score += when {
                            qt == 0 -> 0
                            qt <= 10 -> qt + 3 // Authentic 1st position (nut to 4th finger)
                            qt <= 14 -> qt + 14 // 2nd position (up to 5th)
                            else -> qt + 28 // Upper mir'a extension
                        }
                        score += abs(cIdx - previousCourseIdx) * 2
                        if (best == null || score < best.score) {
                            best = Candidate(cIdx, qt, score)
                        }
                    }
                }
                if (best != null && octavePenaltyIdx == 0 && best.qt <= 14) break
            }

            if (best == null) {
                val targetPc = ((note.quarterToneOffset % 24) + 24) % 24
                for (cIdx in courses.indices) {
                    for (qt in 0..MAX_NECK_QUARTER_TONES) {
                        val pc = (((courses[cIdx].openQuarterToneOffset + qt) % 24) + 24) % 24
                        if (pc == targetPc) {
                            best = Candidate(cIdx, qt, qt + 100)
                            break
                        }
                    }
                    if (best != null) break
                }
            }

            val chosen = best ?: Candidate(courses.lastIndex, 0, 999)
            previousCourseIdx = chosen.cIdx
            val course = courses[chosen.cIdx]
            val fingerNote = noteAtPosition(chosen.cIdx, course, chosen.qt)
            val (fingerName, posDesc) = describeOudFinger(chosen.qt, fingerNote.isQuarterTone)

            steps.add(
                OudFingeringStep(
                    stepNumber = index + 1,
                    note = note,
                    courseIndex = chosen.cIdx,
                    course = course,
                    quarterToneSteps = chosen.qt,
                    fingerName = fingerName,
                    positionLabel = posDesc,
                    oudNote = fingerNote
                )
            )
        }

        val firstStep = steps.firstOrNull()
        val qtSteps = steps.filter { it.oudNote.isQuarterTone }
        val summaryTip = buildString {
            if (firstStep != null) {
                append("يبدأ العزف وتستقر جملة ${maqam.name} على ${firstStep.course.traditionalName} (${firstStep.fingerName}). ")
            }
            if (qtSteps.isNotEmpty()) {
                val qtNames = qtSteps.distinctBy { it.oudNote.arabicName }.joinToString(" و") {
                    "${it.oudNote.arabicName} (${it.oudNote.westernShort}) على ${it.course.traditionalName}"
                }
                append("الموضع المميز للربع تون يكون في $qtNames بضبط الأصبع في منتصف المسافة الصوتية بدقة.")
            } else {
                append("هذا المقام خالٍ من الربع تون، لذا تقع جميع الأصابع على مواضع الأنصاف والأبعاد الكاملة وفق طول الوتر الطبيعي.")
            }
        }

        val rishaTip = when {
            maqam.hasQuarterTone ->
                "استخدم ريشة هابطة واضحة على نغمة الركوز ودرجة الربع تون، وريشة صاعدة خفيفة في الزخارف الانتقالية لإبراز الشجن الشرقي."
            maqam.family == MaqamFamily.HIJAZ || maqam.family == MaqamFamily.NIKRIZ ->
                "أبرِز القفزة اللحنية (البُعد ونصف) بريشة رشيقة متصلة، مع ترك الوتر يرنّ لثانية على نغمة الغمّاز (${maqam.ghammazName})."
            else ->
                "احرص على تناوب الريشة (هابطة ⌄ ثم صاعدة ⌃) في الجمل السريعة، واضغط بقرب الأنف لنعومة الجواب."
        }

        return OudMaqamGuide(
            maqam = maqam,
            tuning = tuning,
            steps = steps,
            summaryTip = summaryTip,
            rishaTip = rishaTip
        )
    }

    private fun describeOudFinger(qt: Int, isQuarterTone: Boolean): Pair<String, String> {
        return when (qt) {
            0 -> "وتر مطلق (٠)" to "دون ضغط"
            1 -> "السبابة المرفوعة قليلاً" to "ربع تون (+١)"
            2 -> "السبابة المرجوعة (١)" to "نصف بُعد (+٢)"
            3 -> "السبابة (١) ربع تون" to "ثلاثة أرباع (+٣)"
            4 -> "السبابة (١)" to "بُعد كامل (+٤)"
            5 -> "الوسطى المرجوعة (٢)" to "بُعد وربع (+٥)"
            6 -> "الوسطى (٢)" to "بُعد ونصف (+٦)"
            7 -> "الوسطى (٢) ربع تون" to "بُعد وثلاثة أرباع (+٧)"
            8 -> "البنصر (٣)" to "بُعدان (+٨)"
            9 -> "البنصر المرتفعة (٣)" to "بُعدان وربع (+٩)"
            10 -> "الخنصر (٤)" to "بُعدان ونصف (+١٠)"
            11 -> "الخنصر المرتفعة (٤)" to "بُعدان وثلاثة أرباع (+١١)"
            12 -> "الوضع الثاني: السبابة" to "ثلاثة أبعاد (+١٢)"
            13 -> "الوضع الثاني: ربع تون" to "خامسة منخفضة (+١٣)"
            14 -> "الوضع الثاني: الوسطى" to "خامسة تامة (+١٤)"
            in 15..18 -> "الوضع الثالث (فوق الرقبة)" to "سادسة (+$qt)"
            in 19..23 -> "منطقة المرآة العلوية" to "سابعة (+$qt)"
            else -> "الجواب الكامل (المرآة)" to "أوكتاف تام (+$qt)"
        }.let { (finger, pos) ->
            if (isQuarterTone && !pos.contains("ربع")) {
                "$finger (ربع تون)" to pos
            } else {
                finger to pos
            }
        }
    }
}
