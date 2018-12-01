package com.kerimovscreations.billsplitter.adapters.spinner;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.Category;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private View rowView;
    private View mView;
    private List<Category> mList;
    private Activity mContext;

    public CategorySpinnerAdapter(Activity context, int layoutId, int textViewId, List<Category> list) {
        super(context, layoutId, textViewId, list);

        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Category rowItem = mList.get(position);

        mView = mContext.getLayoutInflater().inflate(R.layout.spinner_category_text, null, true);

        TextView txtTitle = mView.findViewById(R.id.title);
        txtTitle.setText(rowItem.getTitle());

        CardView colorIc = mView.findViewById(R.id.color_ic);
        colorIc.setCardBackgroundColor(Color.parseColor(rowItem.getHexColor()));

        return mView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Category rowItem = mList.get(position);

        rowView = mContext.getLayoutInflater().inflate(R.layout.spinner_category_text, null, true);

        TextView txtTitle = rowView.findViewById(R.id.title);
        txtTitle.setText(rowItem.getTitle());
        CardView colorIc = rowView.findViewById(R.id.color_ic);
        colorIc.setCardBackgroundColor(Color.parseColor(rowItem.getHexColor()));

        return rowView;
    }
}
