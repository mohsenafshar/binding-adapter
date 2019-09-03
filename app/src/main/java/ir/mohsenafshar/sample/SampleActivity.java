package ir.mohsenafshar.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ir.mohsenafshar.adapterannotation.AdapterAnnotation;

@AdapterAnnotation(adapterClassName = "MyAdapter", itemType = String.class, layoutId = R.layout.item_list)
public class SampleActivity extends AppCompatActivity {

    public ArrayList<String> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }
}
