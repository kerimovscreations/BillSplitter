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

public class PricePickerBottomSheetDialogFragment extends BottomSheetDialogFragment {

    View mView;

    @BindView(R.id.input)
    TextInputEditText mPrice;
    
    private OnClickListener mListener;

    public interface OnClickListener {
        void onSubmit(Float price);
    }

    public void setClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public static PricePickerBottomSheetDialogFragment getInstance() {
        return new PricePickerBottomSheetDialogFragment();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_dialog_price_picker, container, false);
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

    @OnClick(R.id.submit_btn)
    void onSend() {
        if (mListener != null) {
            mListener.onSubmit(Float.valueOf(Objects.requireNonNull(mPrice.getText()).toString().trim()));
            dismiss();
        }
    }

    @OnClick(R.id.cancel_btn)
    void onCancel() {
        dismiss();
    }
}
