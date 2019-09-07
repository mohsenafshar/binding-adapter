package ir.mohsenafshar.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ir.mohsenafshar.adapterannotation.AdapterAnnotation;
import ir.mohsenafshar.adapters.SampleAdapter;
import ir.mohsenafshar.listener.ItemClickListener;
import ir.mohsenafshar.listener.ItemLongClickListener;

@AdapterAnnotation(
//        adapterClassName = "MyAdapter",
        itemType = String.class,
        viewHolderClass = MyViewHolder.class,
        layoutId = R.layout.item_list)
public class SampleActivity extends AppCompatActivity {

    public ArrayList<String> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        LayoutInflater.from(getApplicationContext());

        new SampleAdapter.Builder(itemList)
                .setItemClickListener(new ItemClickListener() {
                    @Override
                    public void itemClicked(View view, int position) {

                    }
                })
                .setItemLongClickListener(new ItemLongClickListener() {
                    @Override
                    public void itemLongClicked(View view, int position) {

                    }
                }).build();
    }
}
