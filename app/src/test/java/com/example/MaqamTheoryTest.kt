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
}
