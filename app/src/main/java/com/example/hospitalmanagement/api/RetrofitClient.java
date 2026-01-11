package com.example.hospitalmanagement.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // NOTE: User must change this URL to their Hugging Face URL
    private static final String BASE_URL = "https://junaidjd-neurotech.hf.space/";
    // For local emulator: "http://10.0.2.2:8000/"
    // private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(90, TimeUnit.SECONDS) // Increased for cold starts
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder builder = original.newBuilder()
                                .header("Authorization",
                                        "Bearer " + com.example.hospitalmanagement.utils.Secrets.HF_ACCESS_TOKEN)
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json");
                        android.util.Log.d("RetrofitClient",
                                "Using Token: " + com.example.hospitalmanagement.utils.Secrets.HF_ACCESS_TOKEN);
                        okhttp3.Request request = builder.build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
