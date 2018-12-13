package com.kerimovscreations.billsplitter.adapters.recyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.Timeline;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineRVAdapter extends RecyclerView.Adapter<TimelineRVAdapter.ViewHolder> {

    private OnItemClickListener mListener;
    private List<Timeline> mList;
    private Context mContext;
    private int mSelectedIndex = 0;

    public TimelineRVAdapter(Context context, List<Timeline> list) {
        mList = list;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.list_item_timeline, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Timeline bItem = mList.get(position);

        viewHolder.title.setText(bItem.getName());

        if (position == mSelectedIndex) {
            viewHolder.title.setAlpha(1.0f);
            viewHolder.title.setTypeface(null, Typeface.BOLD);
        } else {
            viewHolder.title.setAlpha(0.5f);
            viewHolder.title.setTypeface(null, Typeface.NORMAL);
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
        @BindView(R.id.layout)
        View layout;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layout.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mSelectedIndex = position;
                        mListener.onItemClick(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
