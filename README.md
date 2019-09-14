# binding-adapter

### Usage :

```
// @Param adapterClassName : Optional, if not provided, the generated Adapter class name will be 
// [Adapter + Name of component without Activity, Fragment or Dialog keyword
// In this case , the adapter class name will be SampleAdapter
@AdapterAnnotation(
        // adapterClassName = "MyAdapter",         
        itemType = String.class,
        viewHolderClass = MyViewHolder.class,
        layoutId = R.layout.item_list)
public class SampleActivity extends AppCompatActivity {

    public ArrayList<String> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

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
```
