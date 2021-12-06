package com.example.salesapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.activity.FormVisitActivity;
import com.example.salesapp.activity.LoginActivity;
import com.example.salesapp.adapter.AllProductAdapter;
import com.example.salesapp.adapter.HistoryTransactionsAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.GetResponseHistoryTransactions;
import com.example.salesapp.model.GetResponseHistoryTransactionsList;
import com.example.salesapp.model.GetResponseProduct;
import com.example.salesapp.model.GetResponseVisit;
import com.example.salesapp.model.HistoryTransactions;
import com.example.salesapp.model.Page;
import com.example.salesapp.model.Performance;
import com.example.salesapp.model.Product;
import com.example.salesapp.pagination.PaginationScrollListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryTransactionFragment extends Fragment {

    private static final String TAG = "VisitFragment";
    private ProgressBar mProgressBarPagination;
    private FloatingActionButton floatingActionButton;

    private HistoryTransactionsAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<Page> call;

    private Context context;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_transaction, container, false);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        floatingActionButton = view.findViewById(R.id.fab1);

        try {
            context = getActivity();
            recyclerView = view.findViewById(R.id.recycler_view);
            mProgressBarPagination = view.findViewById(R.id.progress_bar_pagination);

            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new HistoryTransactionsAdapter(context);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading =  true;
                    if (!isLastPage) {
                        new Handler().postDelayed(() -> loadData(page), 200);
                    }
                }

                @Override
                public boolean isLastPage() {
                    return isLastPage;
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });
            loadData(page);
        }catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        floatingActionButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FormVisitActivity.class));
        });

        return view;
    }

    private void loadData(int page) {
        mProgressBarPagination.setVisibility(View.VISIBLE);
        call = service.getVisit(1);
        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {
                Page serverResponse = response.body();
                resultAction(serverResponse);
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                Log.e(TAG, t.toString());
                mProgressBarPagination.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resultAction(Page model) {
        mProgressBarPagination.setVisibility(View.INVISIBLE);
        isLoading = false;
        if (model != null) {
            adapter.updateData(model.getData(), 0);
            if (model.getCurrentPage() == model.getLastPage()) {
                isLastPage = true;
            }
            page = model.getCurrentPage() + 1;
        }
    }
}