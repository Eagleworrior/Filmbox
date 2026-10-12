package com.filmbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.filmbox.R;
import com.filmbox.model.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private final List<Genre> genres = new ArrayList<>();
    private int selectedPosition = 0;
    private final OnGenreClickListener listener;

    public interface OnGenreClickListener {
        void onGenreClick(Genre genre);
    }

    public GenreAdapter(OnGenreClickListener listener) {
        this.listener = listener;
    }

    public void setGenres(List<Genre> newGenres) {
        this.genres.clear();
        if (newGenres != null) {
            this.genres.addAll(newGenres);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.tvGenreName.setText(genre.getName());

        if (selectedPosition == position) {
            holder.tvGenreName.setBackgroundResource(R.drawable.bg_chip_selected);
        } else {
            holder.tvGenreName.setBackgroundResource(R.drawable.bg_chip);
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                int previousPosition = selectedPosition;
                selectedPosition = currentPos;
                if (previousPosition >= 0 && previousPosition < genres.size()) {
                    notifyItemChanged(previousPosition);
                }
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onGenreClick(genre);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGenreName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGenreName = itemView.findViewById(R.id.tvGenreName);
        }
    }
}
