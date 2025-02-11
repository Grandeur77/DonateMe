package com.example.donateme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class feedback_history extends AppCompatActivity {
    RecyclerView fRecyclerView;
    FeedbackHistoryAdapter feedAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_history);
        fRecyclerView=(RecyclerView) findViewById(R.id.reviews);
        fRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ReadWriteUserFeedbackHistory> options=new FirebaseRecyclerOptions.Builder<ReadWriteUserFeedbackHistory>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Reviews").child(FirebaseAuth.getInstance().getCurrentUser().getUid()),ReadWriteUserFeedbackHistory.class).build();
        feedAdapter = new FeedbackHistoryAdapter(options);
        fRecyclerView.setAdapter(feedAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        feedAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        feedAdapter.stopListening();
    }
}