package com.example.salesapp.api;

import com.example.salesapp.model.AccessToken;
import com.example.salesapp.model.GetResponseHistoryTransactions;
import com.example.salesapp.model.GetResponseHistoryTransactionsList;
import com.example.salesapp.model.GetResponseNotification;
import com.example.salesapp.model.GetResponseProduct;
import com.example.salesapp.model.GetResponseProfile;
import com.example.salesapp.model.GetResponseToken;
import com.example.salesapp.model.Transaction;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("login")
    @FormUrlEncoded
    Call<GetResponseToken> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("device_token") String device_token
    );

    @POST("forgot")
    @FormUrlEncoded
    Call<GetResponseToken> forgotPassword(
            @Field("email") String email
    );

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken
    );

    @GET("product")
    Call<GetResponseProduct> getProduct();

    @POST("transactions")
    Call<Transaction> booking(
            @Body Transaction detail
    );

    @POST("logout")
    Call<AccessToken> logout();

    @GET("transactions/detail/{id}")
    Call<GetResponseHistoryTransactions> getHistoryTransactions(
            @Path("id") int id
    );

    @GET("transactions")
    Call<GetResponseHistoryTransactionsList> getHistoryTransactionsWithoutDetail();

    @GET("notifications")
    Call<GetResponseNotification> getNotification();

    @GET("profile")
    Call<GetResponseProfile> getProfile();

    @Multipart
    @POST("transactions/{id}")
    Call<RequestBody> uploadPayment(
            @Path("id") int id,
            @Part MultipartBody.Part body,
            @Part("_method") RequestBody method
    );
}

