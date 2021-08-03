package com.example.salesapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.adapter.ProductInvoiceAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.DetailTransactions;
import com.example.salesapp.model.GetResponseHistoryTransactions;
import com.example.salesapp.model.HistoryTransactions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final int REQUEST_IMAGE = 100;
    Uri uri;

    private Toolbar mToolbar;
    private TextView mTitleToolbar;
    private TextView tvInvoiceNumber, tvTotalPrice, tvStatus, tvPayment;
    private Button btnDownloadInvoice, btnUploadPayment;
    private View mProgressBar;
    private ProgressBar mCycleProgressBar;

    private RecyclerView recyclerView;
    private ProductInvoiceAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<GetResponseHistoryTransactions> call;
    private Call<RequestBody> callUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        btnDownloadInvoice = findViewById(R.id.button_download_invoice);
        btnUploadPayment = findViewById(R.id.button_upload_payment);
        tvInvoiceNumber = findViewById(R.id.text_view_invoice_number);
        tvTotalPrice = findViewById(R.id.text_view_invoice_total_payment);
        tvStatus = findViewById(R.id.text_view_invoice_status);
        tvPayment = findViewById(R.id.text_view_payment);
        recyclerView = findViewById(R.id.recycler_view);
        mProgressBar = findViewById(R.id.progress_bar_login);
        mCycleProgressBar = mProgressBar.findViewById(R.id.progress_bar_cycle);

        mToolbar = findViewById(R.id.toolbar);
        mTitleToolbar = mToolbar.findViewById(R.id.toolbar_title);
        mTitleToolbar.setText("Invoice");

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

        getDataInvoice();

        btnUploadPayment.setOnClickListener(v -> execute(REQUEST_IMAGE));
    }

    void execute(int requestCode){
        switch (requestCode){
            case REQUEST_IMAGE:
                if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                    openGalleryIntent.setType("image/*");
                    startActivityForResult(openGalleryIntent, REQUEST_IMAGE);
                    break;
                }else{
                    EasyPermissions.requestPermissions(this,"Izinkan Aplikasi Mengakses Storage?",REQUEST_IMAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
                }
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    void uploadFile(Uri contentURI){
        String filePath = getRealPathFromURIPath(contentURI,InvoiceActivity.this);
        File file = new File(filePath);
        Log.d("File",""+file.getName());

        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("bukti",file.getName(),mFile);

        String method = "PUT";
        RequestBody _method = RequestBody.create(okhttp3.MultipartBody.FORM, method);

        mProgressBar.setVisibility(View.VISIBLE);
        mCycleProgressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        callUpload = service.uploadPayment(intent.getIntExtra("id",0),body,_method);
        callUpload.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InvoiceActivity.this, "Upload success...", Toast.LENGTH_SHORT).show();
                    finish();
                }
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
                Toast.makeText(InvoiceActivity.this, "Upload failed...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK){
            uri = data.getData();
            uploadFile(uri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == REQUEST_IMAGE){
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(requestCode == REQUEST_IMAGE){
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataInvoice() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCycleProgressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        call = service.getHistoryTransactions(intent.getIntExtra("id",0));
        call.enqueue(new Callback<GetResponseHistoryTransactions>() {
            @Override
            public void onResponse(Call<GetResponseHistoryTransactions> call, Response<GetResponseHistoryTransactions> response) {
                if (response.isSuccessful()) {
                    HistoryTransactions historyTransactions = response.body().getHistoryTransactions();
                    List<DetailTransactions> detailTransactions = response.body().getHistoryTransactions().getDetailTransactions();

                    tvInvoiceNumber.setText("Invoice-"+historyTransactions.getInvoiceNumber());
                    tvStatus.setText(historyTransactions.getStatus());
                    tvPayment.setText(historyTransactions.getPayment());

                    DecimalFormat decim = new DecimalFormat("#,###.##");
                    String price = decim.format(historyTransactions.getTotalPrice());
                    tvTotalPrice.setText(price.replace(',', '.'));

                    String invoice = historyTransactions.getInvoice();

                    adapter = new ProductInvoiceAdapter(detailTransactions, getApplicationContext());
                    recyclerView.setAdapter(adapter);

                    btnDownloadInvoice.setOnClickListener(v -> {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW);
                        webIntent.setData(Uri.parse(invoice));
                        startActivity(webIntent);
                    });
                }
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GetResponseHistoryTransactions> call, Throwable t) {
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