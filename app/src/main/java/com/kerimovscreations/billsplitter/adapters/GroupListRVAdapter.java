package com.kerimovscreations.billsplitter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.Group;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupListRVAdapter extends RecyclerView.Adapter<GroupListRVAdapter.ViewHolder> {

    private OnItemClickListener mListener;
    private ArrayList<Group> mList;
    private Context mContext;
    private int mSelectedIndex = 0;

    public GroupListRVAdapter(Context context, ArrayList<Group> list) {
        mList = list;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    private int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setOnItemClickListener(GroupListRVAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public GroupListRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.list_item_group, parent, false);

        return new GroupListRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListRVAdapter.ViewHolder viewHolder, int position) {
        Group bItem = mList.get(position);

        viewHolder.title.setText(bItem.getTitle());
        viewHolder.peopleCount.setText(String.valueOf(bItem.getGroupUsers().size()));

        if (getSelectedIndex() == position) {
            viewHolder.layout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.bg_selected_group, null));
            viewHolder.title.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            viewHolder.peopleIc.setColorFilter(getContext().getResources().getColor(R.color.colorPrimary));
            viewHolder.peopleCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            viewHolder.layout.setBackground(null);
            viewHolder.title.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkText));
            viewHolder.peopleIc.setColorFilter(getContext().getResources().getColor(R.color.colorDarkText));
            viewHolder.peopleCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkText));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.people_count)
        TextView peopleCount;
        @BindView(R.id.people_ic)
        ImageView peopleIc;
        @BindView(R.id.layout)
        View layout;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layout.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                        mSelectedIndex = position;
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}