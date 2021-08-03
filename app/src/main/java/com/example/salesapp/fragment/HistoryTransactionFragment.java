package com.example.salesapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.salesapp.R;
import com.example.salesapp.activity.LoginActivity;
import com.example.salesapp.adapter.AllProductAdapter;
import com.example.salesapp.adapter.HistoryTransactionsAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.GetResponseHistoryTransactions;
import com.example.salesapp.model.GetResponseHistoryTransactionsList;
import com.example.salesapp.model.GetResponseProduct;
import com.example.salesapp.model.HistoryTransactions;
import com.example.salesapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryTransactionFragment extends Fragment {

    private static final String TAG = "HistoryTransaction";

    private Toolbar mToolbar;
    private TextView mTitleToolbar;
    private View mProgressBar;
    private ProgressBar mCycleProgressBar;
    private RelativeLayout relativeLayoutEmpty;

    private RecyclerView recyclerView;
    private HistoryTransactionsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<GetResponseHistoryTransactionsList> call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_transaction, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        mProgressBar = view.findViewById(R.id.progress_bar_login);
        mToolbar = view.findViewById(R.id.toolbar);
        relativeLayoutEmpty = view.findViewById(R.id.relative_layout_empty);

        mCycleProgressBar = mProgressBar.findViewById(R.id.progress_bar_cycle);
        mTitleToolbar = mToolbar.findViewById(R.id.toolbar_title);
        mTitleToolbar.setText("History Transactions");

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        getDataHistoryTransactions();

        return view;
    }

    private void getDataHistoryTransactions() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCycleProgressBar.setVisibility(View.VISIBLE);
        call = service.getHistoryTransactionsWithoutDetail();
        call.enqueue(new Callback<GetResponseHistoryTransactionsList>() {
            @Override
            public void onResponse(Call<GetResponseHistoryTransactionsList> call, Response<GetResponseHistoryTransactionsList> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    List<HistoryTransactions> list = response.body().getHistoryTransactionsList();
                    if (list.size() > 0) {
                        adapter = new HistoryTransactionsAdapter(list, getContext());
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
            public void onFailure(Call<GetResponseHistoryTransactionsList> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }
        });
    }
}