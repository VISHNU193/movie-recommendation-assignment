package com.example.movierecommendation.repository


import com.example.movierecommendation.network.TmdbService
import com.example.movierecommendation.data.MovieDao
import com.example.movierecommendation.data.MovieEntity
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val dao: MovieDao) {

    private val api = TmdbService.api

    suspend fun getPopular() = api.popular()
    suspend fun search(query: String) = api.search(query)
    suspend fun getMovieDetails(id: Int) = api.movieDetails(id)
    suspend fun getRecommendations(id: Int) = api.recommendations(id)
    suspend fun getSimilar(id: Int) = api.similar(id)

    // Room
    suspend fun save(movie: MovieEntity) = dao.insert(movie)
    suspend fun update(movie: MovieEntity) = dao.update(movie)
    suspend fun delete(id: Int) = dao.deleteById(id)
    fun favorites(): Flow<List<MovieEntity>> = dao.getFavorites()
    fun watchlist(): Flow<List<MovieEntity>> = dao.getWatchlist()
    suspend fun getSavedById(id: Int) = dao.getById(id)
}
