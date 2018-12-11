package com.kerimovscreations.billsplitter.adapters.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.models.Transaction;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionListRVAdapter extends RecyclerView.Adapter<TransactionListRVAdapter.ViewHolder> {

    private TransactionListRVAdapter.OnItemClickListener mListener;
    private List<Transaction> mList;
    private Context mContext;
    private int mSelectedIndex = 0;

    public TransactionListRVAdapter(Context context, List<Transaction> list) {
        mList = list;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setOnItemClickListener(TransactionListRVAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public TransactionListRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.list_item_transaction, parent, false);

        return new TransactionListRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListRVAdapter.ViewHolder viewHolder, int position) {
        Transaction bItem = mList.get(position);

        viewHolder.name.setText(bItem.getFrom().getFullName());
        viewHolder.email.setText(bItem.getFrom().getEmail());
        Picasso.get().load(bItem.getFrom().getPicture()).into(viewHolder.avatar);
        viewHolder.amount.setText(String.format(Locale.getDefault(), "%.2f %s", bItem.getBalance(), bItem.getCurrency().getName()));

        if(bItem.getBalance() < 0) {
            viewHolder.amount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
            viewHolder.editIc.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            viewHolder.amount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            viewHolder.editIc.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreen), android.graphics.PorterDuff.Mode.SRC_IN);
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
        @BindView(R.id.user_name)
        TextView name;
        @BindView(R.id.user_email)
        TextView email;
        @BindView(R.id.avatar)
        CircleImageView avatar;
        @BindView(R.id.amount_text)
        TextView amount;
        @BindView(R.id.edit_ic)
        ImageView editIc;
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