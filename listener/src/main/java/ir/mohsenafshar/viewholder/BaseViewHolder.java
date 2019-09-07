package ir.mohsenafshar.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ir.mohsenafshar.listener.ItemClickListener;
import ir.mohsenafshar.listener.ItemLongClickListener;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder  {

    protected ItemClickListener itemClickListener;
    protected ItemLongClickListener itemLongClickListener;
    protected View itemView;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public abstract void bind(String item, int position);

    public void setClickListener(ItemClickListener clickListener) {
        itemClickListener = clickListener;
    }

    public void setLongClickListener(ItemLongClickListener longClickListener) {
        itemLongClickListener = longClickListener;
    }
}
