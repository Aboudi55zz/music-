package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.InstrumentTimbre
import com.example.model.MusicalNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tanh

/**
 * Polyphonic real-time physical-modeling audio engine supporting exact 24-TET microtonal
 * quarter-tones across realistic Oud, Nay, and Qanun instruments.
 */
class MaqamAudioEngine {
    private val sampleRate = 44100
    private val sampleRateD = sampleRate.toDouble()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val activeVoices = CopyOnWriteArrayList<SynthVoice>()
    private var sequenceJob: Job? = null
    private var mixerJob: Job? = null

    @Volatile
    private var droneVoice: DroneVoice? = null

    @Volatile
    private var lastActivityTimeMs: Long = 0L

    fun playSingleNote(
        frequencyHz: Double,
        timbre: InstrumentTimbre = InstrumentTimbre.OUD,
        durationMs: Long = 950L
    ) {
        ensureMixerRunning()
        spawnVoice(frequencyHz, timbre, durationMs, velocity = 0.95)
    }

    fun playOudNote(
        frequencyHz: Double,
        isDoubleCourse: Boolean,
        isWoundBass: Boolean,
        isStopped: Boolean,
        pluckPosRatio: Double = 0.21,
        isUpstroke: Boolean = false,
        durationMs: Long = 1300L
    ) {
        ensureMixerRunning()
        lastActivityTimeMs = System.currentTimeMillis()
        while (activeVoices.size >= 10) {
            activeVoices.removeAt(0)
        }
        val voice = OudStringVoice(
            freq = frequencyHz,
            sampleRate = sampleRateD,
            durationSamples = ((durationMs * sampleRate) / 1000L).toInt().coerceAtLeast(4096),
            velocity = if (isUpstroke) 0.86 else 0.96,
            isDoubleCourse = isDoubleCourse,
            isWoundBass = isWoundBass,
            isStopped = isStopped,
            pluckPosRatio = pluckPosRatio.coerceIn(0.11, 0.34),
            isUpstroke = isUpstroke
        )
        activeVoices.add(voice)
    }

    fun playSequence(
        notes: List<MusicalNote>,
        timbre: InstrumentTimbre = InstrumentTimbre.OUD,
        tempoMultiplier: Float = 1f,
        onActiveNoteChanged: (Int) -> Unit,
        onCompleted: () -> Unit
    ) {
        stopSequence()
        if (notes.isEmpty()) {
            onCompleted()
            return
        }
        ensureMixerRunning()
        sequenceJob = scope.launch {
            val baseBeatMs = (540f / tempoMultiplier.coerceIn(0.5f, 1.8f)).toLong()
            try {
                for (index in notes.indices) {
                    if (!isActive) break
                    val note = notes[index]
                    onActiveNoteChanged(index)
                    val stepMs = (baseBeatMs * note.durationBeats).toLong().coerceAtLeast(180L)
                    // Let plucked strings ring slightly longer than the step for natural legato overlap
                    val sustainMs = when (timbre) {
                        InstrumentTimbre.OUD -> (stepMs * 1.55).toLong().coerceAtLeast(800L)
                        InstrumentTimbre.QANUN -> (stepMs * 1.65).toLong().coerceAtLeast(850L)
                        InstrumentTimbre.NAY -> (stepMs * 0.96).toLong().coerceAtLeast(220L)
                    }
                    // Expressive phrasing accents on longer/final notes
                    val velocity = when {
                        index == notes.lastIndex -> 0.98
                        note.durationBeats >= 1.5f -> 0.94
                        note.durationBeats <= 0.6f -> 0.80
                        else -> 0.88
                    }
                    if (timbre == InstrumentTimbre.OUD) {
                        val isUpstroke = (index % 2 == 1) && note.durationBeats <= 1.0f && index != notes.lastIndex
                        lastActivityTimeMs = System.currentTimeMillis()
                        while (activeVoices.size >= 10) {
                            activeVoices.removeAt(0)
                        }
                        activeVoices.add(
                            OudStringVoice(
                                freq = note.frequencyHz,
                                sampleRate = sampleRateD,
                                durationSamples = ((sustainMs * sampleRate) / 1000L).toInt().coerceAtLeast(4096),
                                velocity = velocity,
                                isDoubleCourse = note.frequencyHz >= 80.0,
                                isWoundBass = note.frequencyHz < 125.0,
                                isStopped = note.quarterToneOffset != 0 && note.quarterToneOffset != -10 && note.quarterToneOffset != -20,
                                pluckPosRatio = if (isUpstroke) 0.19 else 0.22,
                                isUpstroke = isUpstroke
                            )
                        )
                    } else {
                        spawnVoice(note.frequencyHz, timbre, sustainMs, velocity)
                    }
                    delay(stepMs)
                }
                // Let the final note decay gracefully
                delay(420L)
            } finally {
                onActiveNoteChanged(-1)
                onCompleted()
            }
        }
    }

