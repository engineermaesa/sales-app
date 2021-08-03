package com.example.salesapp.adapter;

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
import com.example.salesapp.model.DetailTransactions;
import com.example.salesapp.model.HistoryTransactions;
import com.example.salesapp.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductInvoiceAdapter extends RecyclerView.Adapter<ProductInvoiceAdapter.ViewHolder> {

    List<DetailTransactions> detailTransactionsList;
    Context context;

    public ProductInvoiceAdapter(List<DetailTransactions> detailTransactionsList, Context context) {
        this.detailTransactionsList = detailTransactionsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_invoice_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DetailTransactions list = detailTransactionsList.get(position);
        Picasso.get()
                .load(RetrofitBuilder.BASE_URL_IMAGES + list.getImg())
                .into(holder.ivIconStatus);
        holder.tvProductName.setText(list.getProductName());
        holder.tvQty.setText(String.valueOf(list.getAmount()));
        DecimalFormat decim = new DecimalFormat("#,###.##");
        int subTotal = list.getAmount()*list.getPrice();
        String price = decim.format(subTotal);
        holder.tvTotalPrice.setText(price.replace(',', '.'));
    }

    @Override
    public int getItemCount() {
        return detailTransactionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIconStatus;
        TextView tvProductName, tvTotalPrice, tvQty;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIconStatus = itemView.findViewById(R.id.image_view_product_in_invoice);
            tvProductName = itemView.findViewById(R.id.text_view_invoice_product_name);
            tvQty = itemView.findViewById(R.id.text_view_invoice_product_qty);
            tvTotalPrice = itemView.findViewById(R.id.text_view_invoice_product_price);
        }
    }
}