package com.kerimovscreations.billsplitter.interfaces;

import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppApiService {

    @Headers({"Accept: application/json"})
    @POST("register")
    @Multipart
    Call<SimpleDataWrapper> register(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> params);

    @Headers({"Accept: application/json"})
    @POST("login")
    @FormUrlEncoded
    Call<SimpleDataWrapper> login(@FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @POST("social_login")
    @FormUrlEncoded
    Call<SimpleDataWrapper> socialLogin(@FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @GET("logout")
    Call<SimpleDataWrapper> logout(@Header("Authorization") String api_token);

    @Headers({"Accept: application/json"})
    @GET("business_categories")
    Call<SimpleDataWrapper> getCategories(@Header("Authorization") String api_token, @Query("language") String language);

    @Headers({"Accept: application/json"})
    @GET("branches")
    Call<SimpleDataWrapper> getBranches(
            @Header("Authorization") String api_token,
            @Query("business_category_id") String categoryID,
            @Query("language") String language,
            @Query("page") int page,
            @Query("q") String q,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude
    );

    @Headers({"Accept: application/json"})
    @GET("branches/{branch_id}/reviews")
    Call<SimpleDataWrapper> getBranchReviews(@Header("Authorization") String api_token, @Path("branch_id") String branchID, @Query("page") int page);

    @Headers({"Accept: application/json"})
    @POST("branches/{branch_id}/reviews")
    @FormUrlEncoded
    Call<SimpleDataWrapper> writeReview(@Header("Authorization") String api_token, @FieldMap HashMap<String, String> params, @Path("branch_id") String branchID);

    @Headers({"Accept: application/json"})
    @POST("me/update")
    @Multipart
    Call<SimpleDataWrapper> updateUserData(@Header("Authorization") String api_token, @Part MultipartBody.Part file, @PartMap HashMap<String, RequestBody> params);

    @Headers({"Accept: application/json"})
    @GET("notifications")
    Call<SimpleDataWrapper> getNotifications(
            @Header("Authorization") String api_token,
            @Query("page") int page,
            @Query("language") String language);

    @Headers({"Accept: application/json"})
    @POST("tracking")
    @FormUrlEncoded
    Call<SimpleDataWrapper> tracking(@Header("Authorization") String api_token, @FieldMap HashMap<String, Double> params);
}