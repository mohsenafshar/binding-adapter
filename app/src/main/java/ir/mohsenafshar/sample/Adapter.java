package ir.mohsenafshar.sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.mohsenafshar.listener.ItemClickListener;
import ir.mohsenafshar.listener.ItemLongClickListener;

public class Adapter extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<String> itemList;
    private ItemClickListener clickListener;
    private ItemLongClickListener longClickListener;

    private Adapter(ArrayList<String> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(itemList.get(position), position);

        if (clickListener != null) {
            holder.setClickListener(clickListener);
        }

        if (longClickListener != null) {
            holder.setLongClickListener(longClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static final class Builder {

        private ArrayList<String> itemList;
        private ItemClickListener clickListener;
        private ItemLongClickListener longClickListener;

        public Builder(ArrayList<String> itemList) {
            this.itemList = itemList;
        }

        public Builder setItemClickListener(ItemClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public Builder setItemLongClickListener(ItemLongClickListener longClickListener) {
            this.longClickListener = longClickListener;
            return this;
        }

        public Adapter build() {
            Adapter adapter = new Adapter(itemList);
            adapter.clickListener = this.clickListener;
            adapter.longClickListener = this.longClickListener;
            return adapter;
        }
    }
}
