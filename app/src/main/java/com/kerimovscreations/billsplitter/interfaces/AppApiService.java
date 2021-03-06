package com.kerimovscreations.billsplitter.interfaces;

import com.kerimovscreations.billsplitter.wrappers.CategoryListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.CurrencyListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.GroupDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.GroupListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.StatisticsDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.TransactionsBundleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.UserDataWrapper;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppApiService {

    @Headers({"Accept: application/json"})
    @POST("user/register")
    @Multipart
    Call<UserDataWrapper> register(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> params);

    @Headers({"Accept: application/json"})
    @POST("user/forgot_password")
    @FormUrlEncoded
    Call<SimpleDataWrapper> forgotPassword(@Field("email") String token);

    @Headers({"Accept: application/json"})
    @POST("user/reset_password")
    @FormUrlEncoded
    Call<SimpleDataWrapper> resetPassword(@Field("code") String code, @Field("password") String password);

    @Headers({"Accept: application/json"})
    @POST("user/login")
    @FormUrlEncoded
    Call<UserDataWrapper> login(@FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @GET("user/me")
    Call<UserDataWrapper> getProfile(@Header("Authorization") String token);

    @Headers({"Accept: application/json"})
    @PUT("user/me")
    @Multipart
    Call<UserDataWrapper> updateUser(@Header("Authorization") String token, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> params);

    @Headers({"Accept: application/json"})
    @POST("user/google")
    @FormUrlEncoded
    Call<UserDataWrapper> googleRegister(@Field("token") String token);

    @Headers({"Accept: application/json"})
    @POST("user/facebook")
    @FormUrlEncoded
    Call<UserDataWrapper> facebookRegister(@Field("token") String token, @Field("email") String email);

    @Headers({"Accept: application/json"})
    @GET("currency")
    Call<CurrencyListDataWrapper> getCurrencies(@Header("Authorization") String token, @Query("q") String search, @Query("page") int pageNumber);

    @Headers({"Accept: application/json"})
    @POST("group")
    @FormUrlEncoded
    Call<GroupDataWrapper> createGroup(@Header("Authorization") String token, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @PUT("group/{group_id}")
    @FormUrlEncoded
    Call<GroupDataWrapper> updateGroup(@Header("Authorization") String token, @Path("group_id") int groupId, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @GET("group")
    Call<GroupListDataWrapper> getGroups(@Header("Authorization") String token, @Query("q") String search, @Query("page") int pageNumber);

    @Headers({"Accept: application/json"})
    @GET("transaction/{group_id}/money")
    Call<TransactionsBundleDataWrapper> getTransactions(@Header("Authorization") String token, @Path("group_id") int groupId);

    @Headers({"Accept: application/json"})
    @POST("transaction")
    @FormUrlEncoded
    Call<SimpleDataWrapper> addTransaction(@Header("Authorization") String token,
                                           @Field("payerId") int payerId,
                                           @Field("receiverId") int receiverId,
                                           @Field("amount") float amount,
                                           @Field("groupId") int groupId);

    @Headers({"Accept: application/json"})
    @DELETE("group/{group_id}")
    Call<SimpleDataWrapper> deleteGroup(@Header("Authorization") String token, @Path("group_id") int groupId);

    @Headers({"Accept: application/json"})
    @GET("purchase/{group_id}")
    Call<ShoppingItemListDataWrapper> getShoppingItems(@Header("Authorization") String token, @Path("group_id") int groupId, @Query("page") int pageNumber);

    @Headers({"Accept: application/json"})
    @POST("product")
    @FormUrlEncoded
    Call<ShoppingItemDataWrapper> createShoppingItem(@Header("Authorization") String token, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @POST("purchase/{group_id}/hide_completed")
    Call<SimpleDataWrapper> hideCompletedShoppingItems(@Header("Authorization") String token, @Path("group_id") int groupId);

    @Headers({"Accept: application/json"})
    @PUT("product/{item_id}")
    @FormUrlEncoded
    Call<ShoppingItemDataWrapper> updateShoppingItem(@Header("Authorization") String token, @Path("item_id") int itemId, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @GET("product")
    Call<ShoppingItemDataWrapper> searchProduct(@Header("Authorization") String token, @Query("groupId") int groupId, @Query("barCode") String barCode);

    @Headers({"Accept: application/json"})
    @GET("product/categories")
    Call<CategoryListDataWrapper> getCategories(@Header("Authorization") String token);

    @Headers({"Accept: application/json"})
    @GET("purchase/{group_id}/statistics")
    Call<StatisticsDataWrapper> getStatistics(@Header("Authorization") String token, @Path("group_id") int groupId, @Query("start") String startDate, @Query("end") String endDate);

}