    fun stopSequence() {
        sequenceJob?.cancel()
        sequenceJob = null
    }

    fun startDrone(frequencyHz: Double) {
        ensureMixerRunning()
        droneVoice = DroneVoice(frequencyHz, sampleRateD)
        lastActivityTimeMs = System.currentTimeMillis()
    }

    fun stopDrone() {
        droneVoice = null
    }

    fun stopAll() {
        stopSequence()
        stopDrone()
        activeVoices.clear()
    }

    fun release() {
        stopAll()
        mixerJob?.cancel()
        mixerJob = null
        scope.cancel()
    }

    private fun spawnVoice(
        frequencyHz: Double,
        timbre: InstrumentTimbre,
        durationMs: Long,
        velocity: Double
    ) {
        lastActivityTimeMs = System.currentTimeMillis()
        // Cap polyphony to prevent overload
        while (activeVoices.size >= 10) {
            activeVoices.removeAt(0)
        }
        val voice: SynthVoice = when (timbre) {
            InstrumentTimbre.OUD -> OudStringVoice(
                freq = frequencyHz,
                sampleRate = sampleRateD,
                durationSamples = ((durationMs * sampleRate) / 1000L).toInt().coerceAtLeast(4096),
                velocity = velocity
            )
            InstrumentTimbre.QANUN -> QanunStringVoice(
                freq = frequencyHz,
                sampleRate = sampleRateD,
                durationSamples = ((durationMs * sampleRate) / 1000L).toInt().coerceAtLeast(4096),
                velocity = velocity
            )
            InstrumentTimbre.NAY -> NayReedVoice(
                freq = frequencyHz,
                sampleRate = sampleRateD,
                durationSamples = ((durationMs * sampleRate) / 1000L).toInt().coerceAtLeast(4096),
                velocity = velocity
            )
        }
        activeVoices.add(voice)
    }

    private val mixerLock = Any()

    private fun ensureMixerRunning() {
        synchronized(mixerLock) {
            if (mixerJob?.isActive == true) return
            lastActivityTimeMs = System.currentTimeMillis()
            mixerJob = scope.launch {
                val chunkSize = 512
                val pcm = ShortArray(chunkSize)
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(chunkSize * 4)

                val track = runCatching {
                    AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(minBuf)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build()
                }.getOrNull() ?: return@launch

                val bodyFilter = InstrumentBodyFilter(sampleRateD)
                val reverb = AcousticReverb(sampleRate)

                try {
                    track.play()
                    while (isActive) {
                        val now = System.currentTimeMillis()
                        if (activeVoices.isEmpty() && droneVoice == null && now - lastActivityTimeMs > 3500L) {
                            break
                        }

                        for (i in 0 until chunkSize) {
                            var oudBus = 0.0
                            var qanunBus = 0.0
                            var nayBus = 0.0

                            val iterator = activeVoices.iterator()
                            while (iterator.hasNext()) {
                                val v = iterator.next()
                                val sample = v.nextSample()
                                when (v.timbre) {
                                    InstrumentTimbre.OUD -> oudBus += sample
                                    InstrumentTimbre.QANUN -> qanunBus += sample
                                    InstrumentTimbre.NAY -> nayBus += sample
                                }
                                if (v.isFinished) {
                                    activeVoices.remove(v)
                                }
                            }

                            var shaped = 0.0
                            if (oudBus != 0.0) shaped += bodyFilter.shapeOud(oudBus)
                            if (qanunBus != 0.0) shaped += bodyFilter.shapeQanun(qanunBus)
                            if (nayBus != 0.0) shaped += bodyFilter.shapeNay(nayBus)

                            val drone = droneVoice
                            if (drone != null) {
                                shaped += drone.nextSample()
                            }

                            val withRoom = reverb.process(shaped, wetMix = 0.24)
                            val mastered = softClip(withRoom * 0.85)
                            val s16 = (mastered * 28000.0).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            pcm[i] = s16.toShort()
                        }

                        track.write(pcm, 0, pcm.size)
                    }
                } catch (_: Throwable) {
                    // Safe exit on audio interruption or headless test environment
                } finally {
                    runCatching {
                        track.pause()
                        track.flush()
                        track.release()
                    }
                    synchronized(mixerLock) {
                        mixerJob = null
                    }
                }
            }
        }
    }
}

