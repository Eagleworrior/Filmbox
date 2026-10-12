package com.filmbox.network;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OmdbApiService {

    class OmdbMovie {
        @SerializedName("Title")
        public String title;

        @SerializedName("Year")
        public String year;

        @SerializedName("Plot")
        public String plot;

        @SerializedName("Poster")
        public String poster;

        @SerializedName("imdbRating")
        public String imdbRating;

        @SerializedName("imdbID")
        public String imdbID;

        @SerializedName("Response")
        public String response;
    }

    @GET("/")
    Call<OmdbMovie> getMovieByTitle(
            @Query("t") String title,
            @Query("apikey") String apiKey
    );

    @GET("/")
    Call<OmdbMovie> getMovieByImdbId(
            @Query("i") String imdbId,
            @Query("apikey") String apiKey
    );
}
