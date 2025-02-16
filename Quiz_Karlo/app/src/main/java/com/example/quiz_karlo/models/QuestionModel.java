package com.example.quiz_karlo.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class QuestionModel implements Parcelable {
    private String question;
    private List<String> options;
    private int correctAnswerIndex;
    private int selectedAnswerIndex;
    private String explanation;

    public QuestionModel(String question, List<String> options, int correctAnswerIndex,
                         int selectedAnswerIndex, String explanation) {
        this.question = question;
        this.options = new ArrayList<>(options); // Create a new list to avoid external modifications
        this.correctAnswerIndex = correctAnswerIndex;
        this.selectedAnswerIndex = selectedAnswerIndex;
        this.explanation = explanation;
    }

    // Getters
    public String getQuestion() { return question; }
    public List<String> getOptions() { return new ArrayList<>(options); } // Return a copy to prevent modification
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public int getSelectedAnswerIndex() { return selectedAnswerIndex; }
    public String getExplanation() { return explanation; }

    public void setSelectedAnswerIndex(int selectedAnswerIndex) {
        this.selectedAnswerIndex = selectedAnswerIndex;
    }

    public boolean isCorrect() {
        return correctAnswerIndex == selectedAnswerIndex && selectedAnswerIndex != -1;
    }

    public boolean isAnswered() {
        return selectedAnswerIndex != -1;
    }

    protected QuestionModel(Parcel in) {
        question = in.readString();
        options = new ArrayList<>();
        in.readStringList(options);
        correctAnswerIndex = in.readInt();
        selectedAnswerIndex = in.readInt();
        explanation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringList(options);
        dest.writeInt(correctAnswerIndex);
        dest.writeInt(selectedAnswerIndex);
        dest.writeString(explanation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
        @Override
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        @Override
        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };

    @Override
    public String toString() {
        return "QuestionModel{" +
                "question='" + question + '\'' +
                ", options=" + options +
                ", correctAnswerIndex=" + correctAnswerIndex +
                ", selectedAnswerIndex=" + selectedAnswerIndex +
                '}';
    }
}