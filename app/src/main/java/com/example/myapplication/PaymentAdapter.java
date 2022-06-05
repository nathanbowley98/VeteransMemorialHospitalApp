package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    List<Payment> list;
    public PaymentAdapter(List<Payment> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = list.get(position);
        holder.tvTitle.setText(payment.getEmployee());
        holder.tv_amount.setText(payment.getAmount()+"");
        holder.tv_time.setText(payment.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tv_amount;
        TextView tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
