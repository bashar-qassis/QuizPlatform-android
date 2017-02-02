
package edu.birzeit.bashar.quizplatform;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Quizzes implements Serializable
{
    @SerializedName("quizzes")
    private List<Quiz> quizzes = null;
    private final static long serialVersionUID = -6237116867351615603L;

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

}
