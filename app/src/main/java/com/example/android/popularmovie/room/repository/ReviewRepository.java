package com.example.android.popularmovie.room.repository;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.example.android.popularmovie.BuildConfig;
import com.example.android.popularmovie.room.dao.ReviewDao;
import com.example.android.popularmovie.room.entity.Review;
import com.example.android.popularmovie.util.ApiResponse;
import com.example.android.popularmovie.util.MyDateUtils;
import com.example.android.popularmovie.util.TmdbService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    private final TmdbService tmdbService;
    private final ReviewDao reviewDao;
    private String errorMessage;
    private final SparseArray<Long> lastUpdates = new SparseArray<>();

    public ReviewRepository(TmdbService tmdbService, ReviewDao reviewDao) {
        this.tmdbService = tmdbService;
        this.reviewDao = reviewDao;
    }

    public ApiResponse<LiveData<List<Review>>> getReviews(int movieId) {
        errorMessage = null;

        if (hasExpired(movieId)) {
            tmdbService.getReviews(movieId, BuildConfig.THE_MOVIE_DATABASE_API_KEY).enqueue(new Callback<List<Review>>() {
                @Override
                public void onResponse(@NonNull Call<List<Review>> call, @NonNull Response<List<Review>> response) {
                    if (response.isSuccessful()) {
                        List<Review> reviews = response.body();

                        for (Review review : Objects.requireNonNull(reviews)) {
                            review.movieId = movieId;
                        }

                        AsyncTask.execute(() -> reviewDao.save(reviews));

                        lastUpdates.put(movieId, System.currentTimeMillis());
                    } else {
                        try {
                            errorMessage = Objects.requireNonNull(response.errorBody()).string();
                        } catch (IOException e) {
                            errorMessage = e.getMessage();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Review>> call, @NonNull Throwable t) {
                    errorMessage = t.getMessage();
                }
            });
        }

        return errorMessage == null ? ApiResponse.success(reviewDao.load(movieId)) : ApiResponse.failure(errorMessage);
    }

    private boolean hasExpired(int movieId) {
        final int MINUTES_TO_EXPIRE = 60;
        Long lastUpdate = lastUpdates.get(movieId);
        return lastUpdate == null || MyDateUtils.Minute.hasExpired(lastUpdate, MINUTES_TO_EXPIRE);
    }
}