package com.kerimovscreations.billsplitter.activities.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.activities.MainActivity;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.FacebookEmailPickerBottomSheet;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.utils.CommonMethods;
import com.kerimovscreations.billsplitter.wrappers.CategoryListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.CurrencyListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.UserDataWrapper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity {

    private final String TAG = "SIGN_UP";

    private final int REQUEST_GOOGLE_SIGN_IN = 12;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.layout_progress)
    View mProgressLayout;

    @BindView(R.id.avatar)
    CircleImageView mAvatar;

    @BindView(R.id.name_input)
    TextInputEditText mNameInput;

    @BindView(R.id.email_input)
    TextInputEditText mEmailInput;

    @BindView(R.id.password_input)
    TextInputEditText mPasswordInput;

    @BindView(R.id.password_confirm_input)
    TextInputEditText mPasswordConfirmInput;

    Uri mSelectedAvatarUri;
    GoogleSignInClient mGoogleSignInClient;
    AppApiService mApiService;

    Call<UserDataWrapper> mRegisterCall;
    Call<UserDataWrapper> mGoogleLoginCall;
    Call<UserDataWrapper> mFacebookLoginCall;

    ArrayList<Call> mCalls = new ArrayList<>();

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_sign_up);
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
    }

    @Override
    public void onBackPressed() {

        for (Call call : mCalls) {
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

        for (Call call : mCalls) {
            if (call != null && !call.isExecuted())
                call.cancel();
        }
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.back_ic)
    void onBack(View view) {
        finish();
    }

    @OnClick(R.id.avatar_layout)
    void onAvatar(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());
    }

    @OnClick(R.id.btn)
    void onSignUp(View view) {
        if (!isFormInputsValid() || !isPasswordsValid()) {
            return;
        }

        register();
    }

    @OnClick(R.id.sign_in_text)
    void onSignIn(View view) {
        finish();
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

    /**
     * UI
     */

    void showProgress(boolean show) {
        mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private boolean isPasswordsValid() {
        if (!CommonMethods.getInstance().isValidPasswordLength(mPasswordInput.getText().toString()) ||
                !CommonMethods.getInstance().isValidPasswordLength(mPasswordConfirmInput.getText().toString())) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_password_length, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        if (!CommonMethods.getInstance().isMatchingPasswords(mPasswordInput.getText().toString(), mPasswordConfirmInput.getText().toString())) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_passwords_not_match, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        return true;
    }

    private boolean isFormInputsValid() {
        if (mNameInput.getText().length() == 0 ||
                mEmailInput.getText().length() == 0 ||
                mPasswordInput.getText().length() == 0 ||
                mPasswordConfirmInput.getText().length() == 0) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_fill_inputs, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        return true;
    }

    /**
     * Actions
     */

    private void getFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    Log.i("Response", response.toString());

                    if (response.getJSONObject().has("email")) {
                        completeFbLogin("");
                    } else {
                        onEmailPicker();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    void completeFbLogin(String email) {
        fbLogin(email, AccessToken.getCurrentAccessToken().getToken());
    }

    void completeLogin(GoogleSignInAccount account) {
        Log.e("GOOGLE", account.getDisplayName());
        Log.e("GOOGLE", account.getEmail());
        Log.e("GOOGLE", account.getId());
        Log.e("GOOGLE", account.getIdToken());

        googleLogin(account.getIdToken());
    }

    private void onEmailPicker() {
        FacebookEmailPickerBottomSheet facebookEmailPickerBottomSheet = FacebookEmailPickerBottomSheet.getInstance();
        facebookEmailPickerBottomSheet.setClickListener(email -> fbLogin(email, AccessToken.getCurrentAccessToken().getToken()));
        facebookEmailPickerBottomSheet.show(getSupportFragmentManager(), "MEMBER_TAG");
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
     * HTTP
     */

    private void register() {
        showProgress(true);

        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), mNameInput.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), mEmailInput.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), mPasswordInput.getText().toString().trim());

        HashMap<String, RequestBody> data = new HashMap<>();

        data.put("FullName", fullName);
        data.put("email", email);
        data.put("password", password);

        MultipartBody.Part fileToUpload = null;

        if (mSelectedAvatarUri != null) {
            RequestBody file = RequestBody.create(MediaType.parse("image/*"), new File(mSelectedAvatarUri.getPath()));
            fileToUpload = MultipartBody.Part.createFormData("photo", mEmailInput.getText().toString(), file);
        }

        mRegisterCall = mApiService.register(fileToUpload, data);

        mCalls.add(mRegisterCall);

        mRegisterCall.enqueue(new Callback<UserDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserDataWrapper> call, @NonNull Response<UserDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().saveProfile(getContext(), response.body().getPerson(), false);
                        Toast.makeText(getContext(), R.string.successful_register, Toast.LENGTH_SHORT).show();
//                        toMain();
                        loadBaseData();
                    });
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
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().saveProfile(getContext(), response.body().getPerson(), true);
                        Toast.makeText(getContext(), R.string.successful_register, Toast.LENGTH_SHORT).show();
//                        toMain();
                        loadBaseData();
                    });
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
     * Results
     */

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mSelectedAvatarUri = result.getUri();

                Bitmap bm;
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mSelectedAvatarUri);
                        mAvatar.setImageBitmap(bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("PHOTO", result.toString());
            }
        } else if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
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

    /**
     * Navigation
     */

    private void toMain() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
