package pt.whackaportugues.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pt.whackaportugues.app.data.ScoreDatabase
import pt.whackaportugues.app.data.ScoreRepository
import pt.whackaportugues.app.model.Score

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScoreRepository
    val globalTopScores: LiveData<List<Score>>

    init {
        val dao = ScoreDatabase.getDatabase(application).scoreDao()
        repository = ScoreRepository(dao)
        globalTopScores = repository.getGlobalTopScores()
    }

    fun getTopScoresForCharacter(characterId: Int): LiveData<List<Score>> =
        repository.getTopScoresForCharacter(characterId)
}
