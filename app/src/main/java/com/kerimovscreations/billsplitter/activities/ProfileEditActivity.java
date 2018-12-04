package com.kerimovscreations.billsplitter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.tools.BaseActivity;
import com.kerimovscreations.billsplitter.tools.CommonMethods;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends BaseActivity {

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_profile_edit);
    }

    @Override
    public void initVars() {
        super.initVars();

        updatePasswordChangeInputsVisibility();
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.back_ic)
    void onBack() {
        finish();
    }

    @OnClick(R.id.avatar)
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

        // TODO: API Integration
    }

    @OnClick(R.id.password_change_layout)
    void onPasswordChange() {
        mIsPasswordChangeLayoutVisible = !mIsPasswordChangeLayoutVisible;
        updatePasswordChangeInputsVisibility();
    }

    /**
     * UI
     */

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
