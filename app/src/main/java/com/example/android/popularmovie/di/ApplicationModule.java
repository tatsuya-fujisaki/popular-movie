package com.example.android.popularmovie.di;

import android.app.Application;
import android.content.Context;

import com.example.android.popularmovie.R;
import com.example.android.popularmovie.TmdbService;
import com.example.android.popularmovie.data.Movie;
import com.example.android.popularmovie.data.MovieDatabase;
import com.example.android.popularmovie.data.MovieRepository;
import com.example.android.popularmovie.data.Review;
import com.example.android.popularmovie.data.ReviewDao;
import com.example.android.popularmovie.data.ReviewRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class ApplicationModule {
    @Singleton
    @Provides
    static Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Singleton
    @Provides
    static MovieDatabase provideMovieDatabase(Context context) {
        return MovieDatabase.getInstance(context);
    }

    @Singleton
    @Provides
    static ReviewDao provideReviewDao(MovieDatabase movieDatabase) {
        return movieDatabase.reviewDao();
    }

    @Singleton
    @Provides
    @Named("GsonWithLocalDateAdapter")
    static Gson provideGsonWithLocalDateAdapter() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json1, type1, context) -> LocalDate.parse(json1.getAsJsonPrimitive().getAsString()))
                .create();
    }

    @Singleton
    @Provides
    @Named("GsonWithMovieArrayAdapter")
    static Gson provideGsonWithMovieArrayAdapter(Context context, @Named("GsonWithLocalDateAdapter") Gson gson) {
        return new GsonBuilder()
                .registerTypeAdapter(Movie[].class, (JsonDeserializer<Movie[]>) (json, type, context1) -> gson.fromJson(json.getAsJsonObject().getAsJsonArray(context.getString(R.string.tmdb_json_results_element)), type))
                .create();
    }

    @Singleton
    @Provides
    @Named("GsonWithReviewArrayAdapter")
    static Gson provideGsonWithReviewArrayAdapter(Context context, @Named("GsonWithLocalDateAdapter") Gson gson) {
        return new GsonBuilder()
                .registerTypeAdapter(Review[].class, (JsonDeserializer<Review[]>) (json, type, context1) -> gson.fromJson(json.getAsJsonObject().getAsJsonArray(context.getString(R.string.tmdb_json_results_element)), type))
                .create();
    }

    @Singleton
    @Provides
    @Named("TmdbServiceWithMovieArrayAdapter")
    static TmdbService provideTmdbServiceWithMovieArrayAdapter(Context context, @Named("GsonWithMovieArrayAdapter") Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.tmdb_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(TmdbService.class);
    }

    @Singleton
    @Provides
    @Named("TmdbServiceWithReviewArrayAdapter")
    static TmdbService provideTmdbServiceWithReviewArrayAdapter(Context context, @Named("GsonWithReviewArrayAdapter") Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.tmdb_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(TmdbService.class);
    }

    @Singleton
    @Provides
    static MovieRepository provideMovieRepository(Context context, @Named("TmdbServiceWithMovieArrayAdapter") TmdbService tmdbService, MovieDatabase movieDatabase, Executor executor) {
        return new MovieRepository(tmdbService, movieDatabase, executor, context.getString(R.string.poster_base_url));
    }

    @Singleton
    @Provides
    static ReviewRepository provideReviewRepository(@Named("TmdbServiceWithReviewArrayAdapter") TmdbService tmdbService, ReviewDao reviewDao, Executor executor) {
        return new ReviewRepository(tmdbService, reviewDao, executor);
    }
}