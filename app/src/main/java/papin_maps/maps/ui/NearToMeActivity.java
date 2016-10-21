package papin_maps.maps.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.backendless.BackendlessCollection;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import papin_maps.maps.R;
import papin_maps.maps.adapter.MyAdapter;
import papin_maps.maps.core.BackManager;
import papin_maps.maps.model.Product;

/**
 * Created by Papin on 20.10.2016.
 */

public class NearToMeActivity extends Activity implements BackManager.getPhotoListner {


    private double lon, lan;
    private LatLng latLng;
    private MyAdapter mAdapter;
    private GridView mGridView;


    ArrayList<Product> products = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        lon = i.getDoubleExtra("lon", 0);
        lan = i.getDoubleExtra("lan", 0);
        latLng = new LatLng(lan, lon);
        setContentView(R.layout.near_to_me_layout);
        BackManager.getInstance().downloadPhoto(latLng, NearToMeActivity.this);
    }

    private void op(List<String> myUrl,List<String> street) {
        for (int i = 0; i < myUrl.size(); i++) {
            products.add(new Product(myUrl.get(i),street.get(i)));
        }
        mGridView = (GridView) findViewById(R.id.mygrid);
        mAdapter = new MyAdapter(this, products);
        mGridView.setAdapter(mAdapter);
    }


    @Override
    public void getMyPhoto(BackendlessCollection<Map> response) throws IOException {
        List<String> MyUrl = new ArrayList<>();
        List<String> street = new ArrayList<>();
        for (int i = 0; i < response.getData().size(); i++) {
            MyUrl.add(String.valueOf(response.getData().get(i).get("photoName")));
            street.add(String.valueOf(response.getData().get(i).get("street")));
        }
        op(MyUrl,street);
    }
}
