package com.filmbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.filmbox.R;
import com.filmbox.model.Episode;

import java.util.ArrayList;
import java.util.List;

import coil.Coil;
import coil.request.ImageRequest;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private final List<Episode> episodes = new ArrayList<>();
    private OnEpisodeClickListener listener;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode);
    }

    public EpisodeAdapter() {}

    public EpisodeAdapter(OnEpisodeClickListener listener) {
        this.listener = listener;
    }

    public void setEpisodes(List<Episode> newEpisodes) {
        this.episodes.clear();
        if (newEpisodes != null) {
            this.episodes.addAll(newEpisodes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode ep = episodes.get(position);
        holder.tvEpisodeTitle.setText(ep.getEpisodeNumber() + ". " + ep.getName());
        holder.tvEpisodeOverview.setText(ep.getOverview());

        try {
            ImageRequest request = new ImageRequest.Builder(holder.itemView.getContext())
                    .data(ep.getStillPath())
                    .target(holder.ivEpisode)
                    .crossfade(true)
                    .build();
            Coil.imageLoader(holder.itemView.getContext()).enqueue(request);
        } catch (Exception ignored) {}

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onEpisodeClick(ep));
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEpisode;
        TextView tvEpisodeTitle, tvEpisodeOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEpisode = itemView.findViewById(R.id.ivEpisode);
            tvEpisodeTitle = itemView.findViewById(R.id.tvEpisodeTitle);
            tvEpisodeOverview = itemView.findViewById(R.id.tvEpisodeOverview);
        }
    }
}
