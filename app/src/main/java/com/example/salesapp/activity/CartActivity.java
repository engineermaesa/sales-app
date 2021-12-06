package com.example.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.adapter.CartAdapter;
import com.example.salesapp.adapter.AllProductAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.fragment.HomeFragment;
import com.example.salesapp.model.GetResponseProfile;
import com.example.salesapp.model.Product;
import com.example.salesapp.model.Profile;
import com.example.salesapp.model.Transaction;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";

    private Toolbar mToolbar;
    private TextView mTitleToolbar;

    private View mProgressBar;
    private ProgressBar mCycleProgressBar;

    public static TextView grandTotal;
    public static int grandTotalplus;
    public static ArrayList<Product> temparraylist;
    private ImageView ivPelanggan;
    private EditText etPelanggan, etAlamat, etCatatan;
    RecyclerView cartRecyclerView;
    CartAdapter cartAdapter;
    public static Button proceedToBook;
    Context context;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<Transaction> callBooking;
    private Call<GetResponseProfile> callProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        context = this;

        temparraylist = new ArrayList<>();
        proceedToBook = findViewById(R.id.button_pesan);
        grandTotal = findViewById(R.id.text_view_total);
        mProgressBar = findViewById(R.id.progress_bar_login);
        mCycleProgressBar = mProgressBar.findViewById(R.id.progress_bar_cycle);
        etPelanggan = findViewById(R.id.edit_text_pelanggan);
        etAlamat = findViewById(R.id.edit_text_alamat);
        etCatatan = findViewById(R.id.edit_text_catatan);
        ivPelanggan = findViewById(R.id.image_view_photo);

        etAlamat.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etAlamat.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etCatatan.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etCatatan.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etPelanggan.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etPelanggan.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mToolbar = findViewById(R.id.toolbar);
        mTitleToolbar = mToolbar.findViewById(R.id.toolbar_title);
        mTitleToolbar.setText("Cart");

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(CartActivity.this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        displayLocation();
        displayImage();

        HomeFragment.cart_count = 0;

        for (int i = 0; i < AllProductAdapter.dataList.size(); i++) {
            for (int j = i + 1; j < AllProductAdapter.dataList.size(); j++) {
                if (AllProductAdapter.dataList.get(i).getImg().equals(AllProductAdapter.dataList.get(j).getImg())) {
                    AllProductAdapter.dataList.get(i).setId(AllProductAdapter.dataList.get(j).getId());
                    AllProductAdapter.dataList.get(i).setStok(AllProductAdapter.dataList.get(j).getStok());
                    AllProductAdapter.dataList.get(i).setTotalCash(AllProductAdapter.dataList.get(j).getTotalCash());
                    AllProductAdapter.dataList.remove(j);
                    j--;
                }
            }
        }

        temparraylist.addAll(AllProductAdapter.dataList);
        AllProductAdapter.dataList.clear();

        cartRecyclerView = findViewById(R.id.recycler_view_cart);
        cartAdapter = new CartAdapter(temparraylist, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(mLayoutManager);
        cartRecyclerView.setAdapter(cartAdapter);

        for (int i = 0; i < temparraylist.size(); i++) {
            grandTotalplus = grandTotalplus + temparraylist.get(i).getTotalCash();
        }

        DecimalFormat decim = new DecimalFormat("#,###.##");
        String price = decim.format(grandTotalplus);
        grandTotal.setText(price.replace(',', '.'));
    }

    private void displayImage() {
        callProfile = service.getProfile();
        callProfile.enqueue(new Callback<GetResponseProfile>() {
            @Override
            public void onResponse(Call<GetResponseProfile> call, Response<GetResponseProfile> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    Profile profile = response.body().getProfile();
                    Picasso.get()
                            .load(RetrofitBuilder.BASE_URL_IMAGES + profile.getAvatar())
                            .into(ivPelanggan);
                }
            }

            @Override
            public void onFailure(Call<GetResponseProfile> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void displayLocation() {
        proceedToBook.setOnClickListener(v -> {
            Transaction detail = new Transaction();

            String originalString = grandTotal.getText().toString();
            originalString = originalString.replaceAll("[.]", "");

            detail.setAddress(etAlamat.getText().toString());
            detail.setNoted(etCatatan.getText().toString());
            detail.setCustomerName(etPelanggan.getText().toString());
//            detail.setTotalPrice(originalString);
            detail.setProducts(temparraylist);

            mProgressBar.setVisibility(View.VISIBLE);
            mCycleProgressBar.setVisibility(View.VISIBLE);

            callBooking = service.booking(detail);
            callBooking.enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        Intent intentToHistory = new Intent(CartActivity.this, NavigationActivity.class);
                        intentToHistory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentToHistory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentToHistory);
                        finish();
                        Toast.makeText(CartActivity.this, "Permintaan sedang diproses, lihat belanjaan Anda!", Toast.LENGTH_SHORT).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mCycleProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    mCycleProgressBar.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                grandTotalplus = 0;
                for (int i = 0; i < temparraylist.size(); i++) {}
                AllProductAdapter.dataList.addAll(temparraylist);
                HomeFragment.cart_count = (temparraylist.size());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        grandTotalplus = 0;
        for (int i = 0; i < temparraylist.size(); i++) {
            HomeFragment.cart_count = (temparraylist.size());
        }
        AllProductAdapter.dataList.addAll(temparraylist);
    }
}