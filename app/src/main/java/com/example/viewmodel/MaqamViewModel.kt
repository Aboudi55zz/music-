package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MaqamAudioEngine
import com.example.data.MaqamRepository
import com.example.data.PreferencesRepository
import com.example.data.TheoryRepository
import com.example.model.InstrumentTimbre
import com.example.model.Maqam
import com.example.model.MaqamFamily
import com.example.model.MusicalNote
import com.example.model.TheoryTopic
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
