package com.filmbox.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TvShowDetail {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("seasons")
    private List<Season> seasons;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : ""; }
    public String getBackdropPath() { return backdropPath != null ? "https://image.tmdb.org/t/p/w780" + backdropPath : ""; }
    public List<Season> getSeasons() { return seasons; }
}
