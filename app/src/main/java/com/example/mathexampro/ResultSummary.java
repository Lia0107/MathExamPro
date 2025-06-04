package com.example.mathexampro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;

public class ResultSummary extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        int[] answers = getIntent().getIntArrayExtra("answers");
        int skipped = getIntent().getIntExtra("skipped", 0);
        int correct = getIntent().getIntExtra("correct", 0);
        int total = getIntent().getIntExtra("total", 7);
        int answered = total - skipped;

        // Calculate percentage based on total questions
        double percentage = ((double) correct / total) * 100;
        int roundedPercentage = (int) Math.round(percentage);

        // Log the values for debugging
        Log.d("MathExam", String.format(
            "Results - Correct: %d, Total: %d, Raw Percentage: %.2f, Rounded: %d",
            correct, total, percentage, roundedPercentage
        ));

        TextView resultText = findViewById(R.id.resultSum);
        if (resultText != null) {
            String resultString = String.format(
                "Exam Results\n\n" +
                "Total Questions: %d\n" +
                "Questions Answered: %d\n" +
                "Questions Skipped: %d\n" +
                "Correct Answers: %d\n" +
                "Final Score: %d%%\n\n" +
                "Performance Summary:\n" +
                "%s",
                total, answered, skipped, correct,
                roundedPercentage,
                getPerformanceMessage(correct, total)
            );
            resultText.setText(resultString);
        }

        Button resetButton = findViewById(R.id.resetExamBtn);
        resetButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        Button returnButton = findViewById(R.id.returnToMain);
        returnButton.setOnClickListener(v -> finish());
    }

    private String getPerformanceMessage(int correct, int total) {
        // Calculate percentage with double precision
        double percentage = ((double) correct / total) * 100;
        
        if (percentage >= 90) {
            return "Outstanding! You've mastered these concepts!";
        } else if (percentage >= 80) {
            return "Great job! You're doing very well!";
        } else if (percentage >= 70) {
            return "Good work! Keep practicing to improve!";
        } else if (percentage >= 60) {
            return "You're on the right track. More practice will help!";
        } else {
            return "Keep studying! You'll get better with practice!";
        }
    }
}
