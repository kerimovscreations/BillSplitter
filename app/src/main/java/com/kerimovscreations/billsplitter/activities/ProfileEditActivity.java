package com.kerimovscreations.billsplitter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.tools.BaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends BaseActivity {

    @BindView(R.id.avatar)
    CircleImageView mAvatar;
    @BindView(R.id.name_input)
    EditText mNameInput;
    @BindView(R.id.email_input)
    EditText mEmailInput;
    @BindView(R.id.current_password)
    EditText mCurrentPasswordInput;
    @BindView(R.id.new_password)
    EditText mNewPasswordInput;
    @BindView(R.id.confirm_new_password)
    EditText mConfirmNewPasswordInput;

    Uri mSelectedAvatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
    }

    @Override
    public void initVars() {
        super.initVars();

    }

    /**
     * Click handlers
     */

    @OnClick(R.id.avatar)
    void onAvatar() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());
    }

    @OnClick(R.id.action_btn)
    void onAction() {
        // TODO: API Integration
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
