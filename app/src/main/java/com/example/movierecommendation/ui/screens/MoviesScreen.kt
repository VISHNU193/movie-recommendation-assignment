package com.example.movierecommendation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movierecommendation.network.Movie
import com.example.movierecommendation.viewmodel.MoviesViewModel
import com.example.movierecommendation.viewmodel.PaginatedMovies
import com.example.movierecommendation.viewmodel.UiState

private val genreMap = mapOf(
    28 to "Action",
    12 to "Adventure",
    16 to "Animation",
    35 to "Comedy",
    80 to "Crime",
    99 to "Documentary",
    18 to "Drama",
    10751 to "Family",
    14 to "Fantasy",
    36 to "History",
    27 to "Horror",
    10402 to "Music",
    9648 to "Mystery",
    10749 to "Romance",
    878 to "Sci-Fi",
    10770 to "TV Movie",
    53 to "Thriller",
    10752 to "War",
    37 to "Western"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Int) -> Unit
) {
    val popularState by viewModel.popular.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<UiState<List<Movie>>?>(null) }
    val focusManager = LocalFocusManager.current
    val gridState = rememberLazyGridState()

    val favoriteIds = favorites.map { it.id }.toSet()
    val watchlistIds = watchlist.map { it.id }.toSet()

    // Infinite scroll detection
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            
            // Load more when we're 6 items away from the end
            lastVisibleItemIndex > (totalItems - 6) && totalItems > 0
        }
    }

    // Trigger load more when scrolled near the end
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && searchResults == null) {
            viewModel.loadMorePopular()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Page Title
        Text(
            text = if (searchResults != null) "Search Results" else "Popular Movies",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                if (it.isEmpty()) {
                    searchResults = null
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search movies...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        searchResults = null
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.search(searchQuery) { result ->
                            searchResults = result
                        }
                        focusManager.clearFocus()
                    }
                }
            )
        )

        // Display search results if searching
        if (searchResults != null) {
            when (val state = searchResults) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(message = state.message) {
                    viewModel.search(searchQuery) { searchResults = it }
                }
                is UiState.Success -> {
                    val movies = state.data
                    if (movies.isEmpty()) {
                        EmptyState(message = "No movies found for \"$searchQuery\"")
                    } else {
                        MovieGrid(
                            movies = movies,
                            favoriteIds = favoriteIds,
                            watchlistIds = watchlistIds,
                            onMovieClick = onMovieClick,
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onWatchlistClick = { viewModel.toggleWatchlist(it) },
                            isLoadingMore = false,
                            gridState = gridState
                        )
                    }
                }
                null -> {}
            }
        } else {
            // Display popular movies with pagination
            when (val state = popularState) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(message = state.message) {
                    viewModel.loadPopular()
                }
                is UiState.Success -> {
                    val paginatedData = state.data
                    if (paginatedData.movies.isEmpty()) {
                        EmptyState(message = "No movies available")
                    } else {
                        MovieGrid(
                            movies = paginatedData.movies,
                            favoriteIds = favoriteIds,
                            watchlistIds = watchlistIds,
                            onMovieClick = onMovieClick,
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onWatchlistClick = { viewModel.toggleWatchlist(it) },
                            isLoadingMore = paginatedData.isLoadingMore,
                            gridState = gridState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieGrid(
    movies: List<Movie>,
    favoriteIds: Set<Int>,
    watchlistIds: Set<Int>,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Movie) -> Unit,
    onWatchlistClick: (Movie) -> Unit,
    isLoadingMore: Boolean,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = movies.size,
            key = { index -> "${movies[index].id}_$index" }
        ) { index ->
            val movie = movies[index]
            MovieCard(
                movie = movie,
                isFavorite = favoriteIds.contains(movie.id),
                isInWatchlist = watchlistIds.contains(movie.id),
                onMovieClick = { onMovieClick(movie.id) },
                onFavoriteClick = { onFavoriteClick(movie) },
                onWatchlistClick = { onWatchlistClick(movie) }
            )
        }
        
        // Loading indicator at the bottom
        if (isLoadingMore) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    isFavorite: Boolean,
    isInWatchlist: Boolean,
    onMovieClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onMovieClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Movie Poster
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 150f
                        )
                    )
            )

            // Action buttons
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onWatchlistClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (isInWatchlist) Color(0xFFFFD700) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Movie info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = movie.title ?: "Unknown",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Genre chips
                movie.genre_ids?.take(2)?.let { genres ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        genres.forEach { genreId ->
                            genreMap[genreId]?.let { genreName ->
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = genreName,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading movies...")
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "😕",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Oops! Something went wrong",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🎬",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
