package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sophiaxiang.wanderlust.R;

import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ActivityViewHolder> {
    private final Context mContext;
    private final List<String> mAttractions;

    public AttractionAdapter(Context mContext, List<String> mActivities) {
        this.mContext = mContext;
        this.mAttractions = mActivities;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_attraction, parent, false);
        return new AttractionAdapter.ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionAdapter.ActivityViewHolder holder, int position) {
        String attraction = mAttractions.get(position);
        holder.bind(attraction);
    }

    @Override
    public int getItemCount() {
        return mAttractions.size();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivity;
        ImageButton ibDeleteActivity;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivity = itemView.findViewById(R.id.tvAttraction);
            ibDeleteActivity = itemView.findViewById(R.id.ibDeleteAttraction);

            ibDeleteActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAttraction(getAdapterPosition());
                }
            });
        }

        public void bind(String attraction) {
            tvActivity.setText(attraction);
        }

        private void removeAttraction(int position) {
            mAttractions.remove(position);
            notifyItemRemoved(position);
        }
    }
}
