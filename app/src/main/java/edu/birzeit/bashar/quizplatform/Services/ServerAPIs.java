package edu.birzeit.bashar.quizplatform.Services;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import edu.birzeit.bashar.quizplatform.Clients.ApiClient;
import edu.birzeit.bashar.quizplatform.Quiz;
import edu.birzeit.bashar.quizplatform.Quizzes;
import edu.birzeit.bashar.quizplatform.interfaces.ServerInterface;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Bashar on 06-Jan-17.
 */

public class ServerAPIs {

    private Context context;
    private String id;

    public ServerAPIs(String id) {
        this.id = id;
    }

    public List<Quiz> testServer() {

        ServerInterface apiService =
                ApiClient.getClient().create(ServerInterface.class);
        Call<Quizzes> call = apiService.getQuizzes(id);
        try {
            Response<Quizzes> response = call.execute();
            Log.d("Result: ", ""+response.body().getQuizzes().toString());
            return response.body().getQuizzes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String submit(JSONObject submittion, String quizId) {
        ServerInterface apiService =
                ApiClient.getClient().create(ServerInterface.class);
//        Log.v("response: ",submittion.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), submittion.toString());
        Call<ResponseBody> call = apiService.submitSolution(this.id,quizId,requestBody);
        try {
            Response<ResponseBody> response = call.execute();
            if(response.code()!= 406){
                Log.v("response: ", String.valueOf(response.body().toString()));
                JSONObject jsonObject = new JSONObject(response.body().toString());
                Log.v("JSONObject: ", jsonObject.toString());
                JSONObject newJSON = jsonObject.getJSONObject("Result");
                return newJSON.toString();
            }
            return "You Already Submitted A Solution";
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "Error: Something Happened!";
        }
    }
}
