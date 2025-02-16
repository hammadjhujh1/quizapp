package com.example.quiz_karlo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import com.example.quiz_karlo.models.QuestionModel;

public class QuizStart extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView totalQuestionsTextView;
    private MaterialButton answerButton1, answerButton2, answerButton3, answerButton4;
    private MaterialButton nextButton, submitButton, prevButton;
    private String userName;

    private final int totalQuestions = QuizQuestions.questions.length;
    private int score = 0;
    private int currentQuestionIndex = 0;
    private String selectedAnswer = "";
    private ArrayList<QuestionModel> questionsList;
    private boolean[] answeredQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        // Get username from intent
        userName = getIntent().getStringExtra("USER_NAME");

        // Initialize question tracking
        answeredQuestions = new boolean[totalQuestions];
        initializeQuestionsList();

        // Initialize views and setup UI
        initializeViews();
        setupClickListeners();
        loadNewQuestion();
    }

    private void initializeQuestionsList() {
        questionsList = new ArrayList<>();
        for (int i = 0; i < totalQuestions; i++) {
            ArrayList<String> options = new ArrayList<>();
            for (String option : QuizQuestions.choices[i]) {
                options.add(option);
            }

            questionsList.add(new QuestionModel(
                    QuizQuestions.questions[i],
                    options,
                    findCorrectAnswerIndex(QuizQuestions.correctAnswers[i], QuizQuestions.choices[i]),
                    -1,  // No answer selected initially
                    "Explanation will be shown in review" // You can add actual explanations here
            ));
        }
    }

    private int findCorrectAnswerIndex(String correctAnswer, String[] choices) {
        for (int i = 0; i < choices.length; i++) {
            if (choices[i].equals(correctAnswer)) {
                return i;
            }
        }
        return 0;
    }

    private void initializeViews() {
        questionTextView = findViewById(R.id.question);
        totalQuestionsTextView = findViewById(R.id.totalQuestions);

        answerButton1 = findViewById(R.id.answerA);
        answerButton2 = findViewById(R.id.answerB);
        answerButton3 = findViewById(R.id.answerC);
        answerButton4 = findViewById(R.id.answerD);

        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupClickListeners() {
        answerButton1.setOnClickListener(this);
        answerButton2.setOnClickListener(this);
        answerButton3.setOnClickListener(this);
        answerButton4.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextButton) {
            if (currentQuestionIndex < totalQuestions - 1) {
                currentQuestionIndex++;
                loadNewQuestion();
            }
        } else if (v.getId() == R.id.prevButton) {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                loadNewQuestion();
            }
        } else if (v.getId() == R.id.submitButton) {
            if (!selectedAnswer.isEmpty()) {
                confirmSubmission();
            } else {
                showUnansweredQuestionsDialog();
            }
        } else {
            handleAnswerSelection((MaterialButton) v);
        }
        updateButtonVisibility();
    }

    private void loadNewQuestion() {
        QuestionModel currentQuestion = questionsList.get(currentQuestionIndex);

        totalQuestionsTextView.setText(String.format("%d/%d", currentQuestionIndex + 1, totalQuestions));
        questionTextView.setText(currentQuestion.getQuestion());

        MaterialButton[] buttons = {answerButton1, answerButton2, answerButton3, answerButton4};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText(currentQuestion.getOptions().get(i));
        }

        resetButtonStates();
        if (currentQuestion.getSelectedAnswerIndex() != -1) {
            selectedAnswer = currentQuestion.getOptions().get(currentQuestion.getSelectedAnswerIndex());
            buttons[currentQuestion.getSelectedAnswerIndex()].setSelected(true);
            buttons[currentQuestion.getSelectedAnswerIndex()].setBackgroundColor(getColor(R.color.selected_option_bg));
            buttons[currentQuestion.getSelectedAnswerIndex()].setTextColor(getColor(R.color.selected_option_text));
        } else {
            selectedAnswer = "";
        }
    }

    private void handleAnswerSelection(MaterialButton clickedButton) {
        resetButtonStates();
        selectedAnswer = clickedButton.getText().toString();

        int selectedIndex = -1;
        MaterialButton[] buttons = {answerButton1, answerButton2, answerButton3, answerButton4};
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == clickedButton) {
                selectedIndex = i;
                break;
            }
        }

        questionsList.get(currentQuestionIndex).setSelectedAnswerIndex(selectedIndex);
        answeredQuestions[currentQuestionIndex] = true;

        clickedButton.setSelected(true);
        clickedButton.setBackgroundColor(getColor(R.color.selected_option_bg));
        clickedButton.setTextColor(getColor(R.color.selected_option_text));
    }

    private void resetButtonStates() {
        MaterialButton[] buttons = {answerButton1, answerButton2, answerButton3, answerButton4};
        for (MaterialButton button : buttons) {
            button.setSelected(false);
            button.setBackgroundColor(Color.WHITE);
            button.setTextColor(Color.BLACK);
        }
    }

    private void updateButtonVisibility() {
        prevButton.setVisibility(currentQuestionIndex > 0 ? View.VISIBLE : View.INVISIBLE);

        if (currentQuestionIndex == totalQuestions - 1) {
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private void confirmSubmission() {
        boolean allAnswered = true;
        for (boolean answered : answeredQuestions) {
            if (!answered) {
                allAnswered = false;
                break;
            }
        }

        if (!allAnswered) {
            showUnansweredQuestionsDialog();
            return;
        }

        score = 0;
        for (QuestionModel question : questionsList) {
            if (question.isCorrect()) {
                score++;
            }
        }

        Intent intent = new Intent(QuizStart.this, QuizReviewActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", totalQuestions);
        intent.putExtra("userName", userName);
        intent.putParcelableArrayListExtra("questions", questionsList);
        startActivity(intent);
        finish();
    }

    private void showUnansweredQuestionsDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Unanswered Questions")
                .setMessage("You haven't answered all questions. Would you like to review them?")
                .setPositiveButton("Review", (dialog, which) -> {
                    // Find first unanswered question
                    for (int i = 0; i < answeredQuestions.length; i++) {
                        if (!answeredQuestions[i]) {
                            currentQuestionIndex = i;
                            loadNewQuestion();
                            updateButtonVisibility();
                            break;
                        }
                    }
                })
                .setNegativeButton("Submit Anyway", (dialog, which) -> {
                    confirmSubmission();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to exit? Your progress will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("Continue", null)
                .show();
    }
}