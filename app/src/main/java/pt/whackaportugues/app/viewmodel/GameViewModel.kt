package pt.whackaportugues.app.viewmodel

import android.app.Application
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.whackaportugues.app.data.ScoreDatabase
import pt.whackaportugues.app.data.ScoreRepository
import pt.whackaportugues.app.model.Character
import pt.whackaportugues.app.model.Score

class GameViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val GAME_DURATION_MS = 60_000L
        const val HOLE_COUNT = 9

        // Mole timing: starts slow, speeds up over 60 seconds
        const val INITIAL_VISIBLE_MS = 1800L
        const val MIN_VISIBLE_MS = 700L
        const val INITIAL_INTERVAL_MS = 1200L
        const val MIN_INTERVAL_MS = 400L
    }

    enum class GameState { IDLE, PLAYING, FINISHED }

    private val repository: ScoreRepository
    private val handler = Handler(Looper.getMainLooper())
    private var countDownTimer: CountDownTimer? = null
    private var moleRunnable: Runnable? = null

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _timeLeftSec = MutableLiveData(GAME_DURATION_MS / 1000)
    val timeLeftSec: LiveData<Long> = _timeLeftSec

    /** Index (0-8) of the currently visible mole, or -1 if none */
    private val _activeMoleIndex = MutableLiveData(-1)
    val activeMoleIndex: LiveData<Int> = _activeMoleIndex

    /** Fired with the index of a successfully hit mole */
    private val _hitIndex = MutableLiveData<Int>()
    val hitIndex: LiveData<Int> = _hitIndex

    private val _gameState = MutableLiveData(GameState.IDLE)
    val gameState: LiveData<GameState> = _gameState

    private val _savedScoreId = MutableLiveData<Long>()
    val savedScoreId: LiveData<Long> = _savedScoreId

    var selectedCharacter: Character? = null
    private var currentMoleIndex = -1
    private var moleVisibleMs = INITIAL_VISIBLE_MS
    private var moleIntervalMs = INITIAL_INTERVAL_MS
    private var elapsed = 0L

    init {
        val dao = ScoreDatabase.getDatabase(application).scoreDao()
        repository = ScoreRepository(dao)
    }

    fun startGame(character: Character) {
        selectedCharacter = character
        _score.value = 0
        _timeLeftSec.value = GAME_DURATION_MS / 1000
        moleVisibleMs = INITIAL_VISIBLE_MS
        moleIntervalMs = INITIAL_INTERVAL_MS
        elapsed = 0L
        currentMoleIndex = -1
        _activeMoleIndex.value = -1
        _gameState.value = GameState.PLAYING
        startCountDown()
        scheduleMole()
    }

    private fun startCountDown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(GAME_DURATION_MS, 200) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                if (seconds != _timeLeftSec.value) {
                    _timeLeftSec.value = seconds
                }
                elapsed = GAME_DURATION_MS - millisUntilFinished
                val progress = elapsed.toFloat() / GAME_DURATION_MS
                moleVisibleMs = (INITIAL_VISIBLE_MS -
                        (INITIAL_VISIBLE_MS - MIN_VISIBLE_MS) * progress).toLong()
                moleIntervalMs = (INITIAL_INTERVAL_MS -
                        (INITIAL_INTERVAL_MS - MIN_INTERVAL_MS) * progress).toLong()
            }

            override fun onFinish() {
                _timeLeftSec.value = 0
                hideMole()
                _gameState.value = GameState.FINISHED
            }
        }.start()
    }

    private fun scheduleMole() {
        moleRunnable?.let { handler.removeCallbacks(it) }
        moleRunnable = Runnable {
            if (_gameState.value == GameState.PLAYING) {
                showRandomMole()
                handler.postDelayed({
                    if (_gameState.value == GameState.PLAYING) {
                        hideMole()
                        handler.postDelayed({ scheduleMole() }, moleIntervalMs.coerceAtLeast(100L))
                    }
                }, moleVisibleMs)
            }
        }
        handler.postDelayed(moleRunnable!!, moleIntervalMs.coerceAtLeast(100L))
    }

    private fun showRandomMole() {
        var newIndex: Int
        do {
            newIndex = (0 until HOLE_COUNT).random()
        } while (newIndex == currentMoleIndex)
        currentMoleIndex = newIndex
        _activeMoleIndex.value = newIndex
    }

    private fun hideMole() {
        currentMoleIndex = -1
        _activeMoleIndex.value = -1
    }

    fun onHoleTapped(index: Int) {
        if (_gameState.value != GameState.PLAYING) return
        if (index == currentMoleIndex) {
            _score.value = (_score.value ?: 0) + 1
            _hitIndex.value = index
            currentMoleIndex = -1
            moleRunnable?.let { handler.removeCallbacks(it) }
            // Short delay before next mole after a hit
            handler.postDelayed({ scheduleMole() }, 300L)
        }
    }

    fun saveScore(playerName: String) {
        val character = selectedCharacter ?: return
        val points = _score.value ?: 0
        viewModelScope.launch {
            val id = repository.insertScore(
                Score(
                    playerName = playerName.trim(),
                    characterId = character.id,
                    characterName = character.displayName,
                    points = points
                )
            )
            _savedScoreId.postValue(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        moleRunnable?.let { handler.removeCallbacks(it) }
    }
}
