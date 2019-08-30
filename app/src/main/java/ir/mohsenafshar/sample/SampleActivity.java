package ir.mohsenafshar.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ir.mohsenafshar.adapterannotation.AdapterAnnotation;

@AdapterAnnotation
public class SampleActivity extends AppCompatActivity {

    public ArrayList<String> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }
}
