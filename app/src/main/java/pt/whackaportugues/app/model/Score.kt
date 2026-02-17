package pt.whackaportugues.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerName: String,
    val characterId: Int,
    val characterName: String,
    val points: Int,
    val timestamp: Long = System.currentTimeMillis()
)
