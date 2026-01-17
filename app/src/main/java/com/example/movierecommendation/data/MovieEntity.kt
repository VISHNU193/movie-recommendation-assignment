package com.example.movierecommendation.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val isFavorite: Boolean = false,
    val isWatchlist: Boolean = false
)
