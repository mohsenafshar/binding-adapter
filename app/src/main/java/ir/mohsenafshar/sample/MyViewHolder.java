package ir.mohsenafshar.sample;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ir.mohsenafshar.listener.ItemClickListener;
import ir.mohsenafshar.listener.ItemLongClickListener;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(String item, int position) {

    }

    public void setClickListener(ItemClickListener clickListener) {

    }

    public void setLongClickListener(ItemLongClickListener longClickListener) {

    }
}
