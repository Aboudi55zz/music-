package com.example

import com.example.audio.AcousticReverb
import com.example.audio.BiquadFilter
import com.example.audio.InstrumentBodyFilter
import com.example.audio.softClip
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

class AcousticDspTest {
    @Test
    fun bodyFiltersAndReverbProduceCleanFiniteAudio() {
        val sampleRate = 44100.0
        val body = InstrumentBodyFilter(sampleRate)
        val reverb = AcousticReverb(44100)
        val bp = BiquadFilter().apply { setBandPass(sampleRate, 440.0, 2.0) }

        var energy = 0.0
        for (i in 0 until 4410) {
            val impulse = sin(2.0 * PI * 320.24 * i / sampleRate) * 0.6
            val oud = body.shapeOud(impulse)
            val qanun = body.shapeQanun(impulse)
            val nay = body.shapeNay(impulse)
            val filtered = bp.process(impulse)
            val wet = reverb.process(oud, 0.25)
            val clipped = softClip(wet)

            assertFalse(oud.isNaN() || oud.isInfinite())
            assertFalse(qanun.isNaN() || qanun.isInfinite())
            assertFalse(nay.isNaN() || nay.isInfinite())
            assertFalse(filtered.isNaN() || filtered.isInfinite())
            assertFalse(clipped.isNaN() || clipped.isInfinite())
            assertTrue(clipped in -1.1..1.1)
            energy += clipped * clipped
        }
        val rms = sqrt(energy / 4410.0)
        assertTrue("Expected audible RMS > 0.05, got $rms", rms > 0.05)
    }
}
