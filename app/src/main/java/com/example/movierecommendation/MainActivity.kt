package com.example.movierecommendation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.movierecommendation.data.AppDatabase
import com.example.movierecommendation.repository.MovieRepository
import com.example.movierecommendation.ui.navigation.AppNavHost
import com.example.movierecommendation.ui.theme.MovieRecommendationTheme
import com.example.movierecommendation.viewmodel.MoviesViewModel
import com.example.movierecommendation.viewmodel.MoviesViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = AppDatabase.getInstance(applicationContext).movieDao()
        val repository = MovieRepository(dao)
        val factory = MoviesViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[MoviesViewModel::class.java]

        setContent {
            MovieRecommendationTheme {
                AppNavHost(viewModel)
            }
        }
    }
}
