package papin_maps.maps.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import papin_maps.maps.R;
import papin_maps.maps.core.BackManager;
import papin_maps.maps.model.map.MainAddress;
import papin_maps.maps.retrofit.API;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class
MainActivity extends FragmentActivity implements OnMapReadyCallback, BackManager.getPhotoListner {

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
    private String currUri;
    private Context context;
    private Gson gson;
    private Retrofit retrofit;
    private API api;
    private String sLat;
    private boolean pirmission_granted = false;
    static private String APP_ID = String.valueOf(R.string.APP_ID);
    static private String SECRET_ID = String.valueOf(R.string.SECRET_ID);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        createDirectory();

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

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
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
                createData(bitmap, String.valueOf(b1itmap), mylatlng);
            } catch (IOException e) {
                Log.d("asd", "e:" + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void createData(final Bitmap bitmap, final String filePath, final LatLng mylatlng) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://maps.googleapis.com/maps/api/geocode/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        api = retrofit.create(API.class);
        sLat = mylatlng.latitude + "," + mylatlng.longitude;
        Call<MainAddress> usersCall = api.getAddress("false", sLat, "ru");
        usersCall.enqueue(new Callback<MainAddress>() {
            @Override
            public void onResponse(Call<MainAddress> call, Response<MainAddress> response1) {
                MainAddress mainAddress = response1.body();
                BackManager.getInstance().upload(bitmap, filePath, mylatlng,mainAddress.getResults().get(0).getFormattedAddress());
            }

            @Override
            public void onFailure(Call<MainAddress> call, Throwable t) {

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
                Log.d("click", "click");
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
        BackManager.getInstance().dowloadMyPhoto(MainActivity.this);
    }

    private void drawMyLocation() {
        mylocation = getLocation();
        if (mylocation != null) {
            mylatlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
            //  map.addMarker(new MarkerOptions().position(mylatlng).title("Me").draggable(true));
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

    @Override
    public void getMyPhoto(final BackendlessCollection<Map> response) throws IOException {
        List<String> MyUrl = new ArrayList<>();

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        for (int i = 0; i < response.getData().size(); i++) {
            MyUrl.add(String.valueOf(response.getData().get(i).get("photoName")));
        }

        for (int i = 0; i < response.getData().size(); i++) {
            Toast.makeText(context, "All photos will be downloaded", Toast.LENGTH_SHORT).show();
            ImageSize targetSize = new ImageSize(100, 120);
            final LatLng latLng;
            latLng = new LatLng((Double) response.getData().get(i).get("Latitude"), (Double) response.getData().get(i).get("Longitude"));
            imageLoader.loadImage(MyUrl.get(i)
                    , targetSize, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            loadedImage = getCroppedBitmap(loadedImage);
                            map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(loadedImage)));
                            Log.d("PAPIN_TAG", "imageUri" + imageUri);
                        }
                    });
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
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d("Maps", "onCameraChange: " + camera.target.latitude + "," + camera.target.longitude);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                        .zoom(23)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
                return true;
            }
        });

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
                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
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
            Intent i = new Intent(this, NearToMeActivity.class);
            i.putExtra("lon", mylatlng.longitude);
            i.putExtra("lan", mylatlng.latitude);
            startActivity(i);
        } else
            Toast.makeText(context, "Please turn on geolacation", Toast.LENGTH_SHORT).show();
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

}