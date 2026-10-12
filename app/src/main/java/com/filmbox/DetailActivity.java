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

        recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(this));

        String title = getIntent().getStringExtra("title");
        String overview = getIntent().getStringExtra("overview");
        String backdrop = getIntent().getStringExtra("backdrop");
        String date = getIntent().getStringExtra("date");
        double rating = getIntent().getDoubleExtra("rating", 0.0);
        tvShowId = getIntent().getIntExtra("tv_id", -1);
        int tmdbId = getIntent().getIntExtra("tmdb_id", tvShowId != -1 ? tvShowId : 27205);
        String passVideoUrl = getIntent().getStringExtra("video_url");

        tvTitle.setText(title);
        tvOverview.setText(overview);
        tvDate.setText(date);
        tvRating.setText("⭐ " + rating);

        try {
            ImageRequest request = new ImageRequest.Builder(this)
                    .data(backdrop)
                    .target(ivBackdrop)
                    .crossfade(true)
                    .build();
            Coil.imageLoader(this).enqueue(request);
        } catch (Exception ignored) {}

        episodeAdapter = new EpisodeAdapter(episode -> {
            Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
            intent.putExtra("title", title + " - Ep " + episode.getEpisodeNumber());
            intent.putExtra("tmdb_id", tmdbId);
            intent.putExtra("media_type", "tv");
            intent.putExtra("season_number", selectedSeasonNumber);
            intent.putExtra("episode_number", episode.getEpisodeNumber());
            intent.putExtra("video_url", passVideoUrl);
            startActivity(intent);
        });
        recyclerViewEpisodes.setAdapter(episodeAdapter);

        View.OnClickListener playListener = v -> {
            Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("tmdb_id", tmdbId);
            intent.putExtra("media_type", tvShowId != -1 ? "tv" : "movie");
            intent.putExtra("season_number", selectedSeasonNumber);
            intent.putExtra("episode_number", 1);
            intent.putExtra("video_url", passVideoUrl != null && !passVideoUrl.isEmpty() ? passVideoUrl : "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
            startActivity(intent);
        };

        btnWatchNow.setOnClickListener(playListener);
        ivBackdrop.setOnClickListener(playListener);

        if (tvShowId != -1) {
            tvSeasonsLabel.setVisibility(View.VISIBLE);
            spinnerSeasons.setVisibility(View.VISIBLE);
            loadTvShowDetails(tvShowId);
        }
    }

    private void loadTvShowDetails(int tvId) {
        ApiClient.getApiService().getTvShowDetails(tvId, ApiClient.API_KEY, "en-US")
                .enqueue(new Callback<TvShowDetail>() {
                    @Override
                    public void onResponse(Call<TvShowDetail> call, Response<TvShowDetail> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TvShowDetail detail = response.body();
                            seasonList = detail.getSeasons();
                            setupSeasonSpinner();
                        }
                    }

                    @Override
                    public void onFailure(Call<TvShowDetail> call, Throwable t) {
                        Toast.makeText(DetailActivity.this, "Failed to load seasons", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSeasonSpinner() {
        List<String> seasonNames = new ArrayList<>();
        for (Season season : seasonList) {
            seasonNames.add(season.getName() + " (" + season.getEpisodeCount() + " Episodes)");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, seasonNames);
        spinnerSeasons.setAdapter(adapter);

        spinnerSeasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Season selectedSeason = seasonList.get(position);
                selectedSeasonNumber = selectedSeason.getSeasonNumber();
                loadEpisodes(tvShowId, selectedSeasonNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadEpisodes(int tvId, int seasonNumber) {
        ApiClient.getApiService().getSeasonDetails(tvId, seasonNumber, ApiClient.API_KEY, "en-US")
                .enqueue(new Callback<SeasonDetail>() {
                    @Override
                    public void onResponse(Call<SeasonDetail> call, Response<SeasonDetail> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            SeasonDetail seasonDetail = response.body();
                            episodeAdapter.setEpisodes(seasonDetail.getEpisodes());
                        }
                    }

                    @Override
                    public void onFailure(Call<SeasonDetail> call, Throwable t) {
                        Toast.makeText(DetailActivity.this, "Failed to load episodes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
