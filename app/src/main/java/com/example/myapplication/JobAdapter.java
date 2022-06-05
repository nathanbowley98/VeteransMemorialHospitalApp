package com.example.myapplication;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    List<Job> list;
    public JobAdapter(List<Job> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Job job = list.get(position);
        Location location = job.getLocation();
        Log.d("testing:", location.getLatitude() + " " + location.getLongitude());
        holder.tvTitle.setText("Job Title:"+job.getJobTitle());
        holder.jobLayoutEmployerEmail.setText("Email:"+job.getEmployerEmail());
        holder.tvDesc.setText("Hourly Rate: " +String.valueOf(job.getCompensation()));
        holder.jobLayoutHourlyRate.setText(job.getHash());
        holder.jobLayoutLatitude.setText("Latitude:"+job.getLocation().getLatitude());
        holder.jobLayoutLongitude.setText("Longitude:"+job.getLocation().getLongitude());
        holder.tvJobCategory.setText(job.getCategory());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView jobLayoutEmployerEmail;
        TextView tvDesc;
        TextView jobLayoutHourlyRate;
        TextView jobLayoutLatitude;
        TextView jobLayoutLongitude;
        TextView tvJobCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.jobLayoutJobTitle);
            jobLayoutEmployerEmail = itemView.findViewById(R.id.jobLayoutEmployerEmail);
            tvDesc = itemView.findViewById(R.id.jobLayoutDescription);
            jobLayoutHourlyRate = itemView.findViewById(R.id.jobLayoutHourlyRate);
            jobLayoutLongitude = itemView.findViewById(R.id.jobLayoutLongitude);
            jobLayoutLatitude = itemView.findViewById(R.id.jobLayoutLatitude);
            tvJobCategory = itemView.findViewById(R.id.jobCategory);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int postion);
    }
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
