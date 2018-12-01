package com.kerimovscreations.billsplitter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMembersRVAdapter extends RecyclerView.Adapter<GroupMembersRVAdapter.ViewHolder> {

    private GroupMembersRVAdapter.OnItemClickListener mListener;
    private List<Person> mList;
    private Context mContext;
    private int mSelectedIndex = 0;

    public GroupMembersRVAdapter(Context context, List<Person> list) {
        mList = list;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setOnItemClickListener(GroupMembersRVAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public GroupMembersRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.list_item_group_member, parent, false);

        return new GroupMembersRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMembersRVAdapter.ViewHolder viewHolder, int position) {
        Person bItem = mList.get(position);

        viewHolder.name.setText(bItem.getFullName());
        viewHolder.email.setText(bItem.getEmail());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_name)
        TextView name;
        @BindView(R.id.user_email)
        TextView email;
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