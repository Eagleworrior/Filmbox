package com.filmbox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.filmbox.adapter.MediaAdapter;
import com.filmbox.model.Movie;
import com.filmbox.model.TvShow;
import com.filmbox.repository.MovieRepository;
import com.filmbox.util.DownloadManagerUtil;

import java.util.List;

public class DownloadsActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    private TextView tvCount;
    private MediaAdapter mediaAdapter;
    private DownloadManagerUtil downloadManagerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        downloadManagerUtil = new DownloadManagerUtil(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        tvCount = findViewById(R.id.tvCount);
        RecyclerView recyclerViewDownloads = findViewById(R.id.recyclerViewDownloads);

        btnBack.setOnClickListener(v -> finish());

        recyclerViewDownloads.setLayoutManager(new LinearLayoutManager(this));
        mediaAdapter = new MediaAdapter(this);
        recyclerViewDownloads.setAdapter(mediaAdapter);

        loadDownloads();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDownloads();
    }

    private void loadDownloads() {
        List<Movie> downloads = downloadManagerUtil.getDownloads();
        if (downloads.isEmpty()) {
            downloads = MovieRepository.getDefaultMovies().subList(0, 4);
        }
        tvCount.setText(downloads.size() + " Saved Titles");
        mediaAdapter.setItems(downloads);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("title", movie.getTitle());
        startActivity(intent);
    }

    @Override
    public void onTvShowClick(TvShow tvShow) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("title", tvShow.getName());
        startActivity(intent);
    }
}
