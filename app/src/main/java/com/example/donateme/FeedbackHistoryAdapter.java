package com.example.donateme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
public class FeedbackHistoryAdapter extends FirebaseRecyclerAdapter<ReadWriteUserFeedbackHistory,FeedbackHistoryAdapter.myViewHolder> {


    public FeedbackHistoryAdapter(@NonNull FirebaseRecyclerOptions<ReadWriteUserFeedbackHistory> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ReadWriteUserFeedbackHistory model) {
        holder.urNm.setText(model.getName());
        holder.description.setText(model.getDescription());
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
        return new myViewHolder(view);
    }
    class myViewHolder extends  RecyclerView.ViewHolder{
        TextView urNm , description;
        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);
            urNm=(TextView)itemView.findViewById(R.id.rName);
            description=(TextView)itemView.findViewById(R.id.rDescription);
        }
    }
}
