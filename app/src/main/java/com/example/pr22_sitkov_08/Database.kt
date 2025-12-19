package com.example.pr22_sitkov_08

import androidx.room.*

// --- МОДЕЛИ ДЛЯ API ---
data class MovieResponse(val docs: List<MovieDTO>)

data class MovieDTO(
    val id: Int,
    val name: String?,
    val year: Int?,
    val description: String?,
    val rating: Rating?
)

data class Rating(val kp: Double)

// --- ROOM БАЗА ДАННЫХ ---
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val year: Int,
    var note: String
)

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAll(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: MovieEntity)

    @Update
    fun update(movie: MovieEntity)

    @Delete
    fun delete(movie: MovieEntity)
}

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}