package com.example.salesapp.api;

import com.example.salesapp.model.AccessToken;
import com.example.salesapp.model.GetResponseBook;
import com.example.salesapp.model.GetResponseHistoryTransactions;
import com.example.salesapp.model.GetResponseHistoryTransactionsList;
import com.example.salesapp.model.GetResponseNotification;
import com.example.salesapp.model.GetResponseProduct;
import com.example.salesapp.model.GetResponseProfile;
import com.example.salesapp.model.GetResponseToken;
import com.example.salesapp.model.GetResponseTransaction;
import com.example.salesapp.model.GetResponseVisit;
import com.example.salesapp.model.Page;
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
import retrofit2.http.Query;

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

    @Multipart
    @POST("visits")
    Call<RequestBody> uploadVisit(
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("phone") RequestBody phone,
            @Part("product") RequestBody product,
            @Part("result") RequestBody result,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part body
    );

    @FormUrlEncoded
    @POST("prama_transaction")
    Call<GetResponseTransaction> transaction(
            @Field("customer_name") String name,
            @Field("phone") String phone,
            @Field("address") String address,
            @Field("price") String price,
            @Field("origin") String origin,
            @Field("destination") String destination,
            @Field("noted") String note,
            @Field("status") String status
    );

    @GET("prama_transaction")
    Call<GetResponseTransaction> getTransaction();

    @GET("visits")
    Call<Page> getVisit(
            @Query("page") int page
    );

    @GET("visits")
    Call<Page> getVisitPerformance();

    @GET("books")
    Call<GetResponseBook> getBook();

}

