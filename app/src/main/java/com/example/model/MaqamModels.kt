package com.example.model

import kotlin.math.pow

enum class Accidental(val symbol: String, val arabicLabel: String) {
    NATURAL("", "طبيعي"),
    FLAT("♭", "بيمول"),
    HALF_FLAT("𝄳", "نصف بيمول"),
    SHARP("♯", "دييز"),
    HALF_SHARP("𝄲", "نصف دييز")
}

enum class MaqamFamily(
    val arabicName: String,
    val englishName: String,
    val summary: String,
    val accentHex: Long
) {
    RAST(
        arabicName = "عائلة الراست",
        englishName = "Rast Family",
        summary = "مقامات الفخامة والتوازن والبهجة الكلاسيكية، وتعتمد على درجة السيكاه (نصف بيمول).",
        accentHex = 0xFFD4A853
    ),
    BAYATI(
        arabicName = "عائلة البياتي",
        englishName = "Bayati Family",
        summary = "أكثر العائلات دفئاً وشعبيةً في الموروث العربي، تجمع بين العذوبة والشجن الهادئ.",
        accentHex = 0xFF2B7A78
    ),
    HIJAZ(
        arabicName = "عائلة الحجاز",
        englishName = "Hijaz Family",
        summary = "تشتهر بالبعد الثلاثي المعبر (تون ونصف)، وتمنح روحانية الصحراء والتأمل والوجد.",
        accentHex = 0xFFC85A32
    ),
    KURD(
        arabicName = "عائلة الكرد",
        englishName = "Kurd Family",
        summary = "مقامات الرومانسية والرقة والعاطفة الجياشة، تخلو من أرباع التون.",
        accentHex = 0xFF8C4A6B
    ),
    NAHAWAND(
        arabicName = "عائلة النهوند",
        englishName = "Nahawand Family",
        summary = "تتميز بالرقة الدرامية والحالمة، وتناظر السلم الصغير في الموسيقى العالمية.",
        accentHex = 0xFF4A6FA5
    ),
    AJAM(
        arabicName = "عائلة العجم",
        englishName = "Ajam Family",
        summary = "مقامات الإشراق والقوة والوضوح، تخلو من أرباع التون وتناظر السلم الكبير.",
        accentHex = 0xFFB57C1E
    ),
    SABA(
        arabicName = "عائلة الصبا",
        englishName = "Saba Family",
        summary = "رمز الشجن العميق والحنين والخشوع الروحي، بتركيبة رابعة ناقصة فريدة.",
        accentHex = 0xFF7B4B3A
    ),
    SIKAH(
        arabicName = "عائلة السيكاه",
        englishName = "Sikah Family",
        summary = "ترتكز مباشرة على نغمة ربع تون (مي أو سي نصف بيمول)، وتمنح طابعاً حالماً مهيباً.",
        accentHex = 0xFF1E7874
    ),
    NIKRIZ(
        arabicName = "عائلة النكريز",
        englishName = "Nikriz Family",
        summary = "تجمع بين الجدية والغموض الساحر مع وجود ثانية زائدة داخل جنس الأصل.",
        accentHex = 0xFF6B5B95
    )
}

enum class InstrumentTimbre(val arabicName: String, val subtitle: String) {
    OUD("عود", "رنين وتري دافئ"),
    NAY("ناي", "نفخ شجي حالم"),
    QANUN("قانون", "أوتار مزدوجة مشرقة")
}

data class MusicalNote(
    val id: String,
    val arabicName: String,
    val maqamPitchName: String,
    val westernName: String,
    val diatonicStep: Int,
    val accidental: Accidental,
    val quarterToneOffset: Int,
    val durationBeats: Float = 1f
) {
    val frequencyHz: Double
        get() = 440.0 * 2.0.pow((quarterToneOffset - 18) / 24.0)

    val isQuarterTone: Boolean
        get() = accidental == Accidental.HALF_FLAT || accidental == Accidental.HALF_SHARP

    fun withDuration(beats: Float): MusicalNote = copy(durationBeats = beats)
}

data class JinsInfo(
    val name: String,
    val rootNoteArabic: String,
    val arabicIntervals: String,
    val westernIntervals: String,
    val notes: List<MusicalNote>,
    val description: String
)

data class FamousExample(
    val title: String,
    val artist: String,
    val style: String
)

data class Maqam(
    val id: String,
    val name: String,
    val englishName: String,
    val family: MaqamFamily,
    val hasQuarterTone: Boolean,
    val tonicName: String,
    val ghammazName: String,
    val jamType: String,
    val moodTags: List<String>,
    val summary: String,
    val detailedExplanation: String,
    val sayrExplanation: String,
    val ascendingScale: List<MusicalNote>,
    val descendingScale: List<MusicalNote>,
    val melodyTitle: String,
    val melodyPhrase: List<MusicalNote>,
    val rootJins: JinsInfo,
    val branchJins: JinsInfo,
    val famousExamples: List<FamousExample>
)

data class TheoryTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val body: String,
    val keyPoints: List<String>,
    val demoTitle: String? = null,
    val demoNotes: List<MusicalNote> = emptyList()
)
