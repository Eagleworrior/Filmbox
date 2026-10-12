package com.filmbox.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private double voteAverage;

    private String videoUrl;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    
    public String getPosterPath() {
        if (posterPath != null && posterPath.startsWith("http")) {
            return posterPath;
        }
        if (posterPath != null && posterPath.length() > 5) {
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
        return "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500&q=80";
    }

    public String getBackdropPath() {
        if (backdropPath != null && backdropPath.startsWith("http")) {
            return backdropPath;
        }
        if (backdropPath != null && backdropPath.length() > 5) {
            return "https://image.tmdb.org/t/p/w780" + backdropPath;
        }
        return "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&q=80";
    }

    public String getReleaseDate() { return releaseDate; }
    public double getVoteAverage() { return voteAverage; }
    
    public String getVideoUrl() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl : "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    }
    
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
