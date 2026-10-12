package com.filmbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.filmbox.adapter.BannerAdapter;
import com.filmbox.adapter.GenreAdapter;
import com.filmbox.adapter.MediaCardAdapter;
import com.filmbox.auth.LoginActivity;
import com.filmbox.auth.UserSession;
import com.filmbox.model.Genre;
import com.filmbox.model.Movie;
import com.filmbox.model.MovieResponse;
import com.filmbox.model.TvShow;
import com.filmbox.network.ApiClient;
import com.filmbox.repository.MovieRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MediaCardAdapter.OnCardClickListener, BannerAdapter.OnBannerClickListener, GenreAdapter.OnGenreClickListener {

    private TextView tvWelcomeUser;
    private ViewPager2 viewPagerBanner;
    private EditText etSearch;
    private RecyclerView recyclerViewGenres;
    private RecyclerView rvTrending, rvPopularMovies, rvPopularTv, rvAction, rvHorror;
    private BottomNavigationView bottomNavigation;

    private BannerAdapter bannerAdapter;
    private GenreAdapter genreAdapter;

    private MediaCardAdapter adapterTrending, adapterMovies, adapterTv, adapterAction, adapterHorror;

    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = new UserSession(this);
        if (!userSession.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        etSearch = findViewById(R.id.etSearch);
        recyclerViewGenres = findViewById(R.id.recyclerViewGenres);
        rvTrending = findViewById(R.id.rvTrending);
        rvPopularMovies = findViewById(R.id.rvPopularMovies);
        rvPopularTv = findViewById(R.id.rvPopularTv);
        rvAction = findViewById(R.id.rvAction);
        rvHorror = findViewById(R.id.rvHorror);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        tvWelcomeUser.setText("Hi, " + userSession.getUserName() + " ✨");

        btnLogout.setOnClickListener(v -> {
            userSession.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        bannerAdapter = new BannerAdapter(this);
        viewPagerBanner.setAdapter(bannerAdapter);

        recyclerViewGenres.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        genreAdapter = new GenreAdapter(this);
        recyclerViewGenres.setAdapter(genreAdapter);
        setupGenreList();

        setupCarousels();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    populateSections();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                populateSections();
                return true;
            } else if (itemId == R.id.nav_movies) {
                adapterMovies.setItems(MovieRepository.getDefaultMovies());
                Toast.makeText(this, "Showing All Movies", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_tv) {
                adapterTv.setItems(MovieRepository.getDefaultTvShows());
                Toast.makeText(this, "Showing TV Series & Seasons", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_downloads) {
                startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Profile: " + userSession.getUserName() + " (" + userSession.getUserEmail() + ") - VIP Member", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        });

        populateSections();
    }

    private void setupCarousels() {
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterTrending = new MediaCardAdapter(this);
        rvTrending.setAdapter(adapterTrending);

        rvPopularMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterMovies = new MediaCardAdapter(this);
        rvPopularMovies.setAdapter(adapterMovies);

        rvPopularTv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterTv = new MediaCardAdapter(this);
        rvPopularTv.setAdapter(adapterTv);

        rvAction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterAction = new MediaCardAdapter(this);
        rvAction.setAdapter(adapterAction);

        rvHorror.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterHorror = new MediaCardAdapter(this);
        rvHorror.setAdapter(adapterHorror);
    }

    private void populateSections() {
        List<Movie> allMovies = MovieRepository.getDefaultMovies();
        List<TvShow> allTv = MovieRepository.getDefaultTvShows();

        bannerAdapter.setMovies(allMovies.subList(0, Math.min(5, allMovies.size())));
        adapterTrending.setItems(allMovies.subList(0, Math.min(10, allMovies.size())));
        adapterMovies.setItems(allMovies);
        adapterTv.setItems(allTv);

        List<Movie> actionList = new ArrayList<>();
        List<Movie> horrorList = new ArrayList<>();
        for (int i = 0; i < allMovies.size(); i++) {
            if (i % 2 == 0) actionList.add(allMovies.get(i));
            else horrorList.add(allMovies.get(i));
        }
        adapterAction.setItems(actionList);
        adapterHorror.setItems(horrorList);

        ApiClient.getApiService().getPopularMovies(ApiClient.API_KEY, "en-US")
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                            List<Movie> liveMovies = response.body().getResults();
                            adapterMovies.setItems(liveMovies);
                            adapterTrending.setItems(liveMovies.subList(0, Math.min(10, liveMovies.size())));
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {}
                });
    }

    private void performSearch(String query) {
        List<Movie> localMatched = new ArrayList<>();
        for (Movie m : MovieRepository.getDefaultMovies()) {
            if (m.getTitle() != null && m.getTitle().toLowerCase().contains(query.toLowerCase())) {
                localMatched.add(m);
            }
        }
        if (!localMatched.isEmpty()) {
            adapterTrending.setItems(localMatched);
            adapterMovies.setItems(localMatched);
        }

        ApiClient.getApiService().searchMulti(ApiClient.API_KEY, query)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                            List<Movie> liveResults = response.body().getResults();
                            adapterTrending.setItems(liveResults);
                            adapterMovies.setItems(liveResults);
                            adapterAction.setItems(liveResults);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {}
                });
    }

    private void setupGenreList() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1, "✨ All"));
        genres.add(new Genre(2, "🔥 Trending"));
        genres.add(new Genre(3, "📺 Kenyan Hits"));
        genres.add(new Genre(4, "🌍 African Movies"));
        genres.add(new Genre(5, "🎬 Hollywood"));
        genres.add(new Genre(6, "📚 Series"));
        genres.add(new Genre(7, "💥 Action"));
        genres.add(new Genre(8, "🎨 Animation"));
        genres.add(new Genre(9, "🚀 Sci-Fi"));
        genres.add(new Genre(10, "😂 Comedy"));
        genreAdapter.setGenres(genres);
    }

    @Override
    public void onGenreClick(Genre genre) {
        String name = genre.getName().toLowerCase();
        List<Movie> movies = MovieRepository.getDefaultMovies();
        List<Movie> filtered = new ArrayList<>();

        if (name.contains("kenyan")) {
            for (Movie m : movies) {
                if (m.getId() == 130737 || m.getId() == 1063242 || m.getId() == 493915 || m.getId() == 47559 || m.getId() == 1129249) {
                    filtered.add(m);
                }
            }
        } else if (name.contains("african")) {
            for (Movie m : movies) {
                if (m.getId() == 1206988 || m.getId() == 1172009 || m.getId() == 1023994 || m.getId() == 1163045 || m.getId() == 570465) {
                    filtered.add(m);
                }
            }
        } else {
            filtered = movies;
        }

        if (!filtered.isEmpty()) {
            adapterTrending.setItems(filtered);
            adapterMovies.setItems(filtered);
        } else {
            populateSections();
        }
    }

    @Override
    public void onBannerClick(Movie movie) {
        onMovieClick(movie);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("tmdb_id", movie.getId());
        intent.putExtra("media_type", "movie");
        intent.putExtra("video_url", movie.getVideoUrl());
        startActivity(intent);
    }

    @Override
    public void onTvShowClick(TvShow tvShow) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("title", tvShow.getName());
        intent.putExtra("overview", tvShow.getOverview());
        intent.putExtra("backdrop", tvShow.getBackdropPath());
        intent.putExtra("date", tvShow.getFirstAirDate());
        intent.putExtra("rating", tvShow.getVoteAverage());
        intent.putExtra("tv_id", tvShow.getId());
        intent.putExtra("tmdb_id", tvShow.getId());
        intent.putExtra("media_type", "tv");
        intent.putExtra("video_url", tvShow.getVideoUrl());
        startActivity(intent);
    }
}
