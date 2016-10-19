package papin_maps.maps.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.backendless.BackendlessCollection;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import papin_maps.maps.R;
import papin_maps.maps.core.BackManager;

public class
MainActivity extends FragmentActivity implements OnMapReadyCallback, BackManager.getPhotoListner {

    private GoogleMap map;
    private Bitmap bitmap;
    private File directory;
    private Button location;
    private Button markers;
    private GoogleApiClient mGoogleApiClient;
    private final int MY_PERMISSIONS_REQUEST = 21;
    private LatLng mylatlng;
    private int permissionCheck;
    private String currUri;
    private Context context;
    private boolean pirmission_granted = false;
    static private String APP_ID = String.valueOf(R.string.APP_ID);
    static private String SECRET_ID = String.valueOf(R.string.SECRET_ID);

    private ImageView image;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        createDirectory();

        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(4));
        //startActivityForResult(intent, 2);

        //Intent intent = new Intent();
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        //startActivityForResult(intent, 1);

        markers = (Button) findViewById(R.id.getPhotos);
        location = (Button) findViewById(R.id.location);
        checkPirmission();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case 4:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".png");
                break;
        }
        currUri = String.valueOf(Uri.fromFile(file));
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        InputStream stream = null;
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(intent.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                //downoadPhoto.upload(bitmap, currUri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                {
                    if (stream != null)
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Uri b1itmap = Uri.parse(currUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), b1itmap);
                //downoadPhoto.upload(bitmap, String.valueOf(b1itmap));
            } catch (IOException e) {
                Log.d("asd", "e:" + e.getMessage());
                e.printStackTrace();
            }
        }

    }


    ///    ***********     MAPS     ***********
    private void checkPirmission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            pirmission_granted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    private void init() {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("Maps", "onMapClick: " + latLng.latitude + "," + latLng.longitude);
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d("Maps", "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d("Maps", "onCameraChange: " + camera.target.latitude + "," + camera.target.longitude);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        BitmapDescriptor factory = BitmapDescriptorFactory.fromResource(R.drawable.powered_by_google_light);
        LatLng myPosition = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(myPosition).title("Me").draggable(true).icon(factory));
        map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        init();

        markers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == 0)
                    pirmission_granted = true;
                if (pirmission_granted == true) {
                    drawMarkers();
                }
            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "click");
                permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == 0)
                    pirmission_granted = true;
                if (pirmission_granted == true) {
                    drawMyLocation();
                }
            }
        });

    }

    private void drawMarkers() {
        Location mylocation = getLocation();
        if (mylocation != null) {
            mylatlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
            map.addMarker(new MarkerOptions().position(mylatlng).title("Me").draggable(true));
            onCameraUPD(mylocation.getLatitude(), mylocation.getLongitude());
            BackManager.getInstance().dowloadMyPhoto(mylatlng, MainActivity.this);
        }
    }

    private void drawMyLocation() {
        Location mylocation = getLocation();

        if (mylocation != null) {
            mylatlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
            map.addMarker(new MarkerOptions().position(mylatlng).title("Me").draggable(true));
            onCameraUPD(mylocation.getLatitude(), mylocation.getLongitude());
            //downoadPhoto.addIntoTable(mylatlng, currUri);

        }
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (location == null)
                    location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
        }

        return location;
    }

    public void onCameraUPD(double LA, double LO) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(LA, LO))
                .zoom(17)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }


    @Override
    public void getMyPhoto(final BackendlessCollection<Map> response) throws IOException {
        final List<String> MyUrl = new ArrayList<>();
        for (int i = 0; i < response.getData().size(); i++) {
            MyUrl.add(String.valueOf(response.getData().get(i).get("photoName")));
        }
        Thread t;
        final List<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.clear();


        final Handler h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        showMarkers(response, bitmaps);
                        break;
                }
            }
        };

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < MyUrl.size(); i++) {
                    URL url = null;
                    InputStream in = null;
                    try {
                      //  url = new URL(MyUrl.get(i));
                       in = new java.net.URL(MyUrl.get(i)).openStream();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();//url
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                        bitmaps.add(BitmapFactory.decodeStream(in));
                }
                h.sendEmptyMessage(1);

            }
        });t.start();



    }

    private void showMarkers(BackendlessCollection<Map> response, List<Bitmap> bitmaps) {
        LatLng latLng;
        for (int i = 0; i < response.getData().size(); i++) {
            latLng = new LatLng((Double) response.getData().get(i).get("Latitude"), (Double) response.getData().get(i).get("Longitude"));
            map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmaps.get(i))));
        }
    }
}