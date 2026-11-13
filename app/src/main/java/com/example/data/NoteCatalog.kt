package com.example.data

import com.example.model.Accidental
import com.example.model.MusicalNote

object NoteCatalog {
    // Octave 3 (lower / Qarar register)
    val G3 = MusicalNote("G3", "صول قرار", "يكاه", "G3", -3, Accidental.NATURAL, -10)
    val G_SHARP_3 = MusicalNote("Gs3", "صول دييز قرار", "نيم حصار", "G♯3", -3, Accidental.SHARP, -8)
    val A_FLAT_3 = MusicalNote("Ab3", "لا بيمول قرار", "عشيران منخفض", "A♭3", -2, Accidental.FLAT, -8)
    val A3 = MusicalNote("A3", "لا قرار", "عشيران", "A3", -2, Accidental.NATURAL, -6)
    val A_HALF_FLAT_3 = MusicalNote("Ahf3", "لا نصف بيمول قرار", "قبا نيم عشيران", "A𝄳3", -2, Accidental.HALF_FLAT, -7)
    val B_FLAT_3 = MusicalNote("Bb3", "سي بيمول قرار", "عجم عشيران", "B♭3", -1, Accidental.FLAT, -4)
    val B_HALF_FLAT_3 = MusicalNote("Bhf3", "سي نصف بيمول قرار", "عراق", "B𝄳3", -1, Accidental.HALF_FLAT, -3)
    val B3 = MusicalNote("B3", "سي قرار", "قوشت", "B3", -1, Accidental.NATURAL, -2)

    // Octave 4 (Middle register - Primary Maqam Octave)
    val C4 = MusicalNote("C4", "دو", "راست", "C4", 0, Accidental.NATURAL, 0)
    val C_SHARP_4 = MusicalNote("Cs4", "دو دييز", "زيركولة", "C♯4", 0, Accidental.SHARP, 2)
    val D_FLAT_4 = MusicalNote("Db4", "ري بيمول", "نيم زيركولة", "D♭4", 1, Accidental.FLAT, 2)
    val D4 = MusicalNote("D4", "ري", "دوكاه", "D4", 1, Accidental.NATURAL, 4)
    val D_SHARP_4 = MusicalNote("Ds4", "ري دييز", "كردي", "D♯4", 1, Accidental.SHARP, 6)
    val E_FLAT_4 = MusicalNote("Eb4", "مي بيمول", "كرد", "E♭4", 2, Accidental.FLAT, 6)
    val E_HALF_FLAT_4 = MusicalNote("Ehf4", "مي نصف بيمول", "سيكاه", "E𝄳4", 2, Accidental.HALF_FLAT, 7)
    val E4 = MusicalNote("E4", "مي", "بوسليك", "E4", 2, Accidental.NATURAL, 8)
    val F4 = MusicalNote("F4", "فا", "جهاركاه", "F4", 3, Accidental.NATURAL, 10)
    val F_SHARP_4 = MusicalNote("Fs4", "فا دييز", "حجاز", "F♯4", 3, Accidental.SHARP, 12)
    val G_FLAT_4 = MusicalNote("Gb4", "صول بيمول", "حجاز", "G♭4", 4, Accidental.FLAT, 12)
    val G4 = MusicalNote("G4", "صول", "نوى", "G4", 4, Accidental.NATURAL, 14)
    val G_SHARP_4 = MusicalNote("Gs4", "صول دييز", "نيم حصار", "G♯4", 4, Accidental.SHARP, 16)
    val A_FLAT_4 = MusicalNote("Ab4", "لا بيمول", "حصار", "A♭4", 5, Accidental.FLAT, 16)
    val A_HALF_FLAT_4 = MusicalNote("Ahf4", "لا نصف بيمول", "تيك حصار", "A𝄳4", 5, Accidental.HALF_FLAT, 17)
    val A4 = MusicalNote("A4", "لا", "حسيني", "A4", 5, Accidental.NATURAL, 18)
    val B_FLAT_4 = MusicalNote("Bb4", "سي بيمول", "عجم", "B♭4", 6, Accidental.FLAT, 20)
    val B_HALF_FLAT_4 = MusicalNote("Bhf4", "سي نصف بيمول", "أوج", "B𝄳4", 6, Accidental.HALF_FLAT, 21)
    val B4 = MusicalNote("B4", "سي", "ماهور", "B4", 6, Accidental.NATURAL, 22)

