package com.filmbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.filmbox.adapter.EpisodeAdapter;
import com.filmbox.model.Season;
import com.filmbox.model.SeasonDetail;
import com.filmbox.model.TvShowDetail;
import com.filmbox.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import coil.Coil;
import coil.request.ImageRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivBackdrop;
    private TextView tvTitle, tvRating, tvDate, tvOverview, tvSeasonsLabel;
    private Button btnWatchNow;
    private Spinner spinnerSeasons;
    private RecyclerView recyclerViewEpisodes;
    private EpisodeAdapter episodeAdapter;

    private int tvShowId = -1;
    private int selectedSeasonNumber = 1;
    private List<Season> seasonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_detail);

            ivBackdrop = findViewById(R.id.ivBackdrop);
            tvTitle = findViewById(R.id.tvTitle);
            tvRating = findViewById(R.id.tvRating);
            tvDate = findViewById(R.id.tvDate);
            tvOverview = findViewById(R.id.tvOverview);
            tvSeasonsLabel = findViewById(R.id.tvSeasonsLabel);
            btnWatchNow = findViewById(R.id.btnWatchNow);
            spinnerSeasons = findViewById(R.id.spinnerSeasons);
            recyclerViewEpisodes = findViewById(R.id.recyclerViewEpisodes);

            if (recyclerViewEpisodes != null) {
                recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(this));
            }

            String title = "Movie";
            String overview = "";
            String backdrop = "";
            String date = "2024";
            double rating = 7.5;
            String passVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            int tmdbId = 27205;

            if (getIntent() != null) {
                if (getIntent().getStringExtra("title") != null) title = getIntent().getStringExtra("title");
                if (getIntent().getStringExtra("overview") != null) overview = getIntent().getStringExtra("overview");
                if (getIntent().getStringExtra("backdrop") != null) backdrop = getIntent().getStringExtra("backdrop");
                if (getIntent().getStringExtra("date") != null) date = getIntent().getStringExtra("date");
                rating = getIntent().getDoubleExtra("rating", 7.5);
                tvShowId = getIntent().getIntExtra("tv_id", -1);
                tmdbId = getIntent().getIntExtra("tmdb_id", tvShowId != -1 ? tvShowId : 27205);
                if (getIntent().getStringExtra("video_url") != null) passVideoUrl = getIntent().getStringExtra("video_url");
            }

            if (tvTitle != null) tvTitle.setText(title);
            if (tvOverview != null) tvOverview.setText(overview);
            if (tvDate != null) tvDate.setText(date);
            if (tvRating != null) tvRating.setText("⭐ " + rating);

            if (ivBackdrop != null && backdrop != null && !backdrop.isEmpty()) {
                try {
                    ImageRequest request = new ImageRequest.Builder(this)
                            .data(backdrop)
                            .target(ivBackdrop)
                            .crossfade(true)
                            .build();
                    Coil.imageLoader(this).enqueue(request);
                } catch (Exception ignored) {}
            }

            final String finalTitle = title;
            final int finalTmdbId = tmdbId;
            final String finalVideoUrl = passVideoUrl;

            episodeAdapter = new EpisodeAdapter(episode -> {
                try {
                    Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
                    intent.putExtra("title", finalTitle + " - Ep " + episode.getEpisodeNumber());
                    intent.putExtra("tmdb_id", finalTmdbId);
                    intent.putExtra("media_type", "tv");
                    intent.putExtra("season_number", selectedSeasonNumber);
                    intent.putExtra("episode_number", episode.getEpisodeNumber());
                    intent.putExtra("video_url", finalVideoUrl);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, "Error playing episode", Toast.LENGTH_SHORT).show();
                }
            });
            if (recyclerViewEpisodes != null) {
                recyclerViewEpisodes.setAdapter(episodeAdapter);
            }

            View.OnClickListener playListener = v -> {
                try {
                    Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
                    intent.putExtra("title", finalTitle);
                    intent.putExtra("tmdb_id", finalTmdbId);
                    intent.putExtra("media_type", tvShowId != -1 ? "tv" : "movie");
                    intent.putExtra("season_number", selectedSeasonNumber);
                    intent.putExtra("episode_number", 1);
                    intent.putExtra("video_url", finalVideoUrl != null && !finalVideoUrl.isEmpty() ? finalVideoUrl : "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, "Error starting player", Toast.LENGTH_SHORT).show();
                }
            };

            if (btnWatchNow != null) btnWatchNow.setOnClickListener(playListener);
            if (ivBackdrop != null) ivBackdrop.setOnClickListener(playListener);

            if (tvShowId != -1) {
                if (tvSeasonsLabel != null) tvSeasonsLabel.setVisibility(View.VISIBLE);
                if (spinnerSeasons != null) spinnerSeasons.setVisibility(View.VISIBLE);
                loadTvShowDetails(tvShowId);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadTvShowDetails(int tvId) {
        try {
            ApiClient.getApiService().getTvShowDetails(tvId, ApiClient.API_KEY, "en-US")
                    .enqueue(new Callback<TvShowDetail>() {
                        @Override
                        public void onResponse(Call<TvShowDetail> call, Response<TvShowDetail> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    TvShowDetail detail = response.body();
                                    seasonList = detail.getSeasons();
                                    setupSeasonSpinner();
                                }
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onFailure(Call<TvShowDetail> call, Throwable t) {}
                    });
        } catch (Exception ignored) {}
    }

    private void setupSeasonSpinner() {
        try {
            List<String> seasonNames = new ArrayList<>();
            for (Season season : seasonList) {
                seasonNames.add(season.getName() + " (" + season.getEpisodeCount() + " Episodes)");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, seasonNames);
            if (spinnerSeasons != null) {
                spinnerSeasons.setAdapter(adapter);
                spinnerSeasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Season selectedSeason = seasonList.get(position);
                            selectedSeasonNumber = selectedSeason.getSeasonNumber();
                            loadEpisodes(tvShowId, selectedSeasonNumber);
                        } catch (Exception ignored) {}
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        } catch (Exception ignored) {}
    }

    private void loadEpisodes(int tvId, int seasonNumber) {
        try {
            ApiClient.getApiService().getSeasonDetails(tvId, seasonNumber, ApiClient.API_KEY, "en-US")
                    .enqueue(new Callback<SeasonDetail>() {
                        @Override
                        public void onResponse(Call<SeasonDetail> call, Response<SeasonDetail> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    SeasonDetail seasonDetail = response.body();
                                    if (episodeAdapter != null && seasonDetail.getEpisodes() != null) {
                                        episodeAdapter.setEpisodes(seasonDetail.getEpisodes());
                                    }
                                }
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onFailure(Call<SeasonDetail> call, Throwable t) {}
                    });
        } catch (Exception ignored) {}
    }
}
