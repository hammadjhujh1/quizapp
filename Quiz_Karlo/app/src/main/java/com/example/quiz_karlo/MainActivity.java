package com.example.quiz_karlo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout textInputLayout;
    private TextInputEditText nameInput;
    private MaterialButton startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupEdgeToEdge();

        initializeViews();

        setupStartButton();
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        textInputLayout = findViewById(R.id.textInputLayout);
        nameInput = findViewById(R.id.nameInput);
        startButton = findViewById(R.id.startButton);

        // Set up name input validation
        nameInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateName();
            }
        });
    }

    private void setupStartButton() {
        startButton.setOnClickListener(v -> {
            if (validateName()) {
                startQuiz();
            }
        });
    }

    private boolean validateName() {
        String name = nameInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            textInputLayout.setError("Please enter your name");
            return false;
        }

        if (name.length() < 2) {
            textInputLayout.setError("Name must be at least 2 characters long");
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            textInputLayout.setError("Name can only contain letters and spaces");
            return false;
        }

        textInputLayout.setError(null);
        return true;
    }

    private void startQuiz() {
        String userName = nameInput.getText().toString().trim();

        getSharedPreferences("QuizPrefs", MODE_PRIVATE)
                .edit()
                .putString("USER_NAME", userName)
                .apply();

        Intent intent = new Intent(MainActivity.this, QuizStart.class);
        intent.putExtra("USER_NAME", userName);
        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameInput.setText("");
        textInputLayout.setError(null);
    }
}