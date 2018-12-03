package com.kerimovscreations.billsplitter.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kerimovscreations.billsplitter.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupEditBottomSheetDialogFragment extends BottomSheetDialogFragment {

    View mView;

    @BindView(R.id.top_pointer_view)
    View mTopPointerView;


    private OnClickListener mListener;

    public interface OnClickListener {
        void onDelete();

        void onEdit();

        void onDeleteItems();
    }

    public void setClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public static GroupEditBottomSheetDialogFragment getInstance() {
        return new GroupEditBottomSheetDialogFragment();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_dialog_group_more, container, false);
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

    @OnClick(R.id.edit_layout)
    void onEdit() {
        if(mListener != null){
            mListener.onEdit();
            dismiss();
        }
    }

    @OnClick(R.id.delete_items_layout)
    void onDeleteItems() {
        if(mListener != null){
            mListener.onDeleteItems();
            dismiss();
        }
    }

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
