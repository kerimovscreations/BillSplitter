package com.kerimovscreations.billsplitter.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kerimovscreations.billsplitter.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeleteItemBottomSheetDialogFragment extends BottomSheetDialogFragment {

    View mView;

    private OnClickListener mListener;

    public interface OnClickListener {
        void onDelete();
    }

    public void setClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public static DeleteItemBottomSheetDialogFragment getInstance() {
        return new DeleteItemBottomSheetDialogFragment();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_dialog_delete_item, container, false);
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

    @OnClick(R.id.delete_layout)
    void onDelete() {
        if(mListener != null){
            mListener.onDelete();
            dismiss();
        }
    }

    @OnClick(R.id.cancel_btn)
    void onCancel() {
        dismiss();
    }
}
