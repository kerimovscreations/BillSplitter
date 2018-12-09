package com.kerimovscreations.billsplitter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.LocalProfile;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.utils.CommonMethods;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.UserDataWrapper;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends BaseActivity {

    private final String TAG = "PROFILE_FORM";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.layout_progress)
    View mProgressLayout;
    @BindView(R.id.avatar)
    CircleImageView mAvatar;
    @BindView(R.id.name_input)
    EditText mNameInput;
    @BindView(R.id.email_input)
    EditText mEmailInput;
    @BindView(R.id.current_password_input)
    EditText mCurrentPasswordInput;
    @BindView(R.id.new_password_input)
    EditText mNewPasswordInput;
    @BindView(R.id.confirm_password_input)
    EditText mConfirmNewPasswordInput;
    @BindView(R.id.password_change_ic)
    ImageView mPasswordChangeIc;
    @BindView(R.id.password_change_inputs)
    View mPasswordChangeInputsLayout;

    Uri mSelectedAvatarUri;
    boolean mIsPasswordChangeLayoutVisible = false;

    LocalProfile mLocalProfile;

    private Call<UserDataWrapper> mUpdateCall;

    AppApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_profile_edit);
    }

    @Override
    public void onBackPressed() {
        if (mUpdateCall != null && !mUpdateCall.isExecuted()) {
            showProgress(false);
            mUpdateCall.cancel();
            mUpdateCall = null;
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mUpdateCall != null && !mUpdateCall.isExecuted())
            mUpdateCall.cancel();
    }

    @Override
    public void initVars() {
        super.initVars();

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);

        updatePasswordChangeInputsVisibility();

        setupData();
    }

    void setupData() {
        mLocalProfile = GlobalApplication.getRealm().where(LocalProfile.class).findFirst();

        Picasso.get().load(mLocalProfile.getPicture())
                .into(mAvatar);

        mNameInput.setText(mLocalProfile.getFullName());
        mEmailInput.setText(mLocalProfile.getEmail());
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.back_ic)
    void onBack() {
        finish();
    }

    @OnClick(R.id.avatar_layout)
    void onAvatar() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());
    }

    @OnClick(R.id.action_btn)
    void onAction() {
        if (mIsPasswordChangeLayoutVisible && !isPasswordsValid()) {
            return;
        }

        register();
    }

    @OnClick(R.id.password_change_layout)
    void onPasswordChange() {
        mIsPasswordChangeLayoutVisible = !mIsPasswordChangeLayoutVisible;
        updatePasswordChangeInputsVisibility();
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

    void updatePasswordChangeInputsVisibility() {
        mPasswordChangeInputsLayout.setVisibility(mIsPasswordChangeLayoutVisible ? View.VISIBLE : View.GONE);
        mPasswordChangeIc.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                mIsPasswordChangeLayoutVisible ? R.drawable.ic_drop_up : R.drawable.ic_drop_down,
                null));
    }

    /**
     * Actions
     */

    private boolean isPasswordsValid() {
        if (mCurrentPasswordInput.getText().length() == 0 &&
                mNewPasswordInput.getText().length() == 0 &&
                mConfirmNewPasswordInput.getText().length() == 0) {
            return true;
        }

        if (!CommonMethods.isValidPasswordLength(mCurrentPasswordInput.getText().toString()) ||
                !CommonMethods.isValidPasswordLength(mNewPasswordInput.getText().toString()) ||
                !CommonMethods.isValidPasswordLength(mConfirmNewPasswordInput.getText().toString())) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_password_length, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        if (!CommonMethods.isMatchingPasswords(mNewPasswordInput.getText().toString(), mConfirmNewPasswordInput.getText().toString())) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_passwords_not_match, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        return true;
    }

    /**
     * HTTP
     */

    private void register() {
        showProgress(true);

        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), mNameInput.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), mEmailInput.getText().toString().trim());

        HashMap<String, RequestBody> data = new HashMap<>();

        data.put("FullName", fullName);
        data.put("email", email);

        if (mIsPasswordChangeLayoutVisible) {
            RequestBody oldPassword = RequestBody.create(MediaType.parse("text/plain"), mCurrentPasswordInput.getText().toString().trim());
            RequestBody password = RequestBody.create(MediaType.parse("text/plain"), mNewPasswordInput.getText().toString().trim());

            data.put("oldPassword", oldPassword);
            data.put("password", password);
        }

        MultipartBody.Part fileToUpload = null;

        if (mSelectedAvatarUri != null) {
            RequestBody file = RequestBody.create(MediaType.parse("image/*"), new File(mSelectedAvatarUri.getPath()));
            fileToUpload = MultipartBody.Part.createFormData("photo", mEmailInput.getText().toString(), file);
        }

        mUpdateCall = mApiService.updateUser(Auth.getInstance().getToken(getContext()), fileToUpload, data);

        mUpdateCall.enqueue(new Callback<UserDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserDataWrapper> call, @NonNull Response<UserDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Auth.getInstance().updateProfile(response.body().getPerson());
                        Toast.makeText(getContext(), R.string.successful_update_profile, Toast.LENGTH_SHORT).show();
                        finish();
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

    /**
     * Permission and results
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
        }
    }
}
