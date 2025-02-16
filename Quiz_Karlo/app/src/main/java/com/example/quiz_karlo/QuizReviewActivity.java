package com.example.quiz_karlo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class QuizReviewActivity extends AppCompatActivity {
    private TextView scoreTextView;
    private TextView percentageTextView;
    private TextView feedbackText;
    private TextView correctCount;
    private TextView incorrectCount;
    private CircularProgressIndicator scoreProgress;
    private MaterialButton retryButton;
    private MaterialButton checkResultsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_review);

        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        String userName = getIntent().getStringExtra("userName");

        initializeViews();

        float percentage = (float) score / totalQuestions * 100;
        int incorrectAnswers = totalQuestions - score;

        updateScoreDisplay(score, totalQuestions, percentage);
        updateStatistics(score, incorrectAnswers);
        updateFeedback(percentage);
        updateCompletionMessage(userName);

        setupButtonListeners();
    }

    private void initializeViews() {
        scoreTextView = findViewById(R.id.scoreTextView);
        percentageTextView = findViewById(R.id.percentageTextView);
        feedbackText = findViewById(R.id.feedbackText);
        correctCount = findViewById(R.id.correctCount);
        incorrectCount = findViewById(R.id.incorrectCount);
        scoreProgress = findViewById(R.id.scoreProgress);
        retryButton = findViewById(R.id.retryButton);
        checkResultsButton = findViewById(R.id.check_results);
    }

    private void updateScoreDisplay(int score, int totalQuestions, float percentage) {
        scoreTextView.setText(score + "/" + totalQuestions);
        percentageTextView.setText(String.format("%.1f%%", percentage));
        scoreProgress.setProgress((int) percentage);
    }

    private void updateStatistics(int correct, int incorrect) {
        correctCount.setText(String.valueOf(correct));
        incorrectCount.setText(String.valueOf(incorrect));
    }

    private void updateFeedback(float percentage) {
        String feedback;
        if (percentage >= 90) {
            feedback = "Excellent! You've demonstrated outstanding knowledge!";
        } else if (percentage >= 70) {
            feedback = "Good job! You've shown solid understanding.";
        } else if (percentage >= 50) {
            feedback = "You're on the right track, but there's room for improvement.";
        } else {
            feedback = "Keep practicing! Review the material and try again.";
        }
        feedbackText.setText(feedback);
    }

    private void updateCompletionMessage(String userName) {
        TextView completionMessage = findViewById(R.id.resultTextView);
        String message = userName != null ?
                "Thank you " + userName + ", your quiz has been completed" :
                "Your quiz has been completed";
        completionMessage.setText(message);
    }

    private void setupButtonListeners() {
        retryButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizReviewActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        checkResultsButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizReviewActivity.this, Results.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
        });
    }
}