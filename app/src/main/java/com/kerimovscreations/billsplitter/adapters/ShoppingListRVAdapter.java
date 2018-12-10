package com.kerimovscreations.billsplitter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.models.Timeline;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingListRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 1, TYPE_ITEM = 2;

    private ShoppingListRVAdapter.OnItemClickListener mListener;
    private List<ShoppingItem> mList;
    private Context mContext;

    public ShoppingListRVAdapter(Context context, List<ShoppingItem> list) {
        mList = list;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setOnItemClickListener(ShoppingListRVAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_HEADER:
                View contactView1 = inflater.inflate(R.layout.list_item_shopping_list_header, parent, false);

                return new ShoppingListRVAdapter.ViewHolderHeader(contactView1);
            case TYPE_ITEM:
                View contactView2 = inflater.inflate(R.layout.list_item_shopping_list, parent, false);

                return new ShoppingListRVAdapter.ViewHolderItem(contactView2);
            default:
                View contactView3 = inflater.inflate(R.layout.list_item_shopping_list, parent, false);

                return new ShoppingListRVAdapter.ViewHolderItem(contactView3);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ShoppingItem bItem = mList.get(position);

        switch (viewHolder.getItemViewType()) {
            case TYPE_HEADER:
                ViewHolderHeader viewHolder1 = (ViewHolderHeader) viewHolder;
                viewHolder1.title.setText(bItem.getDate());
                break;
            case TYPE_ITEM:
                ViewHolderItem viewHolder2 = (ViewHolderItem) viewHolder;
                viewHolder2.title.setText(bItem.getProduct().getName());

                if (bItem.isComplete()) {
                    viewHolder2.title.setPaintFlags(viewHolder2.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    viewHolder2.title.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                }

                viewHolder2.checkbox.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(),
                        bItem.isComplete() ? R.drawable.ic_check_on : R.drawable.ic_check_off,
                        null));

                viewHolder2.category.setText(bItem.getProduct().getCategory().getTitle());
                viewHolder2.categoryCard.setCardBackgroundColor(Color.parseColor("#" + bItem.getProduct().getCategory().getHexColor()));

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onCheckClick(int position);
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.category)
        TextView category;
        @BindView(R.id.category_card)
        CardView categoryCard;
        @BindView(R.id.checkbox)
        ImageView checkbox;
        @BindView(R.id.layout)
        View layout;

        ViewHolderItem(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            layout.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });

            checkbox.setOnClickListener(view -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onCheckClick(position);
                        mList.get(position).toggleComplete();
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.layout)
        View layout;

        ViewHolderHeader(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ShoppingItem bItem = mList.get(position);
        return bItem.isHeader() ? TYPE_HEADER : TYPE_ITEM;
    }
}
