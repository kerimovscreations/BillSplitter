package com.kerimovscreations.billsplitter.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kerimovscreations.billsplitter.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteMemberBottomSheetDialogFragment extends BottomSheetDialogFragment {

    View mView;

    @BindView(R.id.email_input)
    TextInputEditText mEmailInput;

    boolean mLogoutBtnVisible = false;

    private OnClickListener mListener;

    public interface OnClickListener {
        void onSend(String email);
    }

    public void setClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public static InviteMemberBottomSheetDialogFragment getInstance() {
        return new InviteMemberBottomSheetDialogFragment();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_dialog_invite_member, container, false);
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
        if (mListener != null && !Objects.requireNonNull(mEmailInput.getText()).toString().isEmpty()) {
            mListener.onSend(Objects.requireNonNull(mEmailInput.getText()).toString().trim());
            dismiss();
        }
    }

    @OnClick(R.id.cancel_btn)
    void onCancel() {
        dismiss();
    }
}
