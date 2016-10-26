package papin_maps.maps.MVP.maps;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import papin_maps.maps.MVP.nearbyPhoto.NearbyActivity;
import papin_maps.maps.R;

public class
MainActivity extends FragmentActivity implements OnMapReadyCallback, InterfaceMain {

    private GoogleMap map;
    private Bitmap bitmap;
    private File directory;
    private Location mylocation;
    private Button addPhoto;
    private Button myPhoto;
    private GoogleApiClient mGoogleApiClient;
    private final int MY_PERMISSIONS_REQUEST = 21;
    private LatLng mylatlng;
    private int permissionCheck;
    private String currUri, email;
    private Context context;
    private boolean pirmission_granted = false;
    static private String APP_ID = String.valueOf(R.string.APP_ID);
    static private String SECRET_ID = String.valueOf(R.string.SECRET_ID);
    private SharedPreferences sp;
    private HashMap<String, String> myMap;
    private PresenterMain presenterMain;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        sp = getSharedPreferences("Data", MODE_PRIVATE);
        email = sp.getString("EMAIL", "");
        presenterMain = new PresenterMain(MainActivity.this);
        createDirectory();
        myMap = new HashMap<>();
        myPhoto = (Button) findViewById(R.id.getPhotos);
        addPhoto = (Button) findViewById(R.id.location);
        checkPirmission();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mylocation = getLocation();
        if (mylocation != null)
            mylatlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        InputStream stream = null;
        int min = 30;
        int max = 123;
        String symbols = "qwertyuiopasdfghjklzxcvbnmQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890";
        StringBuilder randString = new StringBuilder();
        int count = (int) (Math.random() * ++max) + min;
        for (int i = 0; i < count; i++)
            randString.append(symbols.charAt((int) (Math.random() * symbols.length())));
        String filePath = String.valueOf(randString);
        Log.d("PAPIN_TAG", "" + filePath);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(intent.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                createData(bitmap, filePath, mylatlng);

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
                createData(bitmap, filePath, mylatlng);
            } catch (IOException e) {
                Log.d("asd", "e:" + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void createData(final Bitmap bitmap, final String filePath, final LatLng mylatlng) {
        onResume();
        myMap.clear();
        presenterMain.upload(email, bitmap, filePath, mylatlng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        BitmapDescriptor factory = BitmapDescriptorFactory.fromResource(R.drawable.powered_by_google_light);
        LatLng myPosition = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(myPosition).title("Me").draggable(true).icon(factory));
        map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        init();

        myPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawMyLocation();
                showDialog(1);
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("popoutPhoto", "popoutPhoto");
                permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == 0)
                    pirmission_granted = true;
                if (pirmission_granted == true) {
                    drawMyLocation();
                       if (mylatlng != null) {
                    showDialog(0);
                      } else {
                    Toast.makeText(context, "Please turn on geolacation", Toast.LENGTH_SHORT).show();
                     }
                }
            }
        });

    }

    private void drawMyPhotos() {
        if (mylocation != null) {
            mylatlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
            onCameraUPD(mylocation.getLatitude(), mylocation.getLongitude());
        }
        presenterMain.dowloadMyPhoto(email);
    }

    private void drawMyLocation() {
        mylocation = getLocation();
        if (mylocation != null) {
            mylatlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
            onCameraUPD(mylocation.getLatitude(), mylocation.getLongitude());
        } else
            Toast.makeText(context, "Please turn on geolacation", Toast.LENGTH_SHORT).show();

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
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d("Maps", "onCameraChange: " + camera.target.latitude + "," + camera.target.longitude);
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                        .zoom(17)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);

                popoutPhoto(marker.getId());
                return true;
            }
        });

    }

    private void popoutPhoto(String id) {

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.pop_out_layout);

        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Picasso.with(context)
                .load(myMap.get(id))
                .resize(320, 480)
                .centerCrop()
                .into(image);

        dialog.show();
    }

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

    private Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case 4:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
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

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case 0:
                builder.setMessage("Choose Action")
                        .setCancelable(true)
                        .setPositiveButton("From Gallery",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent i = new Intent(Intent.ACTION_PICK);
                                        i.setType("image/*");
                                        startActivityForResult(i, 1);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("From Camera",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(4));
                                        startActivityForResult(intent, 2);
                                        dialog.cancel();
                                    }
                                });
                return builder.create();
            case 1:
                builder.setMessage("Choose Action")
                        .setCancelable(true)
                        .setPositiveButton("Near to me",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        startNeartome();
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("All my photos",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                                                Manifest.permission.ACCESS_FINE_LOCATION);
                                        if (permissionCheck == 0)
                                            pirmission_granted = true;
                                        if (pirmission_granted == true) {
                                            drawMyPhotos();
                                        } else
                                            drawMyPhotos();
                                    }
                                });
                return builder.create();
        }
        return null;
    }

    private void startNeartome() {
        drawMyLocation();
        if (mylatlng != null) {
            Intent i = new Intent(this, NearbyActivity.class);
            i.putExtra("lon", mylatlng.longitude);
            i.putExtra("lan", mylatlng.latitude);
            startActivity(i);
        } else
            Toast.makeText(context, "Please turn on geolacation", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getViewContext() {
        return context;
    }

    @Override
    public void UploadAnswer(boolean answer) {
        if (answer)
            Toast.makeText(context, "Photo added successfully", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getMyPhoto(MarkerOptions options,String imageUri) throws IOException {
        String id = map.addMarker(options).getId();
        myMap.put(id, imageUri);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenterMain.attach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenterMain.dettach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenterMain.attach(this);
    }
}