private sealed interface SynthVoice {
    val timbre: InstrumentTimbre
    val isFinished: Boolean
    fun nextSample(): Double
}

private fun computeStringDamping(freq: Double, decaySeconds: Double): Double {
    val cycles = (freq * decaySeconds).coerceAtLeast(10.0)
    return Math.pow(0.08, 1.0 / cycles).coerceIn(0.985, 0.9988)
}

/**
 * Physical-modeling Karplus-Strong Arabic Oud course (single wound bass or double unison nylon pair)
 * with frequency-compensated damping, wooden Risha plectrum transient, and finger-flesh attenuation.
 */
private class OudStringVoice(
    private val freq: Double,
    private val sampleRate: Double,
    private val durationSamples: Int,
    private val velocity: Double,
    private val isDoubleCourse: Boolean = true,
    private val isWoundBass: Boolean = freq < 125.0,
    private val isStopped: Boolean = false,
    private val pluckPosRatio: Double = 0.21,
    private val isUpstroke: Boolean = false
) : SynthVoice {
    override val timbre: InstrumentTimbre = InstrumentTimbre.OUD
    override var isFinished: Boolean = false
        private set

    private val decaySeconds = when {
        isWoundBass && !isStopped -> 2.35
        isWoundBass -> 1.85
        !isStopped -> 1.80
        else -> 1.40
    }
    private val oudDamping = computeStringDamping(freq, decaySeconds = decaySeconds)

    // Higher frequencies run the loop filter more often per second, so lower lpAlpha keeps treble clear
    private val baseLpAlpha = (0.45 - 0.24 * ((freq - 65.0) / 300.0)).coerceIn(0.18, 0.43) +
        (if (isStopped) 0.04 else 0.0) -
        (if (isUpstroke) 0.025 else 0.0)

    private val courseA = KarplusString(
        freq = if (isDoubleCourse) freq * 0.99945 else freq,
        sampleRate = sampleRate,
        damping = oudDamping,
        lpAlpha = baseLpAlpha.coerceIn(0.16, 0.48),
        pluckPosRatio = pluckPosRatio,
        bassHarmonicBoost = if (isWoundBass) 0.30 else 0.08,
        seed = 0x1234567L
    )
    private val courseB = if (isDoubleCourse) {
        KarplusString(
            freq = freq * 1.00055,
            sampleRate = sampleRate,
            damping = oudDamping * 0.9997,
            lpAlpha = (baseLpAlpha + 0.025).coerceIn(0.16, 0.48),
            pluckPosRatio = (pluckPosRatio + 0.025).coerceAtMost(0.36),
            bassHarmonicBoost = if (isWoundBass) 0.24 else 0.06,
            seed = 0x7654321L
        )
    } else null

    private val rishaNoise = FastNoise(0x9E3779B9L)
    private val rishaFilter = BiquadFilter().apply {
        setBandPass(sampleRate, if (isUpstroke) 2150.0 else 1680.0, 1.15)
    }
    private val courseBDelaySamples = (sampleRate * (if (isUpstroke) 0.0022 else 0.0032)).toInt()
    private var sampleIdx = 0
    private val releaseSamples = (sampleRate * 0.18).toInt()

    override fun nextSample(): Double {
        if (sampleIdx >= durationSamples) {
            isFinished = true
            return 0.0
        }
        val a = courseA.tick()
        val rawString = if (courseB != null) {
            val b = if (sampleIdx >= courseBDelaySamples) courseB.tick() else 0.0
            a * 0.54 + b * 0.46
        } else {
            a * 0.96
        }

        // Tactile wooden Risha brush transient on first ~14ms
        val rishaEnv = if (sampleIdx < 620) exp(-sampleIdx / (if (isUpstroke) 95.0 else 125.0)) else 0.0
        val rishaGain = if (isUpstroke) 0.24 else 0.31
        val risha = rishaFilter.process(rishaNoise.nextWhite()) * rishaEnv * rishaGain

        // Subtle wooden soundboard thump for deep bass courses (C2, F2, A2) so low notes carry on small speakers
        val thump = if (isWoundBass && sampleIdx < 1600) {
            val t = sampleIdx / sampleRate
            sin(2.0 * PI * 108.0 * t) * exp(-t * 42.0) * 0.14
        } else 0.0

        val releaseEnv = if (sampleIdx > durationSamples - releaseSamples) {
            (durationSamples - sampleIdx).toDouble() / releaseSamples
        } else {
            1.0
        }

        sampleIdx++
        return (rawString + risha + thump) * velocity * releaseEnv * 0.88
    }
}

