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
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.model.HistoryTransactions;
import com.example.salesapp.model.Product;
import com.example.salesapp.model.Visit;
import com.squareup.picasso.Picasso;

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

    ArrayList<Visit> historyTransactions;
    Context context;

    public HistoryTransactionsAdapter(Context context) {
        historyTransactions = new ArrayList<>();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_transactions_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Visit list = historyTransactions.get(position);

        Picasso.get()
                .load(RetrofitBuilder.BASE_URL_IMAGES + list.getPhoto())
                .into(holder.ivIconStatus);

        holder.tvCustomerName.setText(list.getName());
        holder.tvNoTelp.setText(list.getPhone());
        holder.tvAddress.setText(list.getAddress());
        holder.tvResult.setText(list.getResult());

//        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
//        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
//
//        String inputText = list.getVisitedAt();
//        Date date = null;
//        try {
//            date = inputFormat.parse(inputText);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String outputText = outputFormat.format(date);

        holder.tvDate.setText(list.getVisitedAt());
    }

    @Override
    public int getItemCount() {
        return historyTransactions.size();
    }

    public void updateData(List<Visit> filesList, int flag) {
        if (flag == 0) { //append
            for (int i = 0; i < filesList.size(); i++) {
                historyTransactions.add(filesList.get(i));
                notifyItemInserted(getItemCount());
            }
        } else { //clear all
            filesList.clear();
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIconStatus;
        TextView tvDate, tvCustomerName, tvNoTelp, tvAddress, tvResult;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIconStatus = itemView.findViewById(R.id.image_view_visit_photo);
            tvDate = itemView.findViewById(R.id.text_view_transactions_date);
            tvCustomerName = itemView.findViewById(R.id.text_view_transactions_customer_name);
            tvNoTelp = itemView.findViewById(R.id.text_view_transactions_total_price);
            tvAddress = itemView.findViewById(R.id.text_view_transactions_status);
            tvResult = itemView.findViewById(R.id.text_view_visit_result);
        }
    }
}