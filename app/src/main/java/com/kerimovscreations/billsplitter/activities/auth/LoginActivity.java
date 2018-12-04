package com.kerimovscreations.billsplitter.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kerimovscreations.billsplitter.activities.MainActivity;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.CountryCode;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private final int REQUEST_GOOGLE_SIGN_IN = 2;

    @BindView(R.id.phone_code)
    TextView mPhoneCode;

    @BindView(R.id.phone_input)
    TextInputEditText mPhoneInput;

    @BindView(R.id.password_input)
    TextInputEditText mPasswordInput;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_login);
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

    @OnClick(R.id.phone_code_layout)
    void onPhoneCode(View view) {
        promptPhoneCodeDialog();
    }

    @OnClick(R.id.forgot_pass_text)
    void onForgetPass(View view) {
        toForgetPass();
    }

    @OnClick(R.id.sign_in_btn)
    void onSignIn(View view) {
        // TODO: Check auth
        toMain();
    }

//    @OnClick(R.id.facebook_btn)
//    void onFacebookLogin(View view) {
//        // TODO: Facebook login
//    }

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

    void completeLogin(GoogleSignInAccount account) {
        Log.e("GOOGLE", account.getDisplayName());
        Log.e("GOOGLE", account.getEmail());
        Log.e("GOOGLE", account.getId());
        Log.e("GOOGLE", account.getIdToken());

        // TODO: Send to backend
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
