package com.example.movierecommendation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movierecommendation.repository.MovieRepository
import com.example.movierecommendation.data.MovieEntity
import com.example.movierecommendation.network.Movie
import com.example.movierecommendation.network.MovieDetail
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class PaginatedMovies(
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 1,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true
)

class MoviesViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _popular = MutableStateFlow<UiState<PaginatedMovies>>(UiState.Loading)
    val popular: StateFlow<UiState<PaginatedMovies>> = _popular.asStateFlow()

    private val _movieDetail = MutableStateFlow<UiState<MovieDetail>>(UiState.Loading)
    val movieDetail: StateFlow<UiState<MovieDetail>> = _movieDetail.asStateFlow()

    val favorites = repository.favorites().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val watchlist = repository.watchlist().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var isLoadingMore = false

    init {
        loadPopular()
    }

    fun loadPopular() {
        viewModelScope.launch {
            _popular.value = UiState.Loading
            try {
                val res = repository.getPopular(page = 1)
                _popular.value = UiState.Success(
                    PaginatedMovies(
                        movies = res.results,
                        currentPage = res.page,
                        totalPages = res.total_pages,
                        isLoadingMore = false,
                        hasMorePages = res.page < res.total_pages
                    )
                )
            } catch (e: Exception) {
                _popular.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loadMorePopular() {
        val currentState = _popular.value
        if (currentState !is UiState.Success) return
        if (isLoadingMore) return
        if (!currentState.data.hasMorePages) return

        isLoadingMore = true
        val nextPage = currentState.data.currentPage + 1

        // Update state to show loading indicator
        _popular.value = UiState.Success(currentState.data.copy(isLoadingMore = true))

        viewModelScope.launch {
            try {
                val res = repository.getPopular(page = nextPage)
                // Filter out duplicates by ID
                val existingIds = currentState.data.movies.map { it.id }.toSet()
                val newMovies = res.results.filter { it.id !in existingIds }
                val updatedMovies = currentState.data.movies + newMovies
                _popular.value = UiState.Success(
                    PaginatedMovies(
                        movies = updatedMovies,
                        currentPage = res.page,
                        totalPages = res.total_pages,
                        isLoadingMore = false,
                        hasMorePages = res.page < res.total_pages
                    )
                )
            } catch (e: Exception) {
                // Keep existing data but stop loading
                _popular.value = UiState.Success(currentState.data.copy(isLoadingMore = false))
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _movieDetail.value = UiState.Loading
            try {
                val detail = repository.getMovieDetails(movieId)
                _movieDetail.value = UiState.Success(detail)
            } catch (e: Exception) {
                _movieDetail.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun search(query: String, onResult: (UiState<List<Movie>>) -> Unit) {
        viewModelScope.launch {
            onResult(UiState.Loading)
            try {
                val res = repository.search(query)
                onResult(UiState.Success(res.results))
            } catch (e: Exception) {
                onResult(UiState.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val saved = repository.getSavedById(movie.id)
            val newFavoriteState = saved?.isFavorite?.not() ?: true
            val entity = MovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                overview = movie.overview,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage ?: 0.0,
                isFavorite = newFavoriteState,
                isWatchlist = saved?.isWatchlist ?: false
            )
            if (!newFavoriteState && !entity.isWatchlist) {
                repository.delete(movie.id)
            } else {
                repository.save(entity)
            }
        }
    }

    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch {
            val saved = repository.getSavedById(movie.id)
            val newWatchlistState = saved?.isWatchlist?.not() ?: true
            val entity = MovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                overview = movie.overview,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage ?: 0.0,
                isFavorite = saved?.isFavorite ?: false,
                isWatchlist = newWatchlistState
            )
            if (!newWatchlistState && !entity.isFavorite) {
                repository.delete(movie.id)
            } else {
                repository.save(entity)
            }
        }
    }

    fun toggleFavoriteFromDetail(movie: MovieDetail) {
        viewModelScope.launch {
            val saved = repository.getSavedById(movie.id)
            val newFavoriteState = saved?.isFavorite?.not() ?: true
            val entity = MovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                overview = movie.overview,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage ?: 0.0,
                isFavorite = newFavoriteState,
                isWatchlist = saved?.isWatchlist ?: false
            )
            if (!newFavoriteState && !entity.isWatchlist) {
                repository.delete(movie.id)
            } else {
                repository.save(entity)
            }
        }
    }

    fun toggleWatchlistFromDetail(movie: MovieDetail) {
        viewModelScope.launch {
            val saved = repository.getSavedById(movie.id)
            val newWatchlistState = saved?.isWatchlist?.not() ?: true
            val entity = MovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                overview = movie.overview,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage ?: 0.0,
                isFavorite = saved?.isFavorite ?: false,
                isWatchlist = newWatchlistState
            )
            if (!newWatchlistState && !entity.isFavorite) {
                repository.delete(movie.id)
            } else {
                repository.save(entity)
            }
        }
    }

    fun removeFromFavorites(movie: MovieEntity) {
        viewModelScope.launch {
            val updatedEntity = movie.copy(isFavorite = false)
            if (!updatedEntity.isWatchlist) {
                repository.delete(movie.id)
            } else {
                repository.save(updatedEntity)
            }
        }
    }

    fun removeFromWatchlist(movie: MovieEntity) {
        viewModelScope.launch {
            val updatedEntity = movie.copy(isWatchlist = false)
            if (!updatedEntity.isFavorite) {
                repository.delete(movie.id)
            } else {
                repository.save(updatedEntity)
            }
        }
    }
}

class MoviesViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoviesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
