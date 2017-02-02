package edu.birzeit.bashar.quizplatform.interfaces;

import org.json.JSONObject;

import edu.birzeit.bashar.quizplatform.Quizzes;
import edu.birzeit.bashar.quizplatform.Quiz;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Bashar on 24-Jan-17.
 */

public interface ServerInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("{userId}/quizzes")
    Call<Quizzes> getQuizzes(@Path("userId") String userId);

//    @GET("group/{id}/users")
//    Call<List<Quizzes>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @POST("{userId}/quizzes/{quizId}")
    Call<ResponseBody> submitSolution(@Path("userId") String userId, @Path("quizId")String quizId, @Body RequestBody quiz);

}
