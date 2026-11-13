package com.example.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal class BiquadFilter {
    private var b0 = 1.0
    private var b1 = 0.0
    private var b2 = 0.0
    private var a1 = 0.0
    private var a2 = 0.0
    private var x1 = 0.0
    private var x2 = 0.0
    private var y1 = 0.0
    private var y2 = 0.0

    fun setBandPass(sampleRate: Double, centerHz: Double, q: Double) {
        val omega = 2.0 * PI * centerHz / sampleRate
        val sn = sin(omega)
        val cs = cos(omega)
        val alpha = sn / (2.0 * q)
        val a0 = 1.0 + alpha

        b0 = (alpha) / a0
        b1 = 0.0
        b2 = (-alpha) / a0
        a1 = (-2.0 * cs) / a0
        a2 = (1.0 - alpha) / a0
    }

    fun setLowPass(sampleRate: Double, cutoffHz: Double, q: Double = 0.707) {
        val omega = 2.0 * PI * cutoffHz / sampleRate
        val sn = sin(omega)
        val cs = cos(omega)
        val alpha = sn / (2.0 * q)
        val a0 = 1.0 + alpha

        b0 = ((1.0 - cs) * 0.5) / a0
        b1 = (1.0 - cs) / a0
        b2 = ((1.0 - cs) * 0.5) / a0
        a1 = (-2.0 * cs) / a0
        a2 = (1.0 - alpha) / a0
    }

    fun setPeaking(sampleRate: Double, centerHz: Double, q: Double, gainDb: Double) {
        val a = Math.pow(10.0, gainDb / 40.0)
        val omega = 2.0 * PI * centerHz / sampleRate
        val sn = sin(omega)
        val cs = cos(omega)
        val alpha = sn / (2.0 * q)
        val a0 = 1.0 + alpha / a

        b0 = (1.0 + alpha * a) / a0
        b1 = (-2.0 * cs) / a0
        b2 = (1.0 - alpha * a) / a0
        a1 = (-2.0 * cs) / a0
        a2 = (1.0 - alpha / a) / a0
    }

    fun process(input: Double): Double {
        val output = b0 * input + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2
        x2 = x1
        x1 = input
        y2 = y1
        y1 = output
        return output
    }
}

/**
 * Warm acoustic Majlis hall reverberator (4 parallel damped comb filters + 2 series allpass diffusers).
 */
internal class AcousticReverb(sampleRate: Int) {
    private class DampedComb(size: Int, private val feedback: Double, private val damp: Double) {
        private val buf = DoubleArray(size)
        private var idx = 0
        private var filterStore = 0.0

        fun process(input: Double): Double {
            val out = buf[idx]
            filterStore = out * (1.0 - damp) + filterStore * damp
            buf[idx] = input + filterStore * feedback
            idx = (idx + 1) % buf.size
            return out
        }
    }

    private class AllPass(size: Int, private val feedback: Double = 0.5) {
        private val buf = DoubleArray(size)
        private var idx = 0

        fun process(input: Double): Double {
            val bufOut = buf[idx]
            val out = -input + bufOut
            buf[idx] = input + bufOut * feedback
            idx = (idx + 1) % buf.size
            return out
        }
    }

    private val scale = sampleRate / 44100.0
    private val combs = listOf(
        DampedComb((1116 * scale).toInt(), 0.78, 0.34),
        DampedComb((1188 * scale).toInt(), 0.79, 0.32),
        DampedComb((1277 * scale).toInt(), 0.80, 0.30),
        DampedComb((1356 * scale).toInt(), 0.81, 0.28)
    )
    private val allPass1 = AllPass((556 * scale).toInt())
    private val allPass2 = AllPass((441 * scale).toInt())

    fun process(dry: Double, wetMix: Double): Double {
        var acc = 0.0
        for (c in combs) {
            acc += c.process(dry * 0.25)
        }
        val diffused = allPass2.process(allPass1.process(acc))
        return dry * (1.0 - wetMix * 0.35) + diffused * wetMix
    }
}

/**
 * Simple deterministic-fast PRNG for breath noise and string pluck excitation.
 */
internal class FastNoise(seed: Long = 0x5DEECE66DL) {
    private var state = seed
    private var pinkB0 = 0.0
    private var pinkB1 = 0.0
    private var pinkB2 = 0.0

    fun nextWhite(): Double {
        state = state * 6364136223846793005L + 1442695040888963407L
        val bits = ((state ushr 33).toInt())
        return bits / 1073741824.0
    }

    fun nextPink(): Double {
        val white = nextWhite()
        pinkB0 = 0.99765 * pinkB0 + white * 0.0990460
        pinkB1 = 0.96300 * pinkB1 + white * 0.2965164
        pinkB2 = 0.57000 * pinkB2 + white * 1.0526913
        return (pinkB0 + pinkB1 + pinkB2 + white * 0.1848) * 0.22
    }
}

/**
 * Acoustic body resonator per instrument timbre to shape raw string/reed output
 * into a realistic wooden Oud, Qanun soundboard, or bamboo Nay.
 */
internal class InstrumentBodyFilter(sampleRate: Double) {
    private val oudAir = BiquadFilter().apply { setBandPass(sampleRate, 102.0, 2.0) }
    private val oudWoodLow = BiquadFilter().apply { setBandPass(sampleRate, 215.0, 1.7) }
    private val oudWoodMid = BiquadFilter().apply { setBandPass(sampleRate, 430.0, 1.6) }
    private val oudBridgePresence = BiquadFilter().apply { setPeaking(sampleRate, 1120.0, 1.2, 2.4) }
    private val oudTopSmooth = BiquadFilter().apply { setLowPass(sampleRate, 3400.0, 0.72) }

    private val qanunBridge = BiquadFilter().apply { setPeaking(sampleRate, 390.0, 1.5, 4.5) }
    private val qanunSparkle = BiquadFilter().apply { setPeaking(sampleRate, 1250.0, 1.4, 3.8) }
    private val qanunWarmth = BiquadFilter().apply { setLowPass(sampleRate, 6200.0, 0.707) }

    private val nayTube1 = BiquadFilter().apply { setPeaking(sampleRate, 480.0, 1.8, 4.0) }
    private val nayTube2 = BiquadFilter().apply { setPeaking(sampleRate, 960.0, 2.0, 2.5) }
    private val naySmooth = BiquadFilter().apply { setLowPass(sampleRate, 4200.0, 0.707) }

    fun shapeOud(input: Double): Double {
        val body = oudAir.process(input) * 0.65 +
            oudWoodLow.process(input) * 0.62 +
            oudWoodMid.process(input) * 0.42
        val direct = oudBridgePresence.process(input) * 0.62
        return oudTopSmooth.process(direct + body * 0.72)
    }

    fun shapeQanun(input: Double): Double {
        val s1 = qanunBridge.process(input)
        val s2 = qanunSparkle.process(s1)
        return qanunWarmth.process(s2) * 0.92
    }

    fun shapeNay(input: Double): Double {
        val s1 = nayTube1.process(input)
        val s2 = nayTube2.process(s1)
        return naySmooth.process(s2)
    }
}

internal fun softClip(x: Double): Double {
    // Fast smooth tanh-like limiter
    val ax = if (x < 0.0) -x else x
    return if (ax > 2.5) {
        if (x > 0) 1.0 else -1.0
    } else {
        x / sqrt(1.0 + x * x * 0.45)
    }
}
