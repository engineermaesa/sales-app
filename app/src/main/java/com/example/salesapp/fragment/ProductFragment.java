package com.example.salesapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.activity.FormVisitActivity;
import com.example.salesapp.activity.HistoriTransactionActivity;
import com.example.salesapp.activity.LoginActivity;
import com.example.salesapp.activity.NavigationActivity;
import com.example.salesapp.adapter.AllProductAdapter;
import com.example.salesapp.adapter.ProductAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.GetResponseProduct;
import com.example.salesapp.model.GetResponseTransaction;
import com.example.salesapp.model.Product;
import com.example.salesapp.util.Converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private Toolbar mToolbar;
    private TextView mTitleToolbar;
    private View mProgressBar;
    private ProgressBar mCycleProgressBar;

    private EditText etName,etPhone,etAddress,etOrigin,etDestination,etPrice,etNote;
    private Button btnSave;
    private ImageView ivHistoryTransaction;
    private RadioGroup radioGroupStatus;
    private RadioButton radioButtonStatus;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<GetResponseTransaction> call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        etName = view.findViewById(R.id.edit_text_customer_name);
        etPhone = view.findViewById(R.id.edit_text_customer_phone);
        etAddress = view.findViewById(R.id.edit_text_customer_address);
        etOrigin = view.findViewById(R.id.edit_text_customer_origin);
        etDestination = view.findViewById(R.id.edit_text_customer_destination);
        etNote = view.findViewById(R.id.edit_text_customer_note);
        radioGroupStatus = view.findViewById(R.id.radio_group_status);
        btnSave = view.findViewById(R.id.button_transaction_save);
        ivHistoryTransaction = view.findViewById(R.id.image_view_history_transaction);
        etPrice = view.findViewById(R.id.edit_text_customer_price);
        etPrice.addTextChangedListener(onTextChangedListener());

        mProgressBar = view.findViewById(R.id.progress_bar_login);
        mCycleProgressBar = mProgressBar.findViewById(R.id.progress_bar_cycle);
        mToolbar = view.findViewById(R.id.toolbar);
        mTitleToolbar = mToolbar.findViewById(R.id.toolbar_title);
        mTitleToolbar.setText("Transaksi");

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        btnSave.setOnClickListener(v -> saveTransaction(view));
        ivHistoryTransaction.setOnClickListener(v -> startActivity(new Intent(getContext(), HistoriTransactionActivity.class)));

        return view;
    }

    private void saveTransaction(View view) {
        int selectedId = radioGroupStatus.getCheckedRadioButtonId();
        radioButtonStatus = view.findViewById(selectedId);

        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        String note = etNote.getText().toString();
        String status = radioButtonStatus.getText().toString();
        String price = etPrice.getText().toString().replaceAll(",", "");

        mProgressBar.setVisibility(View.VISIBLE);
        mCycleProgressBar.setVisibility(View.VISIBLE);

        call = service.transaction(name,phone,address,price,origin,destination,note,status);
        call.enqueue(new Callback<GetResponseTransaction>() {
            @Override
            public void onResponse(Call<GetResponseTransaction> call, Response<GetResponseTransaction> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(getContext(), NavigationActivity.class));
                    getActivity().finish();
                    Toast.makeText(getContext(), "Transaksi berhasil...", Toast.LENGTH_SHORT).show();
                }
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GetResponseTransaction> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etPrice.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    etPrice.setText(formattedString);
                    etPrice.setSelection(etPrice.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                etPrice.addTextChangedListener(this);
            }
        };
    }
}