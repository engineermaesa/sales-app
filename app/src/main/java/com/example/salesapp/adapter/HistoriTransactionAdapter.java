package com.example.salesapp.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.salesapp.R;
import com.example.salesapp.model.Notification;
import com.example.salesapp.model.Transaction;

import java.text.DecimalFormat;
import java.util.List;

public class HistoriTransactionAdapter extends RecyclerView.Adapter<HistoriTransactionAdapter.ViewHolder> {

    List<Transaction> transactionList;
    Context context;

    public HistoriTransactionAdapter(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @Override
    public HistoriTransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_histori_transaction_adapter, parent, false);
        return new HistoriTransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoriTransactionAdapter.ViewHolder holder, int position) {
        Transaction list = transactionList.get(position);

        holder.tvInvoice.setText(list.getInvoiceNumber());
        holder.tvName.setText(list.getCustomerName());
        holder.tvAddress.setText(list.getAddress());
        holder.tvNoted.setText(list.getNoted());

        DecimalFormat decim = new DecimalFormat("#,###.##");
        String formatPrice = decim.format(list.getTotalPrice());
        holder.tvPrice.setText(formatPrice);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvInvoice, tvName, tvAddress, tvPrice, tvNoted;

        public ViewHolder(View itemView) {
            super(itemView);
            tvInvoice = itemView.findViewById(R.id.text_view_history_invoice);
            tvName = itemView.findViewById(R.id.text_view_history_name);
            tvAddress = itemView.findViewById(R.id.text_view_history_address);
            tvPrice = itemView.findViewById(R.id.text_view_history_price);
            tvNoted = itemView.findViewById(R.id.text_view_history_note);
        }
    }

}