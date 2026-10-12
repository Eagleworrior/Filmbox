package com.filmbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.filmbox.R;
import com.filmbox.model.Movie;

import java.util.ArrayList;
import java.util.List;

import coil.Coil;
import coil.request.ImageRequest;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private final List<Movie> movies = new ArrayList<>();
    private final OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(Movie movie);
    }

    public BannerAdapter(OnBannerClickListener listener) {
        this.listener = listener;
    }

    public void setMovies(List<Movie> newMovies) {
        this.movies.clear();
        if (newMovies != null) {
            this.movies.addAll(newMovies);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.tvBannerTitle.setText(movie.getTitle());
        holder.tvBannerRating.setText("🔥 Trending | ⭐ " + movie.getVoteAverage());

        try {
            ImageRequest request = new ImageRequest.Builder(holder.itemView.getContext())
                    .data(movie.getBackdropPath())
                    .target(holder.ivBanner)
                    .crossfade(true)
                    .build();
            Coil.imageLoader(holder.itemView.getContext()).enqueue(request);
        } catch (Exception ignored) {}

        holder.itemView.setOnClickListener(v -> listener.onBannerClick(movie));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvBannerTitle, tvBannerRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            tvBannerTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvBannerRating = itemView.findViewById(R.id.tvBannerRating);
        }
    }
}
