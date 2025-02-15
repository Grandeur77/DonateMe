package com.example.donateme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
public class HistoryAdapter extends FirebaseRecyclerAdapter<ReadWriteUserHistory,HistoryAdapter.myViewHolder> {


    public HistoryAdapter(@NonNull FirebaseRecyclerOptions<ReadWriteUserHistory> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ReadWriteUserHistory model) {
        holder.item.setText(model.getItem());
        holder.description.setText(model.getDescription());
        holder.category.setText(model.getCategory());
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new myViewHolder(view);
    }
    class myViewHolder extends  RecyclerView.ViewHolder{
        TextView item , description , category;
        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);
            item=(TextView)itemView.findViewById(R.id.rcitem);
            description=(TextView)itemView.findViewById(R.id.rcDescription);
            category=(TextView)itemView.findViewById(R.id.category);
        }
    }
}
