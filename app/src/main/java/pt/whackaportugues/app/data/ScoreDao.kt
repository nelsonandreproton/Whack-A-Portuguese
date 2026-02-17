package pt.whackaportugues.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pt.whackaportugues.app.model.Score

@Dao
interface ScoreDao {

    @Query("SELECT * FROM scores ORDER BY points DESC LIMIT 50")
    fun getAllScores(): LiveData<List<Score>>

    @Query("SELECT * FROM scores WHERE characterId = :characterId ORDER BY points DESC LIMIT 10")
    fun getTopScoresForCharacter(characterId: Int): LiveData<List<Score>>

    @Query("SELECT * FROM scores ORDER BY points DESC LIMIT 10")
    fun getGlobalTopScores(): LiveData<List<Score>>

    @Query("SELECT MAX(points) FROM scores WHERE characterId = :characterId")
    suspend fun getBestScoreForCharacter(characterId: Int): Int?

    @Insert
    suspend fun insertScore(score: Score): Long
}
