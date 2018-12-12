package com.kerimovscreations.billsplitter.activities.auth;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kerimovscreations.billsplitter.activities.MainActivity;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.FacebookEmailPickerBottomSheet;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.CategoryListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.CurrencyListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.UserDataWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private final String TAG = "LOGIN";
    private static final String EMAIL = "email";

    private final int REQUEST_GOOGLE_SIGN_IN = 2;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.layout_progress)
    View mProgressLayout;

    @BindView(R.id.email_input)
    TextInputEditText mEmailInput;

    @BindView(R.id.password_input)
    TextInputEditText mPasswordInput;

    GoogleSignInClient mGoogleSignInClient;
    AppApiService mApiService;

    Call<UserDataWrapper> mLoginCall;
    Call<UserDataWrapper> mGoogleLoginCall;
    Call<UserDataWrapper> mFacebookLoginCall;

    ArrayList<Call> mCalls = new ArrayList<>();

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_login);
    }

    @Override
    public void initVars() {
        super.initVars();

        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);

        // TODO: Remove on production
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.kerimovscreations.billsplitter", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    @Override
    public void onBackPressed() {

        for(Call call : mCalls) {
            if (call != null && !call.isExecuted()) {
                showProgress(false);
                call.cancel();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for(Call call : mCalls) {
            if (call != null && !call.isExecuted())
                call.cancel();
        }
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.forgot_pass_text)
    void onForgetPass(View view) {
        toForgetPass();
    }

    @OnClick(R.id.sign_in_btn)
    void onSignIn(View view) {
        if (!isFormInputsValid()) {
            return;
        }

        login();
    }

    @OnClick(R.id.facebook_btn)
    void onFacebookLogin(View view) {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFacebookData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e(TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG, exception.toString());
                    }
                });
    }

    @OnClick(R.id.google_btn)
    void onGoogleLogin(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
    }

    @OnClick(R.id.sign_up_text)
    void onSignUp(View view) {
        toSignUp();
    }

    /**
     * Actions
     */

    private void getFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    // Application code
                    try {
                        Log.i("Response", response.toString());

                        if(response.getJSONObject().has("email")) {
                            String email = response.getJSONObject().getString("email");
                            Log.e(TAG, AccessToken.getCurrentAccessToken().getToken());
                            completeFbLogin("");
                        } else {
                            onEmailPicker();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    void completeLogin(GoogleSignInAccount account) {
        Log.e("GOOGLE", account.getDisplayName());
        Log.e("GOOGLE", account.getEmail());
        Log.e("GOOGLE", account.getId());
        Log.e("GOOGLE", account.getIdToken());

        googleLogin(account.getIdToken());
    }

    void completeFbLogin(String email) {
        fbLogin(email, AccessToken.getCurrentAccessToken().getToken());
    }

    int mCheckBaseDataCount = 0;

    void loadBaseData() {
        mCheckBaseDataCount = 2;

        getCurrencies();
        getCategories();
    }

    void checkLoadingBaseData() {
        mCheckBaseDataCount--;

        if (mCheckBaseDataCount == 0) {
            toMain();
        }
    }

    /**
     * UI
     */

    private void showProgress(boolean show) {
        mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void onEmailPicker() {
        FacebookEmailPickerBottomSheet facebookEmailPickerBottomSheet = FacebookEmailPickerBottomSheet.getInstance();
        facebookEmailPickerBottomSheet.setClickListener(email -> fbLogin(email, AccessToken.getCurrentAccessToken().getToken()));
        facebookEmailPickerBottomSheet.show(getSupportFragmentManager(), "MEMBER_TAG");
    }

    private boolean isFormInputsValid() {
        if (mEmailInput.getText().length() == 0 ||
                mPasswordInput.getText().length() == 0) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_fill_inputs, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        return true;
    }

    /**
     * HTTP
     */

    private void login() {
        showProgress(true);

        HashMap<String, String> data = new HashMap<>();

        data.put("email", mEmailInput.getText().toString().trim());
        data.put("password", mPasswordInput.getText().toString().trim());

        mLoginCall = mApiService.login(data);

        mCalls.add(mLoginCall);

        mLoginCall.enqueue(new Callback<UserDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserDataWrapper> call, @NonNull Response<UserDataWrapper> response) {

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().saveProfile(getContext(), response.body().getPerson(), false);
//                        Toast.makeText(getContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
                        loadBaseData();
                    });
                } else {
                    runOnUiThread(() -> showProgress(false));

                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            Log.d(TAG, errorString);

                            runOnUiThread(() -> Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                    }
                    Log.d(TAG, response.raw().toString());
                    Log.e(TAG, "onResponse: Request Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    Log.e(TAG, "onFailure: Request Failed");

                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void googleLogin(String token) {
        showProgress(true);

        mGoogleLoginCall = mApiService.googleRegister(token);

        mCalls.add(mGoogleLoginCall);

        mGoogleLoginCall.enqueue(new retrofit2.Callback<UserDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserDataWrapper> call, @NonNull Response<UserDataWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().saveProfile(getContext(), response.body().getPerson(), true);
//                        Toast.makeText(getContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
                        loadBaseData();
                    });
                } else {
                    runOnUiThread(() -> showProgress(false));

                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            Log.d(TAG, errorString);

                            runOnUiThread(() -> Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                    }
                    Log.d(TAG, response.raw().toString());
                    Log.e(TAG, "onResponse: Request Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    Log.e(TAG, "onFailure: Request Failed");

                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void fbLogin(String email, String token) {
        showProgress(true);

        mFacebookLoginCall = mApiService.facebookRegister(token, email);

        mCalls.add(mFacebookLoginCall);

        mFacebookLoginCall.enqueue(new retrofit2.Callback<UserDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserDataWrapper> call, @NonNull Response<UserDataWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().saveProfile(getContext(), response.body().getPerson(), true);
//                        Toast.makeText(getContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
                        loadBaseData();
                    });
                } else {
                    runOnUiThread(() -> showProgress(false));

                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            Log.d(TAG, errorString);

                            runOnUiThread(() -> Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                    }
                    Log.d(TAG, response.raw().toString());
                    Log.e(TAG, "onResponse: Request Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    Log.e(TAG, "onFailure: Request Failed");

                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void getCurrencies() {
        Call<CurrencyListDataWrapper> call = mApiService.getCurrencies(Auth.getInstance().getToken(getContext()), "", 1);

        call.enqueue(new Callback<CurrencyListDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<CurrencyListDataWrapper> call, @NonNull Response<CurrencyListDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    getRealm().executeTransactionAsync(realm -> {
                        realm.delete(Currency.class);
                        realm.copyToRealm(response.body().getList());
                    }, () -> checkLoadingBaseData());
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            Log.d(TAG, errorString);

                            runOnUiThread(() -> Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                    }
                    Log.d(TAG, response.raw().toString());
                    Log.e(TAG, "onResponse: Request Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CurrencyListDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getCategories() {
        Call<CategoryListDataWrapper> call = mApiService.getCategories(Auth.getInstance().getToken(getContext()));

        call.enqueue(new Callback<CategoryListDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<CategoryListDataWrapper> call, @NonNull Response<CategoryListDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    getRealm().executeTransactionAsync(realm -> {
                        realm.delete(Category.class);
                        realm.copyToRealm(response.body().getList());
                    }, () -> checkLoadingBaseData());
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            Log.d(TAG, errorString);

                            runOnUiThread(() -> Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                    }
                    Log.d(TAG, response.raw().toString());
                    Log.e(TAG, "onResponse: Request Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryListDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    /**
     * Navigation
     */

    void toMain() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void toSignUp() {
        Intent intent = new Intent(getContext(), SignUpActivity.class);
        startActivity(intent);
    }

    void toForgetPass() {
        Intent intent = new Intent(getContext(), ForgotPasswordFormActivity.class);
        startActivity(intent);
    }

    /**
     * Permission and results
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            completeLogin(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLE", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
