package com.kerimovscreations.billsplitter.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.CountryCode;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgotPasswordFormActivity extends BaseActivity {

    @BindView(R.id.email_input)
    TextInputEditText mEmailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_forgot_password_form);
    }

    @Override
    public void initVars() {
        super.initVars();
    }

    @OnClick(R.id.reset_pass_btn)
    void onForgotPass(View view) {
        toForgotPasswordConfirm("TEST_CODE");
    }

    /**
     * UI
     */

    /**
     * Navigation
     */

    void toForgotPasswordConfirm(String code) {
        Intent intent = new Intent(getContext(), ForgotPasswordConfirmActivity.class);
        intent.putExtra(ForgotPasswordConfirmActivity.INTENT_REFERENCE_CODE, code);
        startActivity(intent);
    }
}
