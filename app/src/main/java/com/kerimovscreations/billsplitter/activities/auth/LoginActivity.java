package com.kerimovscreations.billsplitter.activities.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.MainActivity;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.CountryCode;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_phone_code)
    TextView mPhoneCode;

    @BindView(R.id.login_phone_input)
    TextInputEditText mPhoneInput;

    @BindView(R.id.login_password_input)
    TextInputEditText mPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_login);
    }

    @Override
    public void initVars() {
        super.initVars();

    }

    /**
     * Click handlers
     */

    @OnClick(R.id.login_phone_code_layout)
    void onPhoneCode(View view) {
        promptPhoneCodeDialog();
    }

    @OnClick(R.id.login_forgot_pass_text)
    void onForgetPass(View view) {
        toForgetPass();
    }

    @OnClick(R.id.sign_in_btn)
    void onSignIn(View view) {
        // TODO: Check auth
    }

    @OnClick(R.id.login_facebook_btn)
    void onFacebookLogin(View view) {
        // TODO: Facebook login
    }

    @OnClick(R.id.login_google_btn)
    void onGoogleLogin(View view) {
        // TODO: Google login
    }

    @OnClick(R.id.login_sign_up_text)
    void onSignUp(View view) {
        toSignUp();
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
        startActivity(intent);
    }

    void toSignUp() {
        Intent intent = new Intent(getContext(), SignUpActivity.class);
        startActivity(intent);
    }

    void toForgetPass() {
        Intent intent = new Intent(getContext(), ForgetPasswordFormActivity.class);
        startActivity(intent);
    }
}
