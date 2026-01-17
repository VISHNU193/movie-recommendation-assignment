package com.example.movierecommendation.data


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM saved_movies WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM saved_movies WHERE isWatchlist = 1")
    fun getWatchlist(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Update
    suspend fun update(movie: MovieEntity)

    @Query("DELETE FROM saved_movies WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM saved_movies WHERE id = :id")
    suspend fun getById(id: Int): MovieEntity?
}
