package papin_maps.maps.MVP.nearbyPhoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import papin_maps.maps.R;
import papin_maps.maps.adapter.MyAdapter;
import papin_maps.maps.core.MyApplication;
import papin_maps.maps.model.Photo;

/**
 * Created by Papin on 20.10.2016.
 */

public class NearbyView extends Activity implements NearbyInterface {

    private double lon, lan;
    private LatLng latLng;
    private MyAdapter mAdapter;
    private GridView mGridView;
    private NearbyPresenter nearbyPresenter;

    private Tracker mTracker;
    private MyApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        lon = i.getDoubleExtra("lon", 0);
        lan = i.getDoubleExtra("lan", 0);
        latLng = new LatLng(lan, lon);
        setContentView(R.layout.near_to_me_layout);
        nearbyPresenter = new NearbyPresenter(NearbyView.this);
        nearbyPresenter.getNearbyPhoto(latLng);

        application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("NearbyView");
        mTracker.send(new HitBuilders.EventBuilder()
                .setAction("Create")
                .setCategory("View")
                .build());

    }

    @Override
    public void getNearbyPhoto(ArrayList<Photo> photos) {
        mGridView = (GridView) findViewById(R.id.mygrid);
        mAdapter = new MyAdapter(this, photos);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nearbyPresenter.attach(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        nearbyPresenter.dettach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nearbyPresenter.attach(this);
    }
}
