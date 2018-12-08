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
import android.widget.Toast;

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
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.utils.CommonMethods;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.UserDataWrapper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_sign_up);
    }

    @Override
    public void initVars() {
        super.initVars();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);
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

    @OnClick(R.id.google_btn)
    void onGoogleLogin(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
    }

    /**
     * UI
     */

    void showProgress(boolean show) {
        // TODO: Implement UI
    }

    private boolean isPasswordsValid() {
        if (!CommonMethods.isValidPasswordLength(mPasswordInput.getText().toString()) ||
                !CommonMethods.isValidPasswordLength(mPasswordConfirmInput.getText().toString())) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_password_length, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        if (!CommonMethods.isMatchingPasswords(mPasswordInput.getText().toString(), mPasswordConfirmInput.getText().toString())) {
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

    void completeLogin(GoogleSignInAccount account) {
        Log.e("GOOGLE", account.getDisplayName());
        Log.e("GOOGLE", account.getEmail());
        Log.e("GOOGLE", account.getId());
        Log.e("GOOGLE", account.getIdToken());

        googleLogin(account.getIdToken());
    }

    /**
     * HTTP
     */

    private void register() {
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

        Call<UserDataWrapper> call = mApiService.register(fileToUpload, data);

        call.enqueue(new Callback<UserDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserDataWrapper> call, @NonNull Response<UserDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().saveToken(getContext(), response.body().getPerson().getApiToken());
                        Toast.makeText(getContext(), R.string.successful_register, Toast.LENGTH_SHORT).show();
                        toMain();
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
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                });

                t.printStackTrace();

                if (!call.isCanceled()) {
                    Log.e(TAG, "onFailure: Request Failed");
                }
            }
        });
    }

    private void googleLogin(String token) {
        Call<UserDataWrapper> call = mApiService.googleRegister(token);

        call.enqueue(new retrofit2.Callback<UserDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserDataWrapper> call, @NonNull Response<UserDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().saveToken(getContext(), response.body().getPerson().getApiToken());
                        Toast.makeText(getContext(), R.string.successful_register, Toast.LENGTH_SHORT).show();
                        toMain();
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
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                });

                t.printStackTrace();

                if (!call.isCanceled()) {
                    Log.e(TAG, "onFailure: Request Failed");
                }
            }
        });
    }

    /**
     * Results
     */

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
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
        finish();
        startActivity(new Intent(getContext(), MainActivity.class));
    }
}
