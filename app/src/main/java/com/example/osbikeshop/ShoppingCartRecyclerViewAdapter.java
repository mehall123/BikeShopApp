package com.example.osbikeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingCartRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingCartRecyclerViewAdapter.ViewHolder> {

    private List<String> nData;
    private List<Integer> mImage, pData;
    private Context mContext;
    private LayoutInflater mInflatter;
    private ItemClickListener mClickListener;

    ShoppingCartRecyclerViewAdapter(ShoppingCartActivity context, List<String> nData, List<Integer> pData, List<Integer> image_urls) {
        this.mInflatter = LayoutInflater.from(context);
        this.nData = nData;
        this.pData = pData;
        this.mImage = image_urls;
        this.mContext = context;
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflatter.inflate(R.layout.cardview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = nData.get(position);
        int price = pData.get(position);
        Integer image_urls = mImage.get(position);
        holder.myTextViewName.setText(item);
        holder.myTextViiewPrice.setText("€" + price);
        holder.myImageView.setImageResource(image_urls);
    }

    @Override
    public int getItemCount() {
        return nData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myTextViewName, myTextViiewPrice;
        ImageView myImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextViewName = itemView.findViewById(R.id.tvItemName);
            myTextViiewPrice = itemView.findViewById(R.id.tvItemPrice);
            myImageView = itemView.findViewById(R.id.ivItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view,getAdapterPosition());
            }

        }
    }

    public interface ItemClickListener {
        void onItemClick (View view, int position);
    }
}
