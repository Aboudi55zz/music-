package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MaqamAudioEngine
import com.example.data.MaqamRepository
import com.example.data.OudCatalog
import com.example.data.OudFingerNote
import com.example.data.OudTuning
import com.example.data.PreferencesRepository
import com.example.data.TheoryRepository
import com.example.model.InstrumentTimbre
import com.example.model.Maqam
import com.example.model.MaqamFamily
import com.example.model.MusicalNote
import com.example.model.TheoryTopic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class QuarterToneFilter(val label: String) {
    ALL("الكل"),
    WITH_QUARTER_TONE("بربع تون"),
    WITHOUT_QUARTER_TONE("بدون ربع تون")
}

enum class QuizDifficulty(val label: String) {
    FUNDAMENTALS("المقامات الأساسية (٨)"),
    ALL_MAQAMAT("الموسوعة الكاملة (٢٣)")
}

data class QuizQuestion(
    val targetMaqam: Maqam,
    val options: List<Maqam>,
    val playMelodyInsteadOfScale: Boolean
)

data class PlaybackUiState(
    val isPlaying: Boolean = false,
    val sourceTag: String = "",
    val activeNoteIndex: Int = -1,
    val tempoMultiplier: Float = 1.0f,
    val isDroneActive: Boolean = false,
    val droneNoteId: String? = null
)

class MaqamViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesRepository(application.applicationContext)
    private val audioEngine = MaqamAudioEngine()

    val allMaqamat: List<Maqam> = MaqamRepository.allMaqamat
    val theoryTopics: List<TheoryTopic> = TheoryRepository.topics
    val featuredMaqam: Maqam = MaqamRepository.getFeaturedMaqam()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFamily = MutableStateFlow<MaqamFamily?>(null)
    val selectedFamily: StateFlow<MaqamFamily?> = _selectedFamily.asStateFlow()

    private val _quarterToneFilter = MutableStateFlow(QuarterToneFilter.ALL)
    val quarterToneFilter: StateFlow<QuarterToneFilter> = _quarterToneFilter.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = prefs.favoriteIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val selectedInstrument: StateFlow<InstrumentTimbre> = prefs.selectedInstrument
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InstrumentTimbre.OUD)

    val bestStreak: StateFlow<Int> = prefs.bestStreak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCorrect: StateFlow<Int> = prefs.totalCorrect
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredMaqamat: StateFlow<List<Maqam>> = combine(
        _searchQuery,
        _selectedFamily,
        _quarterToneFilter,
        _onlyFavorites,
        favoriteIds
    ) { query, family, qtFilter, onlyFavs, favSet ->
        val trimmed = query.trim()
        allMaqamat.filter { maqam ->
            val matchesFamily = family == null || maqam.family == family
            val matchesQt = when (qtFilter) {
                QuarterToneFilter.ALL -> true
                QuarterToneFilter.WITH_QUARTER_TONE -> maqam.hasQuarterTone
                QuarterToneFilter.WITHOUT_QUARTER_TONE -> !maqam.hasQuarterTone
            }
            val matchesFav = !onlyFavs || maqam.id in favSet
            val matchesQuery = trimmed.isEmpty() ||
                maqam.name.contains(trimmed, ignoreCase = true) ||
                maqam.englishName.contains(trimmed, ignoreCase = true) ||
                maqam.summary.contains(trimmed, ignoreCase = true) ||
                maqam.family.arabicName.contains(trimmed, ignoreCase = true) ||
                maqam.moodTags.any { it.contains(trimmed, ignoreCase = true) } ||
                maqam.rootJins.name.contains(trimmed, ignoreCase = true)

            matchesFamily && matchesQt && matchesFav && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allMaqamat)

    private val _playbackState = MutableStateFlow(PlaybackUiState())
    val playbackState: StateFlow<PlaybackUiState> = _playbackState.asStateFlow()

    // Studio keyboard state
    private val _studioMaqam = MutableStateFlow(featuredMaqam)
    val studioMaqam: StateFlow<Maqam> = _studioMaqam.asStateFlow()

    private val _studioActiveNoteId = MutableStateFlow<String?>(null)
    val studioActiveNoteId: StateFlow<String?> = _studioActiveNoteId.asStateFlow()

    // 2D Interactive Oud state
    private val _oudTuning = MutableStateFlow(OudTuning.ARABIC_STANDARD)
    val oudTuning: StateFlow<OudTuning> = _oudTuning.asStateFlow()

    private val _oudGuideMaqam = MutableStateFlow<Maqam?>(featuredMaqam)
    val oudGuideMaqam: StateFlow<Maqam?> = _oudGuideMaqam.asStateFlow()

    private val _showOudGuides = MutableStateFlow(true)
    val showOudGuides: StateFlow<Boolean> = _showOudGuides.asStateFlow()

    private val _lastOudNote = MutableStateFlow<OudFingerNote?>(null)
    val lastOudNote: StateFlow<OudFingerNote?> = _lastOudNote.asStateFlow()

    private val _oudPulseTrigger = MutableStateFlow(0L)
    val oudPulseTrigger: StateFlow<Long> = _oudPulseTrigger.asStateFlow()

    private val _oudExternalCourse = MutableStateFlow<Int?>(null)
    val oudExternalCourse: StateFlow<Int?> = _oudExternalCourse.asStateFlow()

    private val _oudExternalPressedQt = MutableStateFlow<Int?>(null)
    val oudExternalPressedQt: StateFlow<Int?> = _oudExternalPressedQt.asStateFlow()

    // Ear training quiz state
    private val _quizDifficulty = MutableStateFlow(QuizDifficulty.FUNDAMENTALS)
    val quizDifficulty: StateFlow<QuizDifficulty> = _quizDifficulty.asStateFlow()

    private val _currentQuestion = MutableStateFlow<QuizQuestion?>(null)
    val currentQuestion: StateFlow<QuizQuestion?> = _currentQuestion.asStateFlow()

    private val _selectedQuizAnswerId = MutableStateFlow<String?>(null)
    val selectedQuizAnswerId: StateFlow<String?> = _selectedQuizAnswerId.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    init {
        generateNextQuizQuestion(autoPlay = false)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectFamily(family: MaqamFamily?) {
        _selectedFamily.value = if (_selectedFamily.value == family) null else family
    }

    fun setQuarterToneFilter(filter: QuarterToneFilter) {
        _quarterToneFilter.value = filter
    }

    fun toggleOnlyFavorites() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedFamily.value = null
        _quarterToneFilter.value = QuarterToneFilter.ALL
        _onlyFavorites.value = false
    }

    fun toggleFavorite(maqamId: String) {
        viewModelScope.launch {
            prefs.toggleFavorite(maqamId)
        }
    }

    fun selectInstrument(timbre: InstrumentTimbre) {
        viewModelScope.launch {
            prefs.setInstrument(timbre)
        }
    }

    fun setTempoMultiplier(multiplier: Float) {
        _playbackState.update { it.copy(tempoMultiplier = multiplier.coerceIn(0.6f, 1.5f)) }
    }

    fun playSingleNote(note: MusicalNote) {
        _studioActiveNoteId.value = note.id
        audioEngine.playSingleNote(note.frequencyHz, selectedInstrument.value)
    }

    fun playNoteSequence(notes: List<MusicalNote>, sourceTag: String) {
        val currentlySameAndPlaying =
            _playbackState.value.isPlaying && _playbackState.value.sourceTag == sourceTag
        if (currentlySameAndPlaying) {
            stopPlayback()
            return
        }
        val tempo = _playbackState.value.tempoMultiplier
        _playbackState.update {
            it.copy(isPlaying = true, sourceTag = sourceTag, activeNoteIndex = 0)
        }
        audioEngine.playSequence(
            notes = notes,
            timbre = selectedInstrument.value,
            tempoMultiplier = tempo,
            onActiveNoteChanged = { index ->
                _playbackState.update { it.copy(activeNoteIndex = index) }
                if (index in notes.indices) {
                    _studioActiveNoteId.value = notes[index].id
                }
            },
            onCompleted = {
                _playbackState.update {
                    it.copy(isPlaying = false, sourceTag = "", activeNoteIndex = -1)
                }
            }
        )
    }

    fun stopPlayback() {
        audioEngine.stopSequence()
        _playbackState.update {
            it.copy(isPlaying = false, sourceTag = "", activeNoteIndex = -1)
        }
    }

    fun selectStudioMaqam(maqam: Maqam) {
        _studioMaqam.value = maqam
        if (_playbackState.value.isDroneActive) {
            val tonic = maqam.ascendingScale.firstOrNull() ?: return
            audioEngine.startDrone(tonic.frequencyHz)
            _playbackState.update { it.copy(droneNoteId = tonic.id) }
        }
    }

    fun toggleDrone(tonicNote: MusicalNote) {
        val nextActive = !_playbackState.value.isDroneActive
        if (nextActive) {
            audioEngine.startDrone(tonicNote.frequencyHz)
            _playbackState.update { it.copy(isDroneActive = true, droneNoteId = tonicNote.id) }
        } else {
            audioEngine.stopDrone()
            _playbackState.update { it.copy(isDroneActive = false, droneNoteId = null) }
        }
    }

    fun stopDrone() {
        audioEngine.stopDrone()
        _playbackState.update { it.copy(isDroneActive = false, droneNoteId = null) }
    }

    // ==================== 2D OUD ACTIONS ====================
    fun selectOudTuning(tuning: OudTuning) {
        _oudTuning.value = tuning
        _lastOudNote.value = null
    }

    fun selectOudGuideMaqam(maqam: Maqam?) {
        _oudGuideMaqam.value = maqam
    }

    fun toggleShowOudGuides() {
        _showOudGuides.value = !_showOudGuides.value
    }

    fun pluckOudCourse(
        courseIndex: Int,
        quarterToneSteps: Int,
        triggerCanvasPulse: Boolean = false,
        pluckPosRatio: Float = 0.21f,
        isUpstroke: Boolean = false
    ) {
        val courses = _oudTuning.value.courses
        if (courseIndex !in courses.indices) return
        val course = courses[courseIndex]
        val note = OudCatalog.noteAtPosition(courseIndex, course, quarterToneSteps)
        _lastOudNote.value = note
        if (triggerCanvasPulse) {
            _oudExternalCourse.value = courseIndex
            _oudExternalPressedQt.value = quarterToneSteps
            _oudPulseTrigger.value = System.currentTimeMillis()
        }
        audioEngine.playOudNote(
            frequencyHz = note.frequencyHz,
            isDoubleCourse = course.isDoubleCourse,
            isWoundBass = course.isWoundBass,
            isStopped = quarterToneSteps > 0,
            pluckPosRatio = pluckPosRatio.toDouble(),
            isUpstroke = isUpstroke,
            durationMs = if (quarterToneSteps == 0) 1450L else 1200L
        )
    }

    fun strumAllOudCourses() {
        viewModelScope.launch {
            val courses = _oudTuning.value.courses
            _oudExternalCourse.value = -1
            _oudExternalPressedQt.value = 0
            _oudPulseTrigger.value = System.currentTimeMillis()
            courses.forEachIndexed { idx, course ->
                val note = OudCatalog.noteAtPosition(idx, course, 0)
                _lastOudNote.value = note
                audioEngine.playOudNote(
                    frequencyHz = note.frequencyHz,
                    isDoubleCourse = course.isDoubleCourse,
                    isWoundBass = course.isWoundBass,
                    isStopped = false,
                    pluckPosRatio = 0.22,
                    isUpstroke = false,
                    durationMs = 1550L
                )
                delay(40L)
            }
        }
    }

    fun playGuideMaqamOnOud() {
        val maqam = _oudGuideMaqam.value ?: return
        val tag = "oud_scale_${maqam.id}"
        if (_playbackState.value.isPlaying && _playbackState.value.sourceTag == tag) {
            stopPlayback()
            return
        }
        stopPlayback()
        val guide = OudCatalog.buildMaqamOudGuide(maqam, _oudTuning.value)
        val steps = guide.steps
        if (steps.isEmpty()) return

        // Use the exact real Oud register (1 octave below written treble) & exact fingering steps
        val oudScaleNotes = steps.map { step ->
            step.note.copy(quarterToneOffset = step.oudNote.absoluteQuarterToneOffset)
        }
        _playbackState.update { it.copy(isPlaying = true, sourceTag = tag, activeNoteIndex = 0) }
        audioEngine.playSequence(
            notes = oudScaleNotes,
            timbre = InstrumentTimbre.OUD,
            tempoMultiplier = _playbackState.value.tempoMultiplier,
            onActiveNoteChanged = { idx ->
                _playbackState.update { it.copy(activeNoteIndex = idx) }
                if (idx in steps.indices) {
                    val step = steps[idx]
                    _lastOudNote.value = step.oudNote
                    _oudExternalCourse.value = step.courseIndex
                    _oudExternalPressedQt.value = step.quarterToneSteps
                    _oudPulseTrigger.value = System.currentTimeMillis()
                }
            },
            onCompleted = {
                _playbackState.update { it.copy(isPlaying = false, sourceTag = "", activeNoteIndex = -1) }
            }
        )
    }

    fun playOudGuideSequence(guide: com.example.data.OudMaqamGuide) {
        val tag = "detail_oud_${guide.maqam.id}"
        if (_playbackState.value.isPlaying && _playbackState.value.sourceTag == tag) {
            stopPlayback()
            return
        }
        stopPlayback()
        val steps = guide.steps
        if (steps.isEmpty()) return

        // Convert each step's exact Oud frequency note into a playable sequence
        val oudNotes = steps.map { step ->
            step.note.copy(quarterToneOffset = step.oudNote.absoluteQuarterToneOffset)
        }
        _playbackState.update { it.copy(isPlaying = true, sourceTag = tag, activeNoteIndex = 0) }
        audioEngine.playSequence(
            notes = oudNotes,
            timbre = InstrumentTimbre.OUD,
            tempoMultiplier = _playbackState.value.tempoMultiplier,
            onActiveNoteChanged = { idx ->
                _playbackState.update { it.copy(activeNoteIndex = idx) }
                if (idx in steps.indices) {
                    val step = steps[idx]
                    _lastOudNote.value = step.oudNote
                    _oudExternalCourse.value = step.courseIndex
                    _oudExternalPressedQt.value = step.quarterToneSteps
                    _oudPulseTrigger.value = System.currentTimeMillis()
                }
            },
            onCompleted = {
                _playbackState.update { it.copy(isPlaying = false, sourceTag = "", activeNoteIndex = -1) }
            }
        )
    }

    fun setQuizDifficulty(difficulty: QuizDifficulty) {
        if (_quizDifficulty.value != difficulty) {
            _quizDifficulty.value = difficulty
            _currentStreak.value = 0
            generateNextQuizQuestion(autoPlay = false)
        }
    }

    fun generateNextQuizQuestion(autoPlay: Boolean = true) {
        stopPlayback()
        _selectedQuizAnswerId.value = null
        val fundamentalIds = setOf("rast", "bayati", "hijaz", "kurd", "nahawand", "ajam", "saba", "sikah")
        val pool = when (_quizDifficulty.value) {
            QuizDifficulty.FUNDAMENTALS -> allMaqamat.filter { it.id in fundamentalIds }
            QuizDifficulty.ALL_MAQAMAT -> allMaqamat
        }
        val target = pool.random()
        val distractors = pool.filter { it.id != target.id }.shuffled().take(3)
        val options = (listOf(target) + distractors).shuffled()
        val useMelody = (0..1).random() == 1
        val question = QuizQuestion(target, options, useMelody)
        _currentQuestion.value = question
        if (autoPlay) {
            playQuizQuestionAudio()
        }
    }

    fun playQuizQuestionAudio() {
        val q = _currentQuestion.value ?: return
        val notes = if (q.playMelodyInsteadOfScale) {
            q.targetMaqam.melodyPhrase
        } else {
            q.targetMaqam.ascendingScale
        }
        playNoteSequence(notes, "quiz_audio")
    }

    fun submitQuizAnswer(maqamId: String) {
        if (_selectedQuizAnswerId.value != null) return
        _selectedQuizAnswerId.value = maqamId
        val target = _currentQuestion.value?.targetMaqam ?: return
        if (maqamId == target.id) {
            val newStreak = _currentStreak.value + 1
            _currentStreak.value = newStreak
            viewModelScope.launch {
                prefs.recordQuizCorrect(newStreak)
            }
        } else {
            _currentStreak.value = 0
        }
    }

    fun getMaqamById(id: String): Maqam? = MaqamRepository.getMaqamById(id)

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