/**
 * Physical-modeling Qanun triple-course bright string with crisp horn/tortoiseshell plectrum shimmer.
 */
private class QanunStringVoice(
    freq: Double,
    private val sampleRate: Double,
    private val durationSamples: Int,
    private val velocity: Double
) : SynthVoice {
    override val timbre: InstrumentTimbre = InstrumentTimbre.QANUN
    override var isFinished: Boolean = false
        private set

    private val qanunDamping = computeStringDamping(freq, decaySeconds = 2.3)
    private val course1 = KarplusString(freq * 0.9991, sampleRate, damping = qanunDamping, lpAlpha = 0.20, pluckPosRatio = 0.14, seed = 101L)
    private val course2 = KarplusString(freq, sampleRate, damping = qanunDamping, lpAlpha = 0.18, pluckPosRatio = 0.16, seed = 202L)
    private val course3 = KarplusString(freq * 1.0009, sampleRate, damping = qanunDamping * 0.9998, lpAlpha = 0.22, pluckPosRatio = 0.18, seed = 303L)

    private val pickNoise = FastNoise(0xCAFEBABEL)
    private val pickFilter = BiquadFilter().apply { setBandPass(sampleRate, 3200.0, 1.5) }
    private var sampleIdx = 0
    private val releaseSamples = (sampleRate * 0.20).toInt()

    override fun nextSample(): Double {
        if (sampleIdx >= durationSamples) {
            isFinished = true
            return 0.0
        }
        val c1 = course1.tick()
        val c2 = if (sampleIdx > 90) course2.tick() else 0.0
        val c3 = if (sampleIdx > 170) course3.tick() else 0.0

        val pickEnv = if (sampleIdx < 420) exp(-sampleIdx / 75.0) else 0.0
        val pick = pickFilter.process(pickNoise.nextWhite()) * pickEnv * 0.26

        val releaseEnv = if (sampleIdx > durationSamples - releaseSamples) {
            (durationSamples - sampleIdx).toDouble() / releaseSamples
        } else {
            1.0
        }

        sampleIdx++
        return ((c1 + c2 + c3) * 0.36 + pick) * velocity * releaseEnv * 0.80
    }
}

/**
 * Physical-modeling Karplus-Strong fractional-delay string core with exact 1-pole loop filter
 * phase-delay compensation for < 0.05 cent 24-TET microtonal accuracy.
 */
private class KarplusString(
    freq: Double,
    sampleRate: Double,
    private val damping: Double,
    private val lpAlpha: Double,
    pluckPosRatio: Double,
    bassHarmonicBoost: Double = 0.12,
    seed: Long
) {
    private val buf: DoubleArray
    private var writePos = 0
    private val intDelay: Int
    private val fracDelay: Double
    private var lpState = 0.0

    init {
        val safeFreq = freq.coerceAtLeast(35.0)
        val omega = 2.0 * PI * safeFreq / sampleRate
        val filterPhaseDelay = atan2(lpAlpha * sin(omega), 1.0 - lpAlpha * cos(omega)) / omega
        val exactDelay = (sampleRate / safeFreq) - filterPhaseDelay
        intDelay = floor(exactDelay).toInt().coerceAtLeast(4)
        fracDelay = (exactDelay - intDelay).coerceIn(0.0, 0.9999)

        val size = intDelay + 8
        buf = DoubleArray(size)

        // Generate comb-filtered pluck burst + warm woody fundamental & octave harmonic warmth
        val noise = FastNoise(seed)
        val rawNoise = DoubleArray(size) { noise.nextWhite() }
        val pluckOffset = (intDelay * pluckPosRatio).toInt().coerceAtLeast(1)
        val excCutoff = (4200.0 + (1.0 - pluckPosRatio) * 1800.0).coerceIn(2800.0, 6200.0)
        val excFilter = BiquadFilter().apply { setLowPass(sampleRate, excCutoff) }

        for (i in 0 until size) {
            val comb = rawNoise[i] - rawNoise[(i - pluckOffset + size) % size]
            val phase = 2.0 * PI * i / exactDelay
            val warmBody = sin(phase) * (0.34 + bassHarmonicBoost * 0.45) +
                sin(phase * 2.0) * (0.18 + bassHarmonicBoost * 0.65) +
                sin(phase * 3.0) * (bassHarmonicBoost * 0.35)
            buf[i] = excFilter.process(comb * 0.66 + warmBody)
        }
    }

    fun tick(): Double {
        val size = buf.size
        val r0 = (writePos - intDelay + size) % size
        val r1 = (r0 - 1 + size) % size
        val delayed = buf[r0] * (1.0 - fracDelay) + buf[r1] * fracDelay

        lpState = delayed * (1.0 - lpAlpha) + lpState * lpAlpha
        val nextVal = lpState * damping
        buf[writePos] = nextVal
        writePos = (writePos + 1) % size
        return delayed
    }
}

