package com.kerimovscreations.billsplitter.interfaces;

import com.kerimovscreations.billsplitter.wrappers.CategoryListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.CurrencyListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.GroupListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.StatisticsDataWrapper;
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
    @GET("currency")
    Call<CurrencyListDataWrapper> getCurrencies(@Header("Authorization") String token, @Query("q") String search, @Query("page") int pageNumber);

    @Headers({"Accept: application/json"})
    @POST("group")
    @FormUrlEncoded
    Call<SimpleDataWrapper> createGroup(@Header("Authorization") String token, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @PUT("group/{group_id}")
    @FormUrlEncoded
    Call<SimpleDataWrapper> updateGroup(@Header("Authorization") String token, @Path("group_id") int groupId, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @GET("group")
    Call<GroupListDataWrapper> getGroups(@Header("Authorization") String token, @Query("q") String search, @Query("page") int pageNumber);

    @Headers({"Accept: application/json"})
    @DELETE("group/{group_id}")
    Call<SimpleDataWrapper> deleteGroup(@Header("Authorization") String token, @Path("group_id") int groupId);

    @Headers({"Accept: application/json"})
    @GET("purchase/{group_id}")
    Call<ShoppingItemListDataWrapper> getShoppingItems(@Header("Authorization") String token, @Path("group_id") int groupId, @Query("page") int pageNumber);

    @Headers({"Accept: application/json"})
    @POST("product")
    @FormUrlEncoded
    Call<SimpleDataWrapper> createPurchase(@Header("Authorization") String token, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @PUT("product/{item_id}")
    @FormUrlEncoded
    Call<SimpleDataWrapper> updateShoppingItem(@Header("Authorization") String token, @Path("item_id") int itemId, @FieldMap HashMap<String, String> params);

    @Headers({"Accept: application/json"})
    @GET("product")
    Call<ShoppingItemDataWrapper> searchProduct(@Header("Authorization") String token, @Query("groupId") int groupId, @Query("barCode") String barCode);

    @Headers({"Accept: application/json"})
    @GET("product/categories")
    Call<CategoryListDataWrapper> getCategories(@Header("Authorization") String token);

    @Headers({"Accept: application/json"})
    @GET("purchase/{group_id}/statistics")
    Call<StatisticsDataWrapper> getStatistics(@Header("Authorization") String token, @Path("group_id") int groupId);

}
