package com.filmbox.network;

import com.filmbox.model.MovieResponse;
import com.filmbox.model.SeasonDetail;
import com.filmbox.model.TvShowDetail;
import com.filmbox.model.TvShowResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApiService {

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/popular")
    Call<TvShowResponse> getPopularTvShows(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{tv_id}")
    Call<TvShowDetail> getTvShowDetails(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{tv_id}/season/{season_number}")
    Call<SeasonDetail> getSeasonDetails(
            @Path("tv_id") int tvId,
            @Path("season_number") int seasonNumber,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("search/multi")
    Call<MovieResponse> searchMulti(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("api_key") String apiKey,
            @Query("with_genres") int genreId
    );

    @GET("discover/tv")
    Call<TvShowResponse> getTvShowsByGenre(
            @Query("api_key") String apiKey,
            @Query("with_genres") int genreId
    );
}
