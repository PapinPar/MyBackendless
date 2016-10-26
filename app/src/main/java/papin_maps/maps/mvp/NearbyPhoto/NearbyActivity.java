package papin_maps.maps.MVP.nearbyPhoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import papin_maps.maps.R;
import papin_maps.maps.adapter.MyAdapter;
import papin_maps.maps.model.Product;

/**
 * Created by Papin on 20.10.2016.
 */

public class NearbyActivity extends Activity implements InterfaceNearby {

    private double lon, lan;
    private LatLng latLng;
    private MyAdapter mAdapter;
    private GridView mGridView;
    private PresenterNearby presenterNearby;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        lon = i.getDoubleExtra("lon", 0);
        lan = i.getDoubleExtra("lan", 0);
        latLng = new LatLng(lan, lon);
        setContentView(R.layout.near_to_me_layout);
        presenterNearby = new PresenterNearby(NearbyActivity.this);
        presenterNearby.getNearbyPhoto(latLng);
    }

    @Override
    public void NearbyRespone(ArrayList<Product> products) {
        mGridView = (GridView) findViewById(R.id.mygrid);
        mAdapter = new MyAdapter(this, products);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenterNearby.attach(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        presenterNearby.dettach();
    }
}
