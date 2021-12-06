package com.example.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.salesapp.R;
import com.example.salesapp.adapter.HistoriTransactionAdapter;
import com.example.salesapp.adapter.NotificationAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.GetResponseNotification;
import com.example.salesapp.model.GetResponseTransaction;
import com.example.salesapp.model.Notification;
import com.example.salesapp.model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoriTransactionActivity extends AppCompatActivity {

    private static final String TAG = "HistoriTransaction";

    private Toolbar mToolbar;
    private TextView mTitleToolbar;
    private View mProgressBar;
    private ProgressBar mCycleProgressBar;
    private RelativeLayout relativeLayoutEmpty;

    private RecyclerView recyclerView;
    private HistoriTransactionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<GetResponseTransaction> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_transaction);

        recyclerView = findViewById(R.id.recycler_view);
        mProgressBar = findViewById(R.id.progress_bar_login);
        mCycleProgressBar = mProgressBar.findViewById(R.id.progress_bar_cycle);
        relativeLayoutEmpty = findViewById(R.id.relative_layout_empty);

        mToolbar = findViewById(R.id.toolbar);
        mTitleToolbar = mToolbar.findViewById(R.id.toolbar_title);
        mTitleToolbar.setText("Histori Transaksi");

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        getDataTransactions();
    }

    private void getDataTransactions() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCycleProgressBar.setVisibility(View.VISIBLE);
        call = service.getTransaction();
        call.enqueue(new Callback<GetResponseTransaction>() {
            @Override
            public void onResponse(Call<GetResponseTransaction> call, Response<GetResponseTransaction> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    List<Transaction> list = response.body().getTransaction();
                    if (list.size() > 0) {
                        adapter = new HistoriTransactionAdapter(list, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }else {
                        relativeLayoutEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GetResponseTransaction> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}