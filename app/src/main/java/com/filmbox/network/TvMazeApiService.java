package com.filmbox.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TvMazeApiService {

    class TvMazeShow {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("summary")
        private String summary;
        @SerializedName("image")
        private TvMazeImage image;
        @SerializedName("rating")
        private TvMazeRating rating;
        @SerializedName("premiered")
        private String premiered;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getSummary() { return summary != null ? summary.replaceAll("<[^>]*>", "") : ""; }
        public String getPoster() { return image != null ? image.original : ""; }
        public double getRating() { return rating != null ? rating.average : 8.0; }
        public String getPremiered() { return premiered; }
    }

    class TvMazeImage {
        @SerializedName("original")
        public String original;
        @SerializedName("medium")
        public String medium;
    }

    class TvMazeRating {
        @SerializedName("average")
        public double average;
    }

    class TvMazeSearchResult {
        @SerializedName("show")
        public TvMazeShow show;
    }

    @GET("shows")
    Call<List<TvMazeShow>> getPopularShows();

    @GET("search/shows")
    Call<List<TvMazeSearchResult>> searchShows(@Query("q") String query);
}
