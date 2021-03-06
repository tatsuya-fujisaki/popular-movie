package com.example.android.popularmovie.ui.activity;

import android.content.Intent;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.android.popularmovie.R;
import com.example.android.popularmovie.databinding.ActivityDetailBinding;
import com.example.android.popularmovie.room.entity.Movie;
import com.example.android.popularmovie.ui.fragment.OverviewFragment;
import com.example.android.popularmovie.ui.fragment.ReviewFragment;
import com.example.android.popularmovie.ui.fragment.TrailerFragment;
import com.example.android.popularmovie.util.NetworkUtils;
import com.example.android.popularmovie.util.ui.IntentUtils;
import com.example.android.popularmovie.viewmodel.MovieViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.example.android.popularmovie.ui.activity.MainActivity.MOVIE_ID_INT_EXTRA_KEY;

public class DetailActivity extends AppCompatActivity {
    public static final String MOVIE_PARCELABLE_EXTRA_KEY = null;

    @Inject
    MovieViewModel movieViewModel;

    private Movie movie;
    private boolean originalFavorite;

    @BindingAdapter("android:src")
    public static void setStringToImageView(ImageView imageView, String path) {
        NetworkUtils.picasso(path).into(imageView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.viewPager.setAdapter(new Adapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        Objects.requireNonNull(binding.tabLayout.getTabAt(0)).setIcon(R.drawable.ic_home);
        Objects.requireNonNull(binding.tabLayout.getTabAt(1)).setIcon(R.drawable.ic_movie);
        Objects.requireNonNull(binding.tabLayout.getTabAt(2)).setIcon(R.drawable.ic_thumb_up);

        movie = IntentUtils.getParcelableExtra(this, MOVIE_PARCELABLE_EXTRA_KEY);

        /*
         * savedInstanceState.containsKey(null) is required to skip the case that network was unavailable and then the device was rotated,
         * In the case, savedInstanceState is not null but contains no key.
         */
        if (savedInstanceState != null && savedInstanceState.containsKey(null)) {
            movie.isFavorite = savedInstanceState.getBoolean(null);
        }

        originalFavorite = Objects.requireNonNull(movie).isFavorite;
        binding.setMovie(movie);
        setFabImage(binding.fab);

        binding.fab.setOnClickListener(view -> {
            movie.isFavorite = !movie.isFavorite;
            setFabImage((FloatingActionButton) view);
            AsyncTask.execute(() -> movieViewModel.updateFavorite(movie.id, movie.isFavorite));
        });

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(movie.originalTitle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && originalFavorite != movie.isFavorite) {
            setResult(RESULT_OK, new Intent().putExtra(MOVIE_ID_INT_EXTRA_KEY, movie.id));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(null, movie.isFavorite);
        super.onSaveInstanceState(outState);
    }

    private void setFabImage(FloatingActionButton fab) {
        fab.setImageResource(movie.isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    private class Adapter extends FragmentPagerAdapter {
        private final List<Pair<Fragment, Integer>> pairs =
                Arrays.asList(Pair.create(new OverviewFragment(), R.string.overview_tab),
                        Pair.create(new TrailerFragment(), R.string.trailers_tab),
                        Pair.create(new ReviewFragment(), R.string.reviews_tabs));

        private Adapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return pairs.get(position).first;
        }

        @Override
        public int getCount() {
            return pairs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(pairs.get(position).second);
        }
    }
}