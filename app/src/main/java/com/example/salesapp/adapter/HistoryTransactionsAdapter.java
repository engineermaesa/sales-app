package com.example.salesapp.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.salesapp.R;
import com.example.salesapp.activity.InvoiceActivity;
import com.example.salesapp.model.HistoryTransactions;
import com.example.salesapp.model.Product;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryTransactionsAdapter extends RecyclerView.Adapter<HistoryTransactionsAdapter.ViewHolder> {

    List<HistoryTransactions> historyTransactions;
    Context context;

    public HistoryTransactionsAdapter(List<HistoryTransactions> historyTransactions, Context context) {
        this.historyTransactions = historyTransactions;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_transactions_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryTransactions list = historyTransactions.get(position);
        holder.tvCustomerName.setText(list.getCustomerName());
        holder.tvStatus.setText(list.getStatus());

        DecimalFormat decim = new DecimalFormat("#,###.##");
        String price = decim.format(list.getTotalPrice());
        holder.tvTotalPrice.setText(price.replace(',', '.'));

        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        String inputText = list.getCreatedAt();
        Date date = null;
        try {
            date = inputFormat.parse(inputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputText = outputFormat.format(date);
        holder.tvDate.setText(outputText);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), InvoiceActivity.class);
            intent.putExtra("id",list.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

//        ImageView ivIconStatus;
        TextView tvCustomerName, tvTotalPrice, tvDate, tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
//            ivIconStatus = itemView.findViewById(R.id.image_view_transactions_status);
            tvCustomerName = itemView.findViewById(R.id.text_view_transactions_customer_name);
            tvTotalPrice = itemView.findViewById(R.id.text_view_transactions_total_price);
            tvDate = itemView.findViewById(R.id.text_view_transactions_date);
            tvStatus = itemView.findViewById(R.id.text_view_transactions_status);
        }
    }
}