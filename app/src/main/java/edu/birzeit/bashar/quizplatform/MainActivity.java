package edu.birzeit.bashar.quizplatform;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.birzeit.bashar.quizplatform.Services.ServerAPIs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SendFeedbackJob sendFeedbackJob = new SendFeedbackJob(this);
        sendFeedbackJob.execute();
    }

    public void submit() {

    }
}

class SendFeedbackJob extends AsyncTask<String, Void, List<Quiz>> {

    private Context context;
    private HashMap quizForms;
    private int viewIdCount;
    private HashMap form;

    public SendFeedbackJob(Context context) {
        this.context = context;
        this.quizForms = new HashMap();
        this.viewIdCount = 0;
    }

    @Override
    protected List<Quiz> doInBackground(String[] params) {
        // do above Server call here
        ServerAPIs serverAPI = new ServerAPIs("1");
//        serverAPI.login("basharqassis9@hotmail.com", "secret");
//        sleep(4000);
        List<Quiz> quizzes = serverAPI.testServer();
        return quizzes;
    }

    @Override
    protected void onPostExecute(List<Quiz> quizzes) {
        //process message
        final MainActivity activity = (MainActivity) context;
        final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activity_main);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (Quiz quiz : quizzes) {
            form = new HashMap();
            Log.v("Quiz", quiz.toString());
            //add quiz name
            TextView quizTitleLabel = new TextView(activity);
            quizTitleLabel.setTag(quiz.getId());
            quizTitleLabel.setId(viewIdCount++);
            quizTitleLabel.setText(quiz.getTitle());
            layout.addView(quizTitleLabel);

            form.put(quizTitleLabel.getId(), quizTitleLabel);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long end = 0;
            try {
                end = formatter.parse(quiz.getEnd_time()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long now = System.currentTimeMillis();

            formatter.setLenient(false);
            final TextView timer = new TextView(context);
            layout.addView(timer);

            new CountDownTimer(end-now, 1000) {
                public void onTick(long millisUntilFinished) {
                    long durationSeconds = millisUntilFinished/1000;
                    timer.setText("Time remaining: " + String.format("%02d:%02d:%02d", durationSeconds / 3600,
                            (durationSeconds % 3600) / 60, (durationSeconds % 60)));
                }

                public void onFinish() {
                    timer.setText("done!");

                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    dialog.dismiss();
                                }
                            });

                            builder.setMessage("Time is up!! click Submit to submit your answers.")
                                    .setTitle("Timeout");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }.start();

            List<Question> questions = quiz.getQuestions();
            for (Question question : questions) {
                TextView questionText = new TextView(activity);
                questionText.setTag(question.getId());
                questionText.setId(viewIdCount++);
                questionText.setText(question.getQuestion());
                layout.addView(questionText);
                form.put(questionText.getId(), questionText);

                List<Answer> answers = question.getAnswers();
                RadioGroup radioGroup = new RadioGroup(activity);
                radioGroup.setId(viewIdCount++);
                for (Answer answer : answers) {
                    RadioButton radioButton = new RadioButton(activity);
                    radioButton.setText(answer.getAnswer());
                    radioButton.setId(viewIdCount++);
                    radioButton.setTag(answer.getId());
                    radioGroup.addView(radioButton);
                }
                layout.addView(radioGroup);
                form.put(radioGroup.getId(), radioGroup);
            }

            final Button submitButton = new Button(activity);
            submitButton.setTag(quiz.getId());
            submitButton.setId(viewIdCount++);
            submitButton.setText("Submit " + quiz.getTitle());
            submitButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            submitButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    JSONArray submittion = new JSONArray();

                    int quizid = (int) v.getTag();
                    HashMap form = (HashMap) quizForms.get(quizid);
                    form = (HashMap) form.clone();
                    Log.v("form value: ", form.toString());
                    boolean flag = true;
                    for (Iterator i = form.keySet().iterator(); i.hasNext(); ) {
                        if (flag) {
                            i.next();
                            i.remove();
                            flag = false;
                        } else if (form.get(i.next()).getClass() == Button.class) {
                            i.remove();
                            break;
                        }
                        View item = (View) form.get(i.next());
                        i.remove();
                        int questionId = (int) item.getTag();
                        Log.v("question ID: ", "" + item.toString());
                        RadioGroup answers = (RadioGroup) form.get(i.next());
                        int answerId = (int) answers.findViewById(answers.getCheckedRadioButtonId()).getTag();

                        JSONObject solution = new JSONObject();
                        try {
                            solution.put("question_id", questionId);
                            solution.put("answer_id", answerId);
                            submittion.put(solution);
                            Log.v("submittion: ", submittion.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONObject finalSubmittion = new JSONObject();
                    try {
                        finalSubmittion.put("Questions", submittion);
                        SubmitSolution submitSolution = new SubmitSolution(activity, String.valueOf(quizid));
                        submitSolution.execute(finalSubmittion);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            layout.addView(submitButton);
            form.put(submitButton.getId(), submitButton);

            quizForms.put(quiz.getId(), form);
            Space space = new Space(activity);
            space.setMinimumHeight(20);
            layout.addView(space);
        }
    }
}

class SubmitSolution extends AsyncTask<JSONObject, Void, String> {

    private final String quizId;
    private Context context;

    public SubmitSolution(Context context, String quizId) {
        this.context = context;
        this.quizId = quizId;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        JSONObject submittion = params[0];
        ServerAPIs serverAPIs = new ServerAPIs("1");
        Log.v("JSON stuff: ", submittion.toString());
        final String result = serverAPIs.submit(submittion, quizId);
        ((MainActivity) context).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });

                builder.setMessage(result)
                        .setTitle("Result");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        Log.v("Grade : ", result);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}