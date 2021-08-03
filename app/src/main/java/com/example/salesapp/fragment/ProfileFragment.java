package com.example.salesapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.salesapp.R;
import com.example.salesapp.activity.GuideActivity;
import com.example.salesapp.activity.LoginActivity;
import com.example.salesapp.activity.PrivacyActivity;
import com.example.salesapp.api.ApiService;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.api.TokenManager;
import com.example.salesapp.model.GetResponseProduct;
import com.example.salesapp.model.GetResponseProfile;
import com.example.salesapp.model.Profile;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private CircleImageView circleImageViewAvatar;
    private TextView tvName, tvCompany, tvDivision;
    private RelativeLayout relativeLayoutGuide, relativeLayoutPrivacy, relativeLayoutLogout;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<GetResponseProfile> callProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        circleImageViewAvatar = view.findViewById(R.id.image_view_photo);
        tvName = view.findViewById(R.id.text_view_name);
        tvCompany = view.findViewById(R.id.text_view_company);
        tvDivision = view.findViewById(R.id.text_view_division);
        relativeLayoutGuide = view.findViewById(R.id.relative_layout_guide);
        relativeLayoutPrivacy = view.findViewById(R.id.relative_layout_privacy);
        relativeLayoutLogout = view.findViewById(R.id.relative_layout_logout);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        relativeLayoutGuide.setOnClickListener(v -> startActivity(new Intent(getContext(), GuideActivity.class)));
        relativeLayoutPrivacy.setOnClickListener(v -> startActivity(new Intent(getContext(), PrivacyActivity.class)));
        relativeLayoutLogout.setOnClickListener(v -> {
            TokenManager tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
            tokenManager.deleteToken();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        getDataProfile();

        return view;
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
                            .load(RetrofitBuilder.BASE_URL_IMAGES + profile.getAvatar())
                            .into(circleImageViewAvatar);
                    tvName.setText(profile.getName());
                    tvCompany.setText(profile.getCompanyName());
                    tvDivision.setText(profile.getDevisionName());
                }
            }

            @Override
            public void onFailure(Call<GetResponseProfile> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}