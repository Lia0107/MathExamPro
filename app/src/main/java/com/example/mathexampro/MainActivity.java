package com.example.mathexampro;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private List<Question>questionList;
    private static final String TAG = "MathExam Pro";
    int currentIndex=0;
    private int[] userAnswer;
    private boolean[] submitted;
    private TextView question_text, skipCounter;
    private RadioGroup optionsGroup;
    private Button submitBtn, nextBtn,prevBtn, skipBtn,resetBtn,resultBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionList=QuestionBank.getRandomQuestions();
        userAnswer = new int[7];
        submitted = new boolean[7];
        Arrays.fill(userAnswer,-1);
        updateQuestion();
    }

    private void setupViews(){
        question_text = findViewById(R.id.question_text);
        skipCounter = findViewById(R.id.skipCounter);
        optionsGroup = findViewById(R.id.optionsGroup);
        prevBtn = findViewById(R.id.prevBtn);
        resetBtn = findViewById(R.id.resetBtn);
        submitBtn = findViewById(R.id.submitBtn);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);
        resultBtn = findViewById(R.id.resultBtn);



    }
    private void updateQuestion(){
        Question q = questionList.get(currentIndex);
        question_text.setText("Q"+(currentIndex+1)+":"+q.getQuestion());
        optionsGroup.removeAllViews();
    }
}
