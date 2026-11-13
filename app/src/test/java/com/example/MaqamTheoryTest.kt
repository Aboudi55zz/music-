package com.example

import com.example.data.MaqamRepository
import com.example.data.NoteCatalog
import com.example.model.MaqamFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MaqamTheoryTest {
    @Test
    fun allFamiliesAreRepresentedInRepository() {
        val all = MaqamRepository.allMaqamat
        assertTrue("Expected at least 20 maqamat", all.size >= 20)
        for (family in MaqamFamily.entries) {
            val count = MaqamRepository.getMaqamatByFamily(family).size
            assertTrue("Family ${family.name} should have at least 1 maqam", count >= 1)
        }
    }

    @Test
    fun everyMaqamHasValidScalesMelodyAndAjnas() {
        for (maqam in MaqamRepository.allMaqamat) {
            assertTrue("${maqam.id} ascending scale empty", maqam.ascendingScale.size >= 7)
            assertTrue("${maqam.id} descending scale empty", maqam.descendingScale.size >= 7)
            assertTrue("${maqam.id} melody phrase empty", maqam.melodyPhrase.size >= 8)
            assertTrue("${maqam.id} root jins notes empty", maqam.rootJins.notes.size >= 3)
            assertTrue("${maqam.id} branch jins notes empty", maqam.branchJins.notes.size >= 3)
            assertTrue("${maqam.id} famous examples empty", maqam.famousExamples.isNotEmpty())
        }
    }

    @Test
    fun microtonalFrequenciesAreAccurate() {
        assertEquals(440.0, NoteCatalog.A4.frequencyHz, 0.01)
        assertEquals(NoteCatalog.C4.frequencyHz * 2.0, NoteCatalog.C5.frequencyHz, 0.05)

        val eb4 = NoteCatalog.E_FLAT_4.frequencyHz
        val ehf4 = NoteCatalog.E_HALF_FLAT_4.frequencyHz
        val e4 = NoteCatalog.E4.frequencyHz
        assertTrue("E half-flat ($ehf4) must be strictly higher than E flat ($eb4)", ehf4 > eb4)
        assertTrue("E half-flat ($ehf4) must be strictly lower than E natural ($e4)", ehf4 < e4)
    }

    @Test
    fun oudTuningsHaveSixCoursesElevenStringsAndAccurateQuarterTones() {
        for (tuning in com.example.data.OudTuning.entries) {
            assertEquals(6, tuning.courses.size)
            val totalStrings = tuning.courses.sumOf { if (it.isDoubleCourse) 2 else 1 }
            assertEquals(11, totalStrings)
            for (course in tuning.courses) {
                assertTrue(course.shortArabicName.isNotBlank())
                assertTrue(course.openSolfegeName.isNotBlank())
                assertTrue(course.openWesternName.isNotBlank())
                val openNote = com.example.data.OudCatalog.noteAtPosition(0, course, 0)
                assertEquals(course.openSolfegeName, openNote.arabicName)
                assertEquals(course.openWesternName, openNote.westernName)
                assertTrue(openNote.maqamPitchName.isNotBlank())
                assertTrue(openNote.westernShort.isNotBlank())
            }
        }
        val standard = com.example.data.OudTuning.ARABIC_STANDARD
        val c4Course = standard.courses[5]
        val eHalfFlat = com.example.data.OudCatalog.noteAtPosition(5, c4Course, 7)
        assertEquals(com.example.model.Accidental.HALF_FLAT, eHalfFlat.accidental)
        assertEquals("مي𝄳", eHalfFlat.arabicName)
        assertEquals("سيكاه", eHalfFlat.maqamPitchName)
        assertEquals("E𝄳4", eHalfFlat.westernName)
        assertEquals("E𝄳", eHalfFlat.westernShort)
        assertEquals(NoteCatalog.E_HALF_FLAT_4.frequencyHz, eHalfFlat.frequencyHz, 0.05)

        val rast = MaqamRepository.getMaqamById("rast")!!
        val markers = com.example.data.OudCatalog.getMaqamMarkersForCourse(5, c4Course, rast)
        assertTrue(markers.any { it.isTonic && it.quarterToneStepsFromNut == 0 })
        assertTrue(markers.any { it.fingerNote.isQuarterTone && it.quarterToneStepsFromNut == 7 })
    }

    @Test
    fun acousticOudDistancesAndRegisterDegreesAreAccurate() {
        // 1. Acoustic vibrating string displacement: open=0.0, 5th (14qt)~=0.3326, octave (24qt)=0.5
        assertEquals(0.0, com.example.data.OudCatalog.nutDisplacementRatio(0.0), 1e-6)
        assertEquals(0.3326, com.example.data.OudCatalog.nutDisplacementRatio(14.0), 0.001)
        assertEquals(0.5, com.example.data.OudCatalog.nutDisplacementRatio(24.0), 1e-6)

        // Step spacing must shrink exponentially as fingers move down the neck
        val firstWholeToneStep = com.example.data.OudCatalog.nutDisplacementRatio(4.0) -
            com.example.data.OudCatalog.nutDisplacementRatio(0.0)
        val octaveWholeToneStep = com.example.data.OudCatalog.nutDisplacementRatio(24.0) -
            com.example.data.OudCatalog.nutDisplacementRatio(20.0)
        assertTrue(firstWholeToneStep > octaveWholeToneStep * 1.5)

        // Exact round-trip across all 0..24 quarter-tone positions
        for (qt in 0..com.example.data.OudCatalog.MAX_NECK_QUARTER_TONES) {
            val ratio = com.example.data.OudCatalog.nutDisplacementRatio(qt.toDouble())
            assertEquals(qt, com.example.data.OudCatalog.quarterTonesFromDisplacementRatio(ratio))
        }

        // 2. Register-aware traditional Arabic degrees on Course 2 (G3 = Yakah) & Course 1 (C4 = Rast -> C5 = Kardan)
        val standard = com.example.data.OudTuning.ARABIC_STANDARD
        val g3Open = com.example.data.OudCatalog.noteAtPosition(4, standard.courses[4], 0)
        assertEquals("يكاه", g3Open.maqamPitchName)
        val a3OnG3 = com.example.data.OudCatalog.noteAtPosition(4, standard.courses[4], 4)
        assertEquals("عُشيران", a3OnG3.maqamPitchName)
        val bHalfFlatOnG3 = com.example.data.OudCatalog.noteAtPosition(4, standard.courses[4], 7)
        assertEquals("عراق", bHalfFlatOnG3.maqamPitchName)
        val c5OctaveOnC4 = com.example.data.OudCatalog.noteAtPosition(5, standard.courses[5], 24)
        assertEquals("كردان", c5OctaveOnC4.maqamPitchName)

        // 3. Musical interval labels between strings
        assertTrue(com.example.data.OudCatalog.intervalLabelBetween(standard.courses[0], standard.courses[1]).contains("رابعة"))
        assertTrue(com.example.data.OudCatalog.intervalLabelBetween(standard.courses[1], standard.courses[2]).contains("ثالثة"))
    }

    @Test
    fun everyMaqamBuildsCompleteOudFingeringGuideAcrossAllTunings() {
        for (tuning in com.example.data.OudTuning.entries) {
            for (maqam in MaqamRepository.allMaqamat) {
                val guide = com.example.data.OudCatalog.buildMaqamOudGuide(maqam, tuning)
                assertEquals(
                    "Step count mismatch for ${maqam.id} on ${tuning.name}",
                    maqam.ascendingScale.size,
                    guide.steps.size
                )
                assertTrue(guide.summaryTip.isNotBlank())
                assertTrue(guide.rishaTip.isNotBlank())
            }
        }
    }
}
