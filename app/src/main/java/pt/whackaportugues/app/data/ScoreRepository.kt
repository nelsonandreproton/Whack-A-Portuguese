package pt.whackaportugues.app.data

import androidx.lifecycle.LiveData
import pt.whackaportugues.app.model.Score

class ScoreRepository(private val dao: ScoreDao) {

    fun getGlobalTopScores(): LiveData<List<Score>> = dao.getGlobalTopScores()

    fun getTopScoresForCharacter(characterId: Int): LiveData<List<Score>> =
        dao.getTopScoresForCharacter(characterId)

    suspend fun getBestScoreForCharacter(characterId: Int): Int? =
        dao.getBestScoreForCharacter(characterId)

    suspend fun insertScore(score: Score): Long = dao.insertScore(score)
}
