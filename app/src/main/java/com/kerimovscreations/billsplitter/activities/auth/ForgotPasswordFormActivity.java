package com.kerimovscreations.billsplitter.activities.auth;

import android.os.Bundle;
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

    @BindView(R.id.phone_code)
    TextView mPhoneCode;

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
}
