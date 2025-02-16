package com.example.quiz_karlo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz_karlo.models.QuestionModel;
import java.util.ArrayList;

public class Results extends AppCompatActivity {
    private RecyclerView resultsRecyclerView;
    private ArrayList<QuestionModel> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        questions = getIntent().getParcelableArrayListExtra("questions");

        if (questions != null && !questions.isEmpty()) {
            for (QuestionModel q : questions) {
                System.out.println("Question: " + q.toString());
            }

            ResultsAdapter adapter = new ResultsAdapter(this, questions);
            resultsRecyclerView.setAdapter(adapter);
        } else {
            TextView errorView = new TextView(this);
            errorView.setText("No questions data available");
            errorView.setPadding(16, 16, 16, 16);
            ((ViewGroup) resultsRecyclerView.getParent()).addView(errorView);
        }
    }
}

class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {
    private final Context context;
    private final ArrayList<QuestionModel> questions;

    public ResultsAdapter(Context context, ArrayList<QuestionModel> questions) {
        this.context = context;
        this.questions = questions;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        QuestionModel question = questions.get(position);

        holder.questionNumberText.setText(String.format("Question %d", position + 1));
        holder.questionText.setText(question.getQuestion());

        holder.answersContainer.removeAllViews();

        ArrayList<String> options = new ArrayList<>(question.getOptions());
        for (int i = 0; i < options.size(); i++) {
            View answerView = LayoutInflater.from(context)
                    .inflate(R.layout.item_answer, holder.answersContainer, false);

            TextView answerText = answerView.findViewById(R.id.answerText);
            answerText.setText(options.get(i));

            if (i == question.getCorrectAnswerIndex()) {
                answerView.setBackgroundResource(R.drawable.correct_answer_background);
                answerText.setTextColor(ContextCompat.getColor(context, R.color.correct_answer_text));
            } else if (i == question.getSelectedAnswerIndex() && !question.isCorrect()) {
                answerView.setBackgroundResource(R.drawable.incorrect_answer_background);
                answerText.setTextColor(ContextCompat.getColor(context, R.color.incorrect_answer_text));
            }

            holder.answersContainer.addView(answerView);
        }

        holder.resultIcon.setImageResource(question.isCorrect() ?
                R.drawable.ic_check_circle : R.drawable.ic_error_circle);

        String explanationPrefix = question.isCorrect() ? "Correct! " : "Incorrect. ";
        holder.explanationText.setText(explanationPrefix + question.getExplanation());
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumberText;
        TextView questionText;
        LinearLayout answersContainer;
        ImageView resultIcon;
        TextView explanationText;

        ResultViewHolder(View itemView) {
            super(itemView);
            questionNumberText = itemView.findViewById(R.id.questionNumberText);
            questionText = itemView.findViewById(R.id.questionText);
            answersContainer = itemView.findViewById(R.id.answersContainer);
            resultIcon = itemView.findViewById(R.id.resultIcon);
            explanationText = itemView.findViewById(R.id.explanationText);
        }
    }
}