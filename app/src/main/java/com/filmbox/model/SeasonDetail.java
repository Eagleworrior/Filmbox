package com.filmbox.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SeasonDetail {
    @SerializedName("_id")
    private String id;

    @SerializedName("season_number")
    private int seasonNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("episodes")
    private List<Episode> episodes;

    public String getId() { return id; }
    public int getSeasonNumber() { return seasonNumber; }
    public String getName() { return name; }
    public List<Episode> getEpisodes() { return episodes; }
}