/**
 * Expressive bamboo Nay reed woodwind with embouchure pitch scoop, air breath turbulence,
 * warm soft-clipped tube harmonics, and natural organic vibrato.
 */
private class NayReedVoice(
    private val freq: Double,
    private val sampleRate: Double,
    private val durationSamples: Int,
    private val velocity: Double
) : SynthVoice {
    override val timbre: InstrumentTimbre = InstrumentTimbre.NAY
    override var isFinished: Boolean = false
        private set

    private var phase = 0.0
    private var sampleIdx = 0
    private val breathNoise = FastNoise(0xA5A5A5A5L)
    private val airFilter = BiquadFilter().apply { setBandPass(sampleRate, freq * 2.4, 2.2) }
    private val chiffFilter = BiquadFilter().apply { setBandPass(sampleRate, 2600.0, 1.4) }
    private val attackSamples = (sampleRate * 0.075).toInt().coerceAtLeast(1)
    private val releaseSamples = (sampleRate * 0.11).toInt().coerceAtLeast(1)

    override fun nextSample(): Double {
        if (sampleIdx >= durationSamples) {
            isFinished = true
            return 0.0
        }
        val t = sampleIdx / sampleRate

        // Organic embouchure scoop: starts ~25 cents flat and glides warmly to pitch
        val scoop = 1.0 - 0.014 * exp(-t * 28.0)
        // Delayed breath vibrato (~4.8 Hz)
        val vibDepth = 0.0055 * (1.0 - exp(-t * 3.5))
        val vib = 1.0 + vibDepth * sin(2.0 * PI * 4.8 * t)
        val instantFreq = freq * scoop * vib

        phase += 2.0 * PI * instantFreq / sampleRate
        if (phase > 2.0 * PI) phase -= 2.0 * PI

        // Soft reed waveshaping (produces warm natural woodwind harmonics)
        val pressure = 0.88 + 0.10 * sin(2.0 * PI * 2.1 * t)
        val reedDrive = sin(phase) * 0.95 + sin(phase * 2.0) * 0.28 + sin(phase * 3.0) * 0.12
        val hollowTone = tanh(reedDrive * 1.35 * pressure) * 0.62

        // Continuous breathy air column + initial lip chiff
        val air = airFilter.process(breathNoise.nextPink()) * 0.22
        val chiffEnv = if (sampleIdx < attackSamples * 2) exp(-t * 22.0) else 0.0
        val chiff = chiffFilter.process(breathNoise.nextWhite()) * chiffEnv * 0.18

        val envAttack = if (sampleIdx < attackSamples) {
            val x = sampleIdx.toDouble() / attackSamples
            x * x * (3.0 - 2.0 * x) // smoothstep
        } else 1.0

        val envRelease = if (sampleIdx > durationSamples - releaseSamples) {
            (durationSamples - sampleIdx).toDouble() / releaseSamples
        } else 1.0

        sampleIdx++
        return (hollowTone + air + chiff) * envAttack * envRelease * velocity * 0.52
    }
}

/**
 * Warm organic Oud/Cello-like sustained drone voice for the tonic note.
 */
private class DroneVoice(
    private val freq: Double,
    private val sampleRate: Double
) {
    private var phase = 0.0
    private var t = 0.0
    private val lpFilter = BiquadFilter().apply { setLowPass(sampleRate, freq * 3.2, 0.8) }

    fun nextSample(): Double {
        val dt = 1.0 / sampleRate
        t += dt
        val drift = 1.0 + 0.0018 * sin(2.0 * PI * 0.35 * t)
        val f = freq * 0.5 * drift // Sub-octave warm tonic drone
        phase += 2.0 * PI * f * dt
        if (phase > 2.0 * PI) phase -= 2.0 * PI

        val bowed = sin(phase) * 0.55 +
            sin(phase * 2.0) * 0.26 +
            sin(phase * 3.0) * 0.12 +
            sin(phase * 4.0) * 0.05
        val shimmer = 0.85 + 0.15 * sin(2.0 * PI * 0.22 * t)
        val fadeIn = (t / 0.35).coerceIn(0.0, 1.0)
        return lpFilter.process(bowed) * shimmer * fadeIn * 0.18
    }
}
