package com.kerimovscreations.billsplitter.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.Transaction;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BalanceBottomSheedDialogFragment extends BottomSheetDialogFragment {

    View mView;

    @BindView(R.id.initial)
    TextView mInitialValue;

    @BindView(R.id.input)
    EditText mInput;

    @BindView(R.id.input_currency)
    TextView mInputCurrency;

    @BindView(R.id.result)
    TextView mResult;

    private BalanceBottomSheedDialogFragment.OnClickListener mListener;

    private Transaction mTransaction;

    public interface OnClickListener {
        void onSubmit(Float price);
    }

    public void setClickListener(BalanceBottomSheedDialogFragment.OnClickListener listener) {
        mListener = listener;
    }

    public static BalanceBottomSheedDialogFragment getInstance(Transaction transaction) {
        BalanceBottomSheedDialogFragment fragment = new BalanceBottomSheedDialogFragment();
        fragment.mTransaction = transaction;
        return fragment;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_dialog_balance, container, false);
        ButterKnife.bind(this, mView);

        initVars();

        return mView;
    }

    void initVars() {
        mInitialValue.setText(String.format(Locale.getDefault(), "%.2f %s", mTransaction.getBalance(), mTransaction.getCurrency().getName()));
        mInputCurrency.setText(mTransaction.getCurrency().getName());

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if (text.length() > 0) {
                    mResult.setText(String.format(Locale.getDefault(), "%.2f %s", mTransaction.getBalance() - Float.valueOf((String) text), mTransaction.getCurrency().getName()));
                } else {
                    mResult.setText(String.format(Locale.getDefault(), "%.2f %s", mTransaction.getBalance(), mTransaction.getCurrency().getName()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
            mListener.onSubmit(Float.valueOf(Objects.requireNonNull(mInput.getText()).toString().trim()));
            dismiss();
        }
    }

    @OnClick(R.id.cancel_btn)
    void onCancel() {
        dismiss();
    }
}
