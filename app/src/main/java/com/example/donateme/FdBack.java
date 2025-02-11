package com.example.donateme;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.donateme.databinding.ActivityFeedbackBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FdBack extends AppCompatActivity {
    private ActivityFeedbackBinding binding;
    private AppCompatButton ButtonSubmit;
    private TextInputEditText yourName, description;
    private TextInputLayout nameLayout, descriptionLayout;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    private void FeedbackDataPushFunction(String key) {
        String urNm = yourName.getText().toString();
        String desc = description.getText().toString();
        String name = getName();
    }

    private void insertDataInFeedbackHistoryNodeInDataBase() {
        Map<String, Object> feedMap = new HashMap<>();
        feedMap.put("urNm", yourName.getText().toString());
        feedMap.put("description", description.getText().toString());

        FirebaseDatabase.getInstance().getReference()
                .child("Reviews")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push()
                .setValue(feedMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(FdBack.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FdBack.this, "Error while pushing data into Reviews node", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchNameAndDescription() {
        if (!validateName() || !validateDescription()) {
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            insertDataInFeedbackHistoryNodeInDataBase();
        }
    }

    private Boolean validateName() {
        String val = yourName.getText().toString();
        if (val.isEmpty()) {
            nameLayout.setError("Field cannot be empty");
            nameLayout.requestFocus();
            return false;
        } else {
            nameLayout.setError(null);
            nameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateDescription() {
        String val = description.getText().toString();
        if (val.isEmpty()) {
            descriptionLayout.setError("Field cannot be empty");
            descriptionLayout.requestFocus();
            return false;
        } else {
            descriptionLayout.setError(null);
            descriptionLayout.setErrorEnabled(false);
            return true;
        }
    }

    private String getName() {
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        return (firebaseUser != null && firebaseUser.getDisplayName() != null) ? firebaseUser.getDisplayName() : "Anonymous";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authProfile = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        ButtonSubmit = findViewById(R.id.button_submit);
        yourName = findViewById(R.id.name);
        description = findViewById(R.id.Description);
        nameLayout = findViewById(R.id.nameLayout);
        descriptionLayout = findViewById(R.id.DescriptionLayout);

        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchNameAndDescription();
            }
        });
    }
}
