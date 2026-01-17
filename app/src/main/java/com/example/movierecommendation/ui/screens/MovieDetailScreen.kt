package com.example.movierecommendation.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.movierecommendation.R
import com.example.movierecommendation.network.MovieDetail
import com.example.movierecommendation.viewmodel.MoviesViewModel
import com.example.movierecommendation.viewmodel.UiState
import kotlinx.coroutines.launch

private const val CHANNEL_ID = "movie_playing_channel"

@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MoviesViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val detailState by viewModel.movieDetail.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val isFavorite = favorites.any { it.id == movieId }
    val isInWatchlist = watchlist.any { it.id == movieId }

    // Permission launcher for notifications (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val movie = (detailState as? UiState.Success<MovieDetail>)?.data
            movie?.let {
                showMoviePlayingNotification(context, it.title ?: "Movie")
            }
        }
    }

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetail(movieId)
    }

    // Create notification channel
    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (detailState) {
                is UiState.Loading -> {
                    LoadingState()
                }
                is UiState.Error -> {
                    ErrorState(message = (detailState as UiState.Error).message) {
                        viewModel.loadMovieDetail(movieId)
                    }
                }
                is UiState.Success -> {
                    val movie = (detailState as UiState.Success<MovieDetail>).data
                    MovieDetailContent(
                        movie = movie,
                        isFavorite = isFavorite,
                        isInWatchlist = isInWatchlist,
                        onBack = onBack,
                        onFavoriteClick = { viewModel.toggleFavoriteFromDetail(movie) },
                        onWatchlistClick = { viewModel.toggleWatchlistFromDetail(movie) },
                        onPlayClick = {
                            // Show in-app Snackbar notification
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "🎬 ${movie.title ?: "Movie"} is Playing",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            // Also try to show system notification
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    showMoviePlayingNotification(context, movie.title ?: "Movie")
                                } else {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                showMoviePlayingNotification(context, movie.title ?: "Movie")
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieDetailContent(
    movie: MovieDetail,
    isFavorite: Boolean,
    isInWatchlist: Boolean,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Banner with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w780${movie.backdropPath ?: movie.posterPath}",
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
                                    Color.Black.copy(alpha = 0.7f),
                                    MaterialTheme.colorScheme.background
                                ),
                                startY = 100f
                            )
                        )
                )

                // Back button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Rating circle
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    CircularScoreIndicator(
                        score = movie.voteAverage ?: 0.0,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Title
                Text(
                    text = movie.title ?: "Unknown Title",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Release Date
                movie.releaseDate?.let { date ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Release Date: $date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Genres
                movie.genres?.let { genres ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        genres.take(4).forEach { genre ->
                            AssistChip(
                                onClick = { },
                                label = { Text(genre.name, fontSize = 12.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Play Now Button
                    Button(
                        onClick = onPlayClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Play Now")
                    }

                    // Favorite Button
                    OutlinedButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Watchlist Button
                    OutlinedButton(
                        onClick = onWatchlistClick,
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isInWatchlist) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Overview
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = movie.overview ?: "No description available.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun CircularScoreIndicator(
    score: Double,
    modifier: Modifier = Modifier
) {
    val percentage = (score / 10f).toFloat()
    val color = when {
        score >= 7 -> Color(0xFF21D07A)
        score >= 5 -> Color(0xFFD2D531)
        else -> Color(0xFFDB2360)
    }

    Box(
        modifier = modifier
            .background(Color(0xFF081C22), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .size(60.dp)
                .padding(5.dp),
            color = color,
            strokeWidth = 4.dp,
            trackColor = color.copy(alpha = 0.3f),
            strokeCap = StrokeCap.Round
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${(score * 10).toInt()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "%",
                    color = Color.White,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Movie Playing"
        val descriptionText = "Notifications when a movie starts playing"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun showMoviePlayingNotification(context: Context, movieTitle: String) {
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Now Playing")
        .setContentText("$movieTitle is Playing")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    try {
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    } catch (e: SecurityException) {
        // Handle missing notification permission
    }
}
