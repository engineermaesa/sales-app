package com.example.salesapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.activity.CartActivity;
import com.example.salesapp.activity.LoginActivity;
import com.example.salesapp.activity.NotificationActivity;
import com.example.salesapp.activity.PrivacyActivity;
import com.example.salesapp.adapter.AllProductAdapter;
import com.example.salesapp.adapter.BookAdapter;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.Book;
import com.example.salesapp.model.GetResponseBook;
import com.example.salesapp.model.GetResponseProduct;
import com.example.salesapp.model.GetResponseProfile;
import com.example.salesapp.model.Product;
import com.example.salesapp.model.Profile;
import com.example.salesapp.util.Converter;
import com.sccomponents.gauges.ScArcGauge;
import com.sccomponents.gauges.ScGauge;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    public static int cart_count = 0;

    private ImageView ivNotification;
    private ImageView ivAvatar;
    private TextView tvName, tvNik, tvDivision, tvTarget, tvAchieved, tvOmset, tvOverachieved;
    private TextView tvTotalMerchants;
    private WebView webViewTnc, webViewVisit;

    private BookAdapter bookAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<GetResponseProfile> callProfile;
    private Call<GetResponseBook> call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        webViewTnc = view.findViewById(R.id.web_view_tnc);
        webViewVisit = view.findViewById(R.id.web_view_visit);
        ivAvatar = view.findViewById(R.id.image_view_photo);
        tvName = view.findViewById(R.id.text_view_name);
        tvNik = view.findViewById(R.id.text_view_nik);
        tvDivision = view.findViewById(R.id.text_view_division);
        ivNotification = view.findViewById(R.id.image_view_notification);
        tvTarget = view.findViewById(R.id.text_view_target);
        tvAchieved = view.findViewById(R.id.text_view_achieved);
        tvOmset = view.findViewById(R.id.text_view_omset);
        tvOverachieved = view.findViewById(R.id.text_view_over_achieved);
        tvTotalMerchants = view.findViewById(R.id.text_view_total_merchants);

        recyclerView = view.findViewById(R.id.recycler_view_book);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createService(ApiService.class);
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        getDataProfile();
        getDataBook();

        ivNotification.setOnClickListener(v -> startActivity(new Intent(getContext(), NotificationActivity.class)));

        return view;
    }

    private void getDataBook() {
        call = service.getBook();
        call.enqueue(new Callback<GetResponseBook>() {
            @Override
            public void onResponse(Call<GetResponseBook> call, Response<GetResponseBook> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    List<Book> bookList = response.body().getData();
                    bookAdapter = new BookAdapter(getContext(), bookList);
                    recyclerView.setAdapter(bookAdapter);
                }
            }

            @Override
            public void onFailure(Call<GetResponseBook> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getDataProfile() {
        callProfile = service.getProfile();
        callProfile.enqueue(new Callback<GetResponseProfile>() {
            @Override
            public void onResponse(Call<GetResponseProfile> call, Response<GetResponseProfile> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    Profile profile = response.body().getProfile();
                    Picasso.get()
                            .load(RetrofitBuilder.BASE_URL_IMAGES + profile.getCompanyLogo())
                            .into(ivAvatar);
                    tvName.setText(profile.getName());
                    tvNik.setText(profile.getNik());
                    tvDivision.setText(profile.getDevisionName());
                    tvTarget.setText(String.valueOf(profile.getVisit_performance().getTarget()));
                    tvAchieved.setText(String.valueOf(profile.getVisit_performance().getAchieved()));
                    tvTotalMerchants.setText(String.valueOf(profile.getNewPartner()));

                    String low = String.valueOf(profile.getPerformance().getTargetLow());
                    String middle = String.valueOf(profile.getPerformance().getTargetMidle());
                    String high = String.valueOf(profile.getPerformance().getTargetHigh());
                    String achieved = String.valueOf(profile.getPerformance().getAchieved());

                    String achievedVisit = String.valueOf(profile.getVisit_performance().getAchieved());
                    String target = String.valueOf(profile.getVisit_performance().getTarget());

                    webViewTnc.getSettings().setJavaScriptEnabled(true);
                    webViewTnc.setWebViewClient(new MyBrowser());
                    webViewTnc.loadUrl("https://sales-app.duatanganindonesia.com/api/performance?achieved="+achieved+"&low="+low+"&middle="+middle+"&high="+high+"");

                    DecimalFormat decim = new DecimalFormat("#,###.##");
                    String formatOmset = decim.format(profile.getPerformance().getAchieved());
                    tvOmset.setText(formatOmset);

                    if (profile.getPerformance().getAchieved() >= profile.getPerformance().getTargetHigh()) {
                        int result = profile.getPerformance().getAchieved() - profile.getPerformance().getTargetHigh();
                        String formatAchieved = decim.format(result);
                        tvOverachieved.setText(formatAchieved);
                    }else {
                        tvOverachieved.setText("0");
                    }

                    webViewVisit.getSettings().setJavaScriptEnabled(true);
                    webViewVisit.setWebViewClient(new MyBrowser());
                    webViewVisit.loadUrl("https://sales-app.duatanganindonesia.com/api/visit_performance?achieved="+achievedVisit+"&target="+target+"");
                }
            }

            @Override
            public void onFailure(Call<GetResponseProfile> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}