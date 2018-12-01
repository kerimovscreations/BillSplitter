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

public class SharedPeopleListRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_EMPTY = 1, TYPE_FULL = 2;

    private SharedPeopleListRVAdapter.OnItemClickListener mListener;
    private List<Person> mList;
    private Context mContext;

    public SharedPeopleListRVAdapter(Context context, List<Person> list) {
        mList = list;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setOnItemClickListener(SharedPeopleListRVAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_EMPTY:
                View contactView1 = inflater.inflate(R.layout.list_item_share_person_empty, parent, false);

                return new SharedPeopleListRVAdapter.ViewHolderEmpty(contactView1);
            case TYPE_FULL:
                View contactView2 = inflater.inflate(R.layout.list_item_share_person_full, parent, false);

                return new SharedPeopleListRVAdapter.ViewHolderFull(contactView2);
            default:
                View contactView3 = inflater.inflate(R.layout.list_item_share_person_empty, parent, false);

                return new SharedPeopleListRVAdapter.ViewHolderEmpty(contactView3);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Person bItem = mList.get(position);

        switch (viewHolder.getItemViewType()) {
            case TYPE_EMPTY:
                SharedPeopleListRVAdapter.ViewHolderEmpty viewHolder1 = (SharedPeopleListRVAdapter.ViewHolderEmpty) viewHolder;
                break;
            case TYPE_FULL:
                SharedPeopleListRVAdapter.ViewHolderFull viewHolder2 = (SharedPeopleListRVAdapter.ViewHolderFull) viewHolder;
                viewHolder2.title.setText(bItem.getFullName());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onSelect(int position);

        void onAdd(int position);
    }

    public class ViewHolderEmpty extends RecyclerView.ViewHolder {
        @BindView(R.id.input)
        TextView input;
        @BindView(R.id.layout)
        View layout;

        ViewHolderEmpty(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layout.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onAdd(position);
                    }
                }
            });
        }
    }

    public class ViewHolderFull extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.layout)
        View layout;

        ViewHolderFull(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layout.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onSelect(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getId() == -1 ? TYPE_EMPTY : TYPE_FULL;
    }
}
