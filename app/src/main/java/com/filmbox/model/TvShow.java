package com.filmbox.model;

import com.google.gson.annotations.SerializedName;

public class TvShow {
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

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("vote_average")
    private double voteAverage;

    private String videoUrl;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getOverview() { return overview; }

    public String getPosterPath() {
        if (posterPath != null && posterPath.startsWith("http")) {
            return posterPath;
        }
        if (posterPath != null && posterPath.length() > 5) {
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
        return "https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?w=500&q=80";
    }

    public String getBackdropPath() {
        if (backdropPath != null && backdropPath.startsWith("http")) {
            return backdropPath;
        }
        if (backdropPath != null && backdropPath.length() > 5) {
            return "https://image.tmdb.org/t/p/w780" + backdropPath;
        }
        return "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=800&q=80";
    }

    public String getFirstAirDate() { return firstAirDate; }
    public double getVoteAverage() { return voteAverage; }

    public String getVideoUrl() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl : "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4";
    }

    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
