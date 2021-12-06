package com.example.salesapp.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.GetResponseToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private LoginActivity self;

    private TextView tvForgotPass;
    private EditText etEmail;
    private ShowHidePasswordEditText etPass;
    private Button btnLogin;

    private View mProgressBar;
    private ProgressBar mCycleProgressBar;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<GetResponseToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        self = this;

        initView();
        setupService();
        navigate();
    }

    private void navigate() {
        btnLogin.setOnClickListener(view -> login());
        tvForgotPass.setOnClickListener(view -> forgot());
    }

    private void forgot() {
        startActivity(new Intent(self, ForgotPasswordActivity.class));
    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.w(TAG, "Token Firebase: " + token);

        mProgressBar.setVisibility(View.VISIBLE);
        mCycleProgressBar.setVisibility(View.VISIBLE);

        call = service.login(email,password,"fyaVE8fPiDQ:APA91bFuQfKdIN4dCqc17Y0fjTjKfMq1Au81nzzVekaIhToxu9a638lKrRbm0n4GDZeaZgi5HIvFl_RZhQTxqW-f8ERCnmyUnTwG6tChXXPjDwhZLG6BHfTrYq6qbovR3pDCswDpU92m");
        call.enqueue(new Callback<GetResponseToken>() {
            @Override
            public void onResponse(Call<GetResponseToken> call, Response<GetResponseToken> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    tokenManager.saveToken(response.body().getData());
                    startActivity(new Intent(self, NavigationActivity.class));
                    finish();
                }
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GetResponseToken> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                mCycleProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initView() {
        tvForgotPass = findViewById(R.id.text_view_forgot_password);
        etEmail = findViewById(R.id.edit_text_email);
        etPass = findViewById(R.id.edit_text_password);
        btnLogin = findViewById(R.id.button_login);
        mProgressBar = findViewById(R.id.progress_bar_login);
        mCycleProgressBar = mProgressBar.findViewById(R.id.progress_bar_cycle);
    }

    private void setupService() {
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if (tokenManager.getToken().getToken() != null) {
            startActivity(new Intent(self, NavigationActivity.class));
            finish();
        }
        service = RetrofitBuilder.createService(ApiService.class);
    }
}

