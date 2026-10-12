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
import com.filmbox.model.TvShow;

import java.util.ArrayList;
import java.util.List;

import coil.Coil;
import coil.request.ImageRequest;

public class MediaCardAdapter extends RecyclerView.Adapter<MediaCardAdapter.ViewHolder> {
    private final List<Object> items = new ArrayList<>();
    private final OnCardClickListener listener;

    public interface OnCardClickListener {
        void onMovieClick(Movie movie);
        void onTvShowClick(TvShow tvShow);
    }

    public MediaCardAdapter(OnCardClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<?> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = items.get(position);
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            holder.tvTitle.setText(movie.getTitle());
            holder.tvRating.setText("⭐ " + movie.getVoteAverage());

            try {
                ImageRequest request = new ImageRequest.Builder(holder.itemView.getContext())
                        .data(movie.getPosterPath())
                        .target(holder.ivPoster)
                        .crossfade(true)
                        .build();
                Coil.imageLoader(holder.itemView.getContext()).enqueue(request);
            } catch (Exception ignored) {}

            holder.itemView.setOnClickListener(v -> listener.onMovieClick(movie));
        } else if (item instanceof TvShow) {
            TvShow tv = (TvShow) item;
            holder.tvTitle.setText(tv.getName());
            holder.tvRating.setText("⭐ " + tv.getVoteAverage());

            try {
                ImageRequest request = new ImageRequest.Builder(holder.itemView.getContext())
                        .data(tv.getPosterPath())
                        .target(holder.ivPoster)
                        .crossfade(true)
                        .build();
                Coil.imageLoader(holder.itemView.getContext()).enqueue(request);
            } catch (Exception ignored) {}

            holder.itemView.setOnClickListener(v -> listener.onTvShowClick(tv));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
