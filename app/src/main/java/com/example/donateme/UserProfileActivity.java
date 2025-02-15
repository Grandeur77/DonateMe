package com.example.donateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textViewFullName, textViewEmail, textViewMobile;
    private String fullName, email, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar progressBar;
    private PreferencesManager preferencesManager; // SharedPreferences instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        swipeToRefresh();
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        imageView = findViewById(R.id.imageView_Profile_dp);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        preferencesManager = new PreferencesManager(this); // Initialize SharedPreferences

        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loadUserProfileFromCache();  // Load from SharedPreferences first
            showUserProfile(firebaseUser); // Fetch from Firebase if needed
        }

        // Check if profile pic is updated from intent
        if (getIntent().hasExtra("imageUri")) {
            String imageUri = getIntent().getStringExtra("imageUri");
            Picasso.with(UserProfileActivity.this).load(imageUri).into(imageView);
        }

        imageView.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, uploadProfilePicActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void swipeToRefresh() {
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            showUserProfile(authProfile.getCurrentUser());
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    // Load profile from SharedPreferences
    private void loadUserProfileFromCache() {
        fullName = preferencesManager.getFullName();
        email = preferencesManager.getEmail();
        mobile = preferencesManager.getMobile();
        String profilePic = preferencesManager.getProfilePic();

        if (fullName != null && email != null && mobile != null) {
            textViewFullName.setText(fullName);
            textViewEmail.setText(email);
            textViewMobile.setText(mobile);
        }

        if (profilePic != null) {
            Picasso.with(this).load(profilePic).into(imageView);
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    mobile = readUserDetails.mobile;

                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewMobile.setText(mobile);

                    // Fetch profile picture
                    Uri uri = firebaseUser.getPhotoUrl();
                    if (uri != null) {
                        Picasso.with(UserProfileActivity.this).load(uri).into(imageView);
                        preferencesManager.saveUserProfile(fullName, email, mobile, uri.toString()); // Save to SharedPreferences
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        showUserProfile(authProfile.getCurrentUser());
    }
}