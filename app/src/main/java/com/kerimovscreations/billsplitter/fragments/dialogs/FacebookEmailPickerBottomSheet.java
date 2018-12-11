package com.kerimovscreations.billsplitter.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.utils.CommonMethods;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FacebookEmailPickerBottomSheet extends BottomSheetDialogFragment {

    View mView;

    @BindView(R.id.email_input)
    TextInputEditText mEmailInput;

    boolean mLogoutBtnVisible = false;

    private FacebookEmailPickerBottomSheet.OnClickListener mListener;

    public interface OnClickListener {
        void onSend(String email);
    }

    public void setClickListener(FacebookEmailPickerBottomSheet.OnClickListener listener) {
        mListener = listener;
    }

    public static FacebookEmailPickerBottomSheet getInstance() {
        return new FacebookEmailPickerBottomSheet();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_dialog_facebook_email_picker, container, false);
        ButterKnife.bind(this, mView);

        initVars();

        return mView;
    }

    void initVars() {
    }

    /**
     * UI
     */

    /**
     * Click handlers
     */

    @OnClick(R.id.send_btn_layout)
    void onSend() {
        if(CommonMethods.getInstance().isEmailValid(mEmailInput.getText().toString())){
            if (mListener != null && !Objects.requireNonNull(mEmailInput.getText()).toString().isEmpty()) {
                mListener.onSend(Objects.requireNonNull(mEmailInput.getText()).toString().trim());
                dismiss();
            }
        } else {
            mEmailInput.setError(getString(R.string.not_valid_email));
        }
    }

    @OnClick(R.id.cancel_btn)
    void onCancel() {
        dismiss();
    }
}
