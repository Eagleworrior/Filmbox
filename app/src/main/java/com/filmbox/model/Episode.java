package com.filmbox.model;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("id")
    private int id;

    @SerializedName("episode_number")
    private int episodeNumber;

    @SerializedName("season_number")
    private int seasonNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("still_path")
    private String stillPath;

    @SerializedName("air_date")
    private String airDate;

    public int getId() { return id; }
    public int getEpisodeNumber() { return episodeNumber; }
    public int getSeasonNumber() { return seasonNumber; }
    public String getName() { return name; }
    public String getOverview() { return overview; }
    public String getStillPath() { return stillPath != null ? "https://image.tmdb.org/t/p/w300" + stillPath : ""; }
    public String getAirDate() { return airDate; }
}
