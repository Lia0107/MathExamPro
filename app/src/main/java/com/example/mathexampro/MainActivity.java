package com.example.mathexampro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private List<Question> questionList;
    int currentIndex = 0;
    private int[] userAnswer = new int[7];
    private boolean[] submitted = new boolean[7]; // Track if question is submitted
    private int skipsLeft = 2;
    private int correctCount = 0;
    private boolean examFinished = false;
    private static final String TAG = "MathExam Pro";
    private TextView question_text, skipCounter, timerText;
    private RadioGroup optionsGroup;
    private Button submitBtn, nextBtn, prevBtn, skipBtn, resetBtn, resultBtn, finishBtn;
    private Switch timerSwitch;
    private boolean timerEnabled = false;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionList = QuestionBank.getRandomQuestions();
        Arrays.fill(userAnswer, -1);
        Arrays.fill(submitted, false);

        setupViews();
        updateQuestion();
    }

    private void setupViews() {
        question_text = findViewById(R.id.question_text);
        skipCounter = findViewById(R.id.skipCounter);
        optionsGroup = findViewById(R.id.optionsGroup);
        prevBtn = findViewById(R.id.prevBtn);
        resetBtn = findViewById(R.id.resetBtn);
        submitBtn = findViewById(R.id.submitBtn);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);
        resultBtn = findViewById(R.id.resultBtn);
        finishBtn = findViewById(R.id.finishBtn);
        timerText = findViewById(R.id.timerText);
        timerSwitch = findViewById(R.id.timerSwitch);

        timerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timerEnabled = isChecked;
            if (isChecked) {
                startTimer();
            } else if (timer != null) {
                timer.cancel();
                timerText.setText("");
            }
        });

        // Set up radio group listener to save temporary answers
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && !submitted[currentIndex]) {
                userAnswer[currentIndex] = checkedId;
                updateNavigationButtons();
            }
        });

        submitBtn.setOnClickListener(v -> submitAnswer());
        nextBtn.setOnClickListener(v -> moveToNextQuestion(1));
        prevBtn.setOnClickListener(view -> moveToNextQuestion(-1));
        skipBtn.setOnClickListener(v -> skipQuestion());
        resetBtn.setOnClickListener(v -> resetExam());
        resultBtn.setOnClickListener(view -> showResults());
        finishBtn.setOnClickListener(v -> submitExam());
    }

    private void updateQuestion() {
        Question q = questionList.get(currentIndex);
        question_text.setText("Q" + (currentIndex + 1) + ":" + q.getQuestion());
        optionsGroup.removeAllViews();
        
        // Create radio buttons for options
        for (int i = 0; i < q.getOptions().length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(q.getOptions()[i]);
            rb.setId(i);
            rb.setEnabled(!submitted[currentIndex]); // Disable if already submitted
            optionsGroup.addView(rb);
        }

        // Restore previous answer if exists
        if (userAnswer[currentIndex] != -1) {
            optionsGroup.check(userAnswer[currentIndex]);
        } else {
            optionsGroup.clearCheck();
        }

        updateNavigationButtons();
        skipCounter.setText("Skips left: " + skipsLeft);
    }

    private void updateNavigationButtons() {
        // Show/hide navigation buttons
        prevBtn.setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
        nextBtn.setVisibility(currentIndex == 6 ? View.GONE : View.VISIBLE);
        
        // Show submit button if not submitted and not on last question
        submitBtn.setVisibility(!submitted[currentIndex] && currentIndex != 6 ? View.VISIBLE : View.GONE);
        
        // Show finish button on last question if current question has an answer
        if (currentIndex == 6) {
            boolean lastQuestionAnswered = userAnswer[currentIndex] != -1 || submitted[currentIndex];
            finishBtn.setVisibility(lastQuestionAnswered ? View.VISIBLE : View.GONE);
        } else {
            finishBtn.setVisibility(View.GONE);
        }
        
        // Show skip button if question is not submitted and skips are available
        boolean canSkip = !submitted[currentIndex] && skipsLeft > 0;
        skipBtn.setVisibility(canSkip ? View.VISIBLE : View.GONE);
        skipBtn.setEnabled(canSkip);

        // Show/hide result and reset buttons
        resultBtn.setVisibility(examFinished ? View.VISIBLE : View.GONE);
        resetBtn.setVisibility(examFinished ? View.VISIBLE : View.GONE);
    }

    private void submitAnswer() {
        int selected = optionsGroup.getCheckedRadioButtonId();
        if (selected == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mark question as submitted
        submitted[currentIndex] = true;
        userAnswer[currentIndex] = selected;

        // Disable radio buttons
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setEnabled(false);
        }

        updateNavigationButtons();
    }

    private void moveToNextQuestion(int direction) {
        currentIndex += direction;
        if (currentIndex < 0) currentIndex = 0;
        if (currentIndex > 6) currentIndex = 6;
        updateQuestion();
    }

    private void skipQuestion() {
        if (skipsLeft > 0 && !submitted[currentIndex]) {
            skipsLeft--;
            submitted[currentIndex] = true;
            userAnswer[currentIndex] = -1;
            updateNavigationButtons();
        }
    }

    private void submitExam() {
        // Verify last question is answered
        if (currentIndex == 6 && !submitted[currentIndex] && userAnswer[currentIndex] != -1) {
            // Auto-submit last question if it has an answer
            submitted[currentIndex] = true;
            // Disable radio buttons
            for (int i = 0; i < optionsGroup.getChildCount(); i++) {
                optionsGroup.getChildAt(i).setEnabled(false);
            }
        }

        // Calculate score
        correctCount = 0;
        for (int i = 0; i < questionList.size(); i++) {
            if (userAnswer[i] == questionList.get(i).getCorrectAnswer()) {
                correctCount++;
            }
        }

        examFinished = true;
        
        // Show success message and enable result summary button
        Toast.makeText(this, "Exam successfully submitted!", Toast.LENGTH_SHORT).show();
        
        // Hide finish button and show result button
        finishBtn.setVisibility(View.GONE);
        resultBtn.setVisibility(View.VISIBLE);
        resetBtn.setVisibility(View.VISIBLE);
        
        // Disable all navigation
        nextBtn.setVisibility(View.GONE);
        prevBtn.setVisibility(View.GONE);
        skipBtn.setVisibility(View.GONE);
        submitBtn.setVisibility(View.GONE);
        
        // Stop timer if running
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void resetExam() {
        questionList = QuestionBank.getRandomQuestions();
        currentIndex = 0;
        Arrays.fill(userAnswer, -1);
        Arrays.fill(submitted, false);
        skipsLeft = 2;
        correctCount = 0;
        examFinished = false;

        if (timerEnabled) {
            if (timer != null) {
                timer.cancel();
            }
            startTimer();
        } else {
            timerText.setText("");
        }

        updateQuestion();
        Toast.makeText(this, "Exam has been reset", Toast.LENGTH_SHORT).show();
    }

    private void showResults() {
        // Log final answers for debugging
        StringBuilder debug = new StringBuilder("Final Answer Summary:\n");
        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            boolean isCorrect = submitted[i] && userAnswer[i] == q.getCorrectAnswer();
            debug.append(String.format(
                "Q%d: User=%d, Correct=%d, Submitted=%b, IsCorrect=%b\n",
                i + 1,
                userAnswer[i],
                q.getCorrectAnswer(),
                submitted[i],
                isCorrect
            ));
        }
        Log.d(TAG, debug.toString());
        Log.d(TAG, "Final correct count: " + correctCount);

        Intent intent = new Intent(MainActivity.this, ResultSummary.class);
        intent.putExtra("answers", userAnswer);
        intent.putExtra("correct", correctCount);
        intent.putExtra("skipped", 2 - skipsLeft);
        intent.putExtra("total", 7);
        startActivity(intent);
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(600000, 1000) {
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("Time left: %d:%02d", minutes, seconds));
            }
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Time's up! Auto-submitting...", Toast.LENGTH_SHORT).show();
                examFinished = true;
                for (int i = 0; i < submitted.length; i++) {
                    if (!submitted[i]) {
                        submitted[i] = true;
                        userAnswer[i] = -1;
                    }
                }
                showResults();
            }
        }.start();
    }
}

