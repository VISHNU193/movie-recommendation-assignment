package com.example.movierecommendation.network

import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.movierecommendation.BuildConfig
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val TAG = "TMDB_API"

// Simple models used for listing (trimmed fields)
@JsonClass(generateAdapter = true)
data class Movie(
    val id: Int,
    val title: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val overview: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "vote_average") val voteAverage: Double?,
    @Json(name = "genre_ids") val genre_ids: List<Int>?
)

@JsonClass(generateAdapter = true)
data class MovieResult(
    val page: Int,
    val results: List<Movie>,
    @Json(name = "total_pages") val total_pages: Int,
    @Json(name = "total_results") val total_results: Int
)

@JsonClass(generateAdapter = true)
data class Genre(val id: Int, val name: String)

@JsonClass(generateAdapter = true)
data class MovieDetail(
    val id: Int,
    val title: String?,
    val overview: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "vote_average") val voteAverage: Double?,
    val genres: List<Genre>?
)

interface TmdbApi {
    @GET("movie/popular")
    suspend fun popular(@Query("language") language: String = "en-US",
                        @Query("page") page: Int = 1): MovieResult

    @GET("trending/movie/day")
    suspend fun trending(): MovieResult

    @GET("search/movie")
    suspend fun search(@Query("query") query: String,
                       @Query("page") page: Int = 1): MovieResult

    @GET("movie/{movie_id}")
    suspend fun movieDetails(@Path("movie_id") id: Int,
                             @Query("language") language: String = "en-US"): MovieDetail

    @GET("movie/{movie_id}/recommendations")
    suspend fun recommendations(@Path("movie_id") id: Int,
                                @Query("page") page: Int = 1): MovieResult

    @GET("movie/{movie_id}/similar")
    suspend fun similar(@Path("movie_id") id: Int,
                        @Query("page") page: Int = 1): MovieResult
}

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val loggingInterceptor = HttpLoggingInterceptor { message ->
    Log.d(TAG, message)
}.apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val authInterceptor = okhttp3.Interceptor { chain ->
    val originalRequest = chain.request()
    val requestWithAuth = originalRequest.newBuilder()
        .header("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
        .header("accept", "application/json")
        .build()
    chain.proceed(requestWithAuth)
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(authInterceptor)
    .addInterceptor(loggingInterceptor)
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

object TmdbService {
    val api: TmdbApi by lazy {
        Log.d(TAG, "Initializing TMDB API with key: ${BuildConfig.TMDB_API_KEY.take(5)}...")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TmdbApi::class.java)
    }
}
