package com.filmbox.network;

import com.filmbox.model.Movie;
import com.filmbox.model.MovieResponse;
import com.filmbox.model.TvShow;
import com.filmbox.model.TvShowResponse;
import com.filmbox.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MultiApiAggregator {

    private static TvMazeApiService tvMazeApiService = null;

    public interface AggregatorCallback<T> {
        void onResult(List<T> items);
    }

    public static TvMazeApiService getTvMazeService() {
        if (tvMazeApiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.tvmaze.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            tvMazeApiService = retrofit.create(TvMazeApiService.class);
        }
        return tvMazeApiService;
    }

    public static void fetchMoviesMultiSource(AggregatorCallback<Movie> callback) {
        ApiClient.getApiService().getPopularMovies(ApiClient.API_KEY, "en-US")
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                            callback.onResult(response.body().getResults());
                        } else {
                            callback.onResult(MovieRepository.getDefaultMovies());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onResult(MovieRepository.getDefaultMovies());
                    }
                });
    }

    public static void fetchTvShowsMultiSource(AggregatorCallback<TvShow> callback) {
        getTvMazeService().getPopularShows().enqueue(new Callback<List<TvMazeApiService.TvMazeShow>>() {
            @Override
            public void onResponse(Call<List<TvMazeApiService.TvMazeShow>> call, Response<List<TvMazeApiService.TvMazeShow>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<TvShow> tvList = new ArrayList<>();
                    for (TvMazeApiService.TvMazeShow show : response.body()) {
                        tvList.add(convertTvMazeToShow(show));
                    }
                    callback.onResult(tvList);
                } else {
                    fetchTvShowsFromTmdbFallback(callback);
                }
            }

            @Override
            public void onFailure(Call<List<TvMazeApiService.TvMazeShow>> call, Throwable t) {
                fetchTvShowsFromTmdbFallback(callback);
            }
        });
    }

    private static void fetchTvShowsFromTmdbFallback(AggregatorCallback<TvShow> callback) {
        ApiClient.getApiService().getPopularTvShows(ApiClient.API_KEY, "en-US")
                .enqueue(new Callback<TvShowResponse>() {
                    @Override
                    public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                            callback.onResult(response.body().getResults());
                        } else {
                            callback.onResult(MovieRepository.getDefaultTvShows());
                        }
                    }

                    @Override
                    public void onFailure(Call<TvShowResponse> call, Throwable t) {
                        callback.onResult(MovieRepository.getDefaultTvShows());
                    }
                });
    }

    private static TvShow convertTvMazeToShow(TvMazeApiService.TvMazeShow show) {
        TvShow t = new TvShow();
        try {
            java.lang.reflect.Field fId = TvShow.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(t, show.getId());

            java.lang.reflect.Field fName = TvShow.class.getDeclaredField("name");
            fName.setAccessible(true);
            fName.set(t, show.getName());

            java.lang.reflect.Field fOverview = TvShow.class.getDeclaredField("overview");
            fOverview.setAccessible(true);
            fOverview.set(t, show.getSummary());

            java.lang.reflect.Field fPoster = TvShow.class.getDeclaredField("posterPath");
            fPoster.setAccessible(true);
            fPoster.set(t, show.getPoster());

            java.lang.reflect.Field fBackdrop = TvShow.class.getDeclaredField("backdropPath");
            fBackdrop.setAccessible(true);
            fBackdrop.set(t, show.getPoster());

            java.lang.reflect.Field fDate = TvShow.class.getDeclaredField("firstAirDate");
            fDate.setAccessible(true);
            fDate.set(t, show.getPremiered());

            java.lang.reflect.Field fRating = TvShow.class.getDeclaredField("voteAverage");
            fRating.setAccessible(true);
            fRating.set(t, show.getRating());
        } catch (Exception ignored) {}
        return t;
    }
}
