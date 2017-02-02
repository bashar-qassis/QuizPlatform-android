package edu.birzeit.bashar.quizplatform.Clients;

/**
 * Created by Bashar on 24-Jan-17.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static edu.birzeit.bashar.quizplatform.CONSTANTS.BASE_URL;


public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