    // Octave 5 (Jawab / High register)
    val C5 = MusicalNote("C5", "دو جواب", "كردان", "C5", 7, Accidental.NATURAL, 24)
    val D_FLAT_5 = MusicalNote("Db5", "ري بيمول جواب", "شهناز", "D♭5", 8, Accidental.FLAT, 26)
    val D5 = MusicalNote("D5", "ري جواب", "محيّر", "D5", 8, Accidental.NATURAL, 28)
    val E_FLAT_5 = MusicalNote("Eb5", "مي بيمول جواب", "سنبلة", "E♭5", 9, Accidental.FLAT, 30)
    val E_HALF_FLAT_5 = MusicalNote("Ehf5", "مي نصف بيمول جواب", "بزرك", "E𝄳5", 9, Accidental.HALF_FLAT, 31)
    val E5 = MusicalNote("E5", "مي جواب", "جواب بوسليك", "E5", 9, Accidental.NATURAL, 32)
    val F5 = MusicalNote("F5", "فا جواب", "ماهوران", "F5", 10, Accidental.NATURAL, 34)
    val F_SHARP_5 = MusicalNote("Fs5", "فا دييز جواب", "جواب حجاز", "F♯5", 10, Accidental.SHARP, 36)
    val G5 = MusicalNote("G5", "صول جواب", "سهم", "G5", 11, Accidental.NATURAL, 38)

    // Ordered notes for the interactive Quarter-Tone Piano Keyboard (from G3 to F5)
    val keyboardWhiteNotes: List<MusicalNote> = listOf(
        G3, A3, B3, C4, D4, E4, F4, G4, A4, B4, C5, D5, E5, F5
    )

    // Microtonal & standard sharps/flats positioned relative to white keys
    data class AccidentalKeySpec(
        val note: MusicalNote,
        val positionIndex: Float // relative to white key index (e.g., 0.5f = between white 0 and 1)
    )

    val keyboardUpperKeys: List<AccidentalKeySpec> = listOf(
        // Standard black keys (halftones)
        AccidentalKeySpec(A_FLAT_3, 0.5f),
        AccidentalKeySpec(B_FLAT_3, 1.5f),
        AccidentalKeySpec(D_FLAT_4, 3.5f),
        AccidentalKeySpec(E_FLAT_4, 4.5f),
        AccidentalKeySpec(F_SHARP_4, 6.5f),
        AccidentalKeySpec(A_FLAT_4, 7.5f),
        AccidentalKeySpec(B_FLAT_4, 8.5f),
        AccidentalKeySpec(D_FLAT_5, 10.5f),
        AccidentalKeySpec(E_FLAT_5, 11.5f),
        AccidentalKeySpec(F_SHARP_5, 13.5f)
    )

    // Dedicated Arabic Quarter-Tone (Sikah / Half-Flat) keys
    val keyboardQuarterToneKeys: List<AccidentalKeySpec> = listOf(
        AccidentalKeySpec(A_HALF_FLAT_3, 0.78f),
        AccidentalKeySpec(B_HALF_FLAT_3, 1.78f),
        AccidentalKeySpec(E_HALF_FLAT_4, 4.78f),
        AccidentalKeySpec(A_HALF_FLAT_4, 7.78f),
        AccidentalKeySpec(B_HALF_FLAT_4, 8.78f),
        AccidentalKeySpec(E_HALF_FLAT_5, 11.78f)
    )

    val traditionalDegreeChart: List<MusicalNote> = listOf(
        G3, A3, B_HALF_FLAT_3, C4, D4, E_HALF_FLAT_4, F4, G4, A4, B_HALF_FLAT_4, C5, D5
    )
}
