package com.kerimovscreations.billsplitter.activities.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgotPasswordConfirmActivity extends BaseActivity {

    public static final String INTENT_REFERENCE_CODE = "REFERENCE_CODE";

    @BindView(R.id.reference_code_input)
    TextInputEditText mReferenceCodeInput;

    @BindView(R.id.password_input)
    TextInputEditText mPasswordInput;

    @BindView(R.id.password_confirm_input)
    TextInputEditText mPasswordConfirmInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_forgot_password_confirm);
    }

    @Override
    public void initVars() {
        super.initVars();

        String referenceCode = getIntent().getStringExtra(INTENT_REFERENCE_CODE);
        mReferenceCodeInput.setText(referenceCode);
    }

    /**
     * Click handlers
     */
    @OnClick(R.id.submit_btn)
    void onSubmit(View view) {

    }
}
