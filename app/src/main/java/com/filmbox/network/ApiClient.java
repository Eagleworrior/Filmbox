package com.filmbox.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String OMDB_BASE_URL = "http://www.omdbapi.com/";

    public static final String API_KEY = "ea220cf0b8ca48bb8aa393a5228b688b";
    public static final String READ_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlYTIyMGNmMGI4Y2E0OGJiOGFhMzkzYTUyMjhiNjg4YiIsIm5iZiI6MTc5MjY1NzgwNC41NDU5OTk4LCJzdWIiOiI2YWQ5Yzk4YzE2NGI4NTAxNGExNDJiMDciLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.iGu3sg_JZvJHnhXGtowf02RveYEyeeX9krB77GR0-t8";

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
