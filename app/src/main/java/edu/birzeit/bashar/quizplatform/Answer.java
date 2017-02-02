
package edu.birzeit.bashar.quizplatform;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Answer implements Serializable
{
    @SerializedName("id")
    private Integer id;
    @SerializedName("is_right")
    private Integer is_right;
    @SerializedName("answer")
    private String answer;
    private final static long serialVersionUID = 1779536689605778665L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIs_right() {
        return is_right;
    }

    public void setIs_right(Integer is_right) {
        this.is_right = is_right;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", is_right=" + is_right +
                ", answer='" + answer + '\'' +
                '}';
    }
}
