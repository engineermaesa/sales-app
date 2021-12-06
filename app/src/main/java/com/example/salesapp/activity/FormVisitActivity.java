package com.example.salesapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.adapter.ProductInvoiceAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.DetailTransactions;
import com.example.salesapp.model.GetResponseHistoryTransactions;
import com.example.salesapp.model.GetResponseToken;
import com.example.salesapp.model.HistoryTransactions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormVisitActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final int REQUEST_IMAGE = 100;
    Uri uri;

    private Toolbar mToolbar;
    private TextView mTitleToolbar;
    private EditText etName, etAddress, etPhone, etProduct, etResult;
    private Button btnUploadPayment;
    private View mProgressBar;
    private ProgressBar mCycleProgressBar;
    private RadioGroup radioGroupStatus;
    private RadioButton radioButtonStatus;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<RequestBody> callUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_visit);

        etName = findViewById(R.id.edit_text_name);
        etAddress = findViewById(R.id.edit_text_address);
        etPhone = findViewById(R.id.edit_text_phone);
        etProduct = findViewById(R.id.edit_text_product);
        etResult = findViewById(R.id.edit_text_result);
        radioGroupStatus = findViewById(R.id.radio_group_status);
        btnUploadPayment = findViewById(R.id.button_upload_visit);
        mProgressBar = findViewById(R.id.progress_bar_login);
        mCycleProgressBar = mProgressBar.findViewById(R.id.progress_bar_cycle);

        mToolbar = findViewById(R.id.toolbar);
        mTitleToolbar = mToolbar.findViewById(R.id.toolbar_title);
        mTitleToolbar.setText("Form Visit");

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

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
        String filePath = getRealPathFromURIPath(contentURI,FormVisitActivity.this);
        File file = new File(filePath);
        Log.d("File",""+file.getName());

        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo",file.getName(),mFile);

        int selectedId = radioGroupStatus.getCheckedRadioButtonId();
        radioButtonStatus = findViewById(selectedId);

        String nameOne = etName.getText().toString();
        String addressOne = etAddress.getText().toString();
        String phoneOne = etPhone.getText().toString();
        String productOne = etProduct.getText().toString();
        String resultOne = etResult.getText().toString();
        String statusOne = radioButtonStatus.getText().toString();

        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), nameOne);
        RequestBody address = RequestBody.create(MediaType.parse("multipart/form-data"), addressOne);
        RequestBody phone = RequestBody.create(MediaType.parse("multipart/form-data"), phoneOne);
        RequestBody product = RequestBody.create(MediaType.parse("multipart/form-data"), productOne);
        RequestBody result = RequestBody.create(MediaType.parse("multipart/form-data"), resultOne);
        RequestBody status = RequestBody.create(MediaType.parse("multipart/form-data"), statusOne);

        mProgressBar.setVisibility(View.VISIBLE);
        mCycleProgressBar.setVisibility(View.VISIBLE);

        callUpload = service.uploadVisit(name,address,phone,product,result,status,body);
        callUpload.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(FormVisitActivity.this, NavigationActivity.class));
                    finish();
                    Toast.makeText(FormVisitActivity.this, "Upload success...", Toast.LENGTH_SHORT).show();
                }
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
                startActivity(new Intent(FormVisitActivity.this, NavigationActivity.class));
                finish();
                Log.e("Error : ",t.getMessage());
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