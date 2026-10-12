package com.filmbox.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String OMDB_BASE_URL = "http://www.omdbapi.com/";

    public static final String API_KEY = "4e44d9029b1270a757cddc766a1bcb63";
    public static final String READ_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGYzMzNmZDQyYzNiMGRlNDc5ZmUzY2IzMjFiMGNiZSIsIm5iZiI6MTc5MTcwMDUwMC43NCwic3ViIjoiNmFjYjJlMTRmNGY5M2Y1Zjc3ZGU0ODVhIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.D2mqfhkPGIZjF7d0jaqrrFtaT65srGIwtnpK5Wq5Z-8";

    public static final String OMDB_API_KEY = "fd1b18ad";

    private static Retrofit retrofit = null;
    private static OmdbApiService omdbApiService = null;

    public static TmdbApiService getApiService() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + READ_ACCESS_TOKEN)
                                .header("accept", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(TmdbApiService.class);
    }

    public static OmdbApiService getOmdbApiService() {
        if (omdbApiService == null) {
            Retrofit omdbRetrofit = new Retrofit.Builder()
                    .baseUrl(OMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            omdbApiService = omdbRetrofit.create(OmdbApiService.class);
        }
        return omdbApiService;
    }
}
