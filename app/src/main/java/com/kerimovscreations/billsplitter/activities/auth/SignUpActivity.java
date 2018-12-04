package com.kerimovscreations.billsplitter.activities.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends BaseActivity {

    private final int REQUEST_GOOGLE_SIGN_IN = 12;

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
        // TODO: API integration
        //File uploadPhoto = new File(mPhotoResultUri.getPath());
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

    /**
     * Actions
     */

    void completeLogin(GoogleSignInAccount account) {
        Log.e("GOOGLE", account.getDisplayName());
        Log.e("GOOGLE", account.getEmail());
        Log.e("GOOGLE", account.getId());
        Log.e("GOOGLE", account.getIdToken());

        // TODO: Send to backend
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
}
