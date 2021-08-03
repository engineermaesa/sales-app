package com.example.salesapp.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.salesapp.R;
import com.example.salesapp.model.HistoryTransactions;
import com.example.salesapp.model.Notification;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<Notification> notificationList;
    Context context;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notification_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification list = notificationList.get(position);
        holder.tvTitleNotification.setText(list.getTitle());
        holder.tvDescNotification.setText(list.getBody());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitleNotification, tvDescNotification;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitleNotification = itemView.findViewById(R.id.text_view_notification_title);
            tvDescNotification = itemView.findViewById(R.id.text_view_notification_description);
        }
    }
}