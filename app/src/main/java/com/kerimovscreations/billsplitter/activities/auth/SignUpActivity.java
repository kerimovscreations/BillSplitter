package com.kerimovscreations.billsplitter.activities.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.CountryCode;
import com.kerimovscreations.billsplitter.tools.BaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.avatar)
    CircleImageView mAvatar;

    @BindView(R.id.phone_code)
    TextView mPhoneCode;

    @BindView(R.id.name_input)
    TextInputEditText mNameInput;

    @BindView(R.id.phone_input)
    TextInputEditText mPhoneInput;

    @BindView(R.id.password_input)
    TextInputEditText mPasswordInput;

    @BindView(R.id.password_confirm_input)
    TextInputEditText mPasswordConfirmInput;

    Uri mSelectedAvatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_sign_up);
    }

    @Override
    public void initVars() {
        super.initVars();

    }

    /**
     * Click handlers
     */

    @OnClick(R.id.avatar)
    void onAvatar(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());
    }

    @OnClick(R.id.btn)
    void onSignUp(View view) {
        // TODO: API integration
        //File uploadPhoto = new File(mPhotoResultUri.getPath());
    }

    @OnClick(R.id.sign_in_text)
    void onSignIn(View view) {
        finish();
    }

    @OnClick(R.id.phone_code_layout)
    void onPhoneCode(View view) {
        promptPhoneCodeDialog();
    }

    /**
     * UI
     */

    void promptPhoneCodeDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.ic_language_select);
        builderSingle.setTitle(getString(R.string.select_country));

        final ArrayAdapter<CountryCode> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item);
        arrayAdapter.addAll(Objects.requireNonNull(CountryCode.loadArrayFromAsset(getContext(), "countryCodes.json")));

        builderSingle.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, position) -> {
            CountryCode code = arrayAdapter.getItem(position);

            if (code != null)
                mPhoneCode.setText(String.format("+%s", code.getDialCode()));
        });
        builderSingle.show();
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
        }
    }
}
