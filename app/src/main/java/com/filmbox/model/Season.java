package com.filmbox.model;

import com.google.gson.annotations.SerializedName;

public class Season {
    @SerializedName("id")
    private int id;

    @SerializedName("season_number")
    private int seasonNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("episode_count")
    private int episodeCount;

    public int getId() { return id; }
    public int getSeasonNumber() { return seasonNumber; }
    public String getName() { return name; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : ""; }
    public int getEpisodeCount() { return episodeCount; }
}
