package com.filmbox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.filmbox.adapter.MediaAdapter;
import com.filmbox.model.Movie;
import com.filmbox.model.TvShow;
import com.filmbox.repository.MovieRepository;
import com.filmbox.util.DownloadManagerUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadsActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    private TextView tvCount;
    private MediaAdapter mediaAdapter;
    private DownloadManagerUtil downloadManagerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_downloads);

            downloadManagerUtil = new DownloadManagerUtil(this);

            ImageButton btnBack = findViewById(R.id.btnBack);
            tvCount = findViewById(R.id.tvCount);
            RecyclerView recyclerViewDownloads = findViewById(R.id.recyclerViewDownloads);

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            if (recyclerViewDownloads != null) {
                recyclerViewDownloads.setLayoutManager(new LinearLayoutManager(this));
                mediaAdapter = new MediaAdapter(this);
                recyclerViewDownloads.setAdapter(mediaAdapter);
            }

            loadDownloads();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading downloads", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDownloads();
    }

    private void loadDownloads() {
        try {
            List<Movie> downloads = downloadManagerUtil != null ? downloadManagerUtil.getDownloads() : new ArrayList<>();
            if (downloads.isEmpty()) {
                List<Movie> defaultMovies = MovieRepository.getDefaultMovies();
                if (defaultMovies != null && !defaultMovies.isEmpty()) {
                    downloads = defaultMovies.subList(0, Math.min(4, defaultMovies.size()));
                } else {
                    downloads = new ArrayList<>();
                }
            }
            if (tvCount != null) {
                tvCount.setText(downloads.size() + " Saved Titles");
            }
            if (mediaAdapter != null) {
                mediaAdapter.setItems(downloads);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onMovieClick(Movie movie) {
        try {
            if (movie != null) {
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("tmdb_id", movie.getId());
                intent.putExtra("media_type", "movie");
                intent.putExtra("video_url", movie.getVideoUrl());
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening player", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTvShowClick(TvShow tvShow) {
        try {
            if (tvShow != null) {
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra("title", tvShow.getName());
                intent.putExtra("tmdb_id", tvShow.getId());
                intent.putExtra("media_type", "tv");
                intent.putExtra("video_url", tvShow.getVideoUrl());
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening player", Toast.LENGTH_SHORT).show();
        }
    }
}
