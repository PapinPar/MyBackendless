package papin_maps.maps.MVP.maps;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.backendless.BackendlessCollection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import papin_maps.maps.core.BackManager;
import papin_maps.maps.model.map.MainAddress;
import papin_maps.maps.retrofit.API;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Papin on 26.10.2016.
 */

public class MainPresenter implements BackManager.getUploadAnswer, BackManager.getPhotoListner {
    MainInterface myView;
    private Gson gson;
    private Retrofit retrofit;
    private API api;
    private String sLat;

    public MainPresenter(MainInterface answer) {
        this.myView = answer;
    }


    public void upload(final String email, final Bitmap bitmap, final String filePath, final LatLng mylatlng) {
        if (myView != null) {
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
                    BackManager.getInstance().upload(email, bitmap, filePath, mylatlng, mainAddress.getResults().get(0).getFormattedAddress(), MainPresenter.this);
                }

                @Override
                public void onFailure(Call<MainAddress> call, Throwable t) {
                }
            });
        }
    }

    public void dowloadMyPhoto(String emial) {
        if (myView != null) {
            BackManager.getInstance().dowloadMyPhoto(emial, MainPresenter.this);
        }
    }

    public void attach(MainInterface view) {
        myView = view;
    }

    public void dettach() {
        myView = null;
    }


    @Override
    public void uploadAnswer(boolean answer) {
        if (myView != null)
            myView.downloadPhoto(answer);

    }

    @Override
    public void getPhotosModel(BackendlessCollection<Map> response) throws IOException {
        if (myView != null) {
            List<String> MyUrl = new ArrayList<>();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(myView.getViewContext()));

            for (int i = 0; i < response.getData().size(); i++) {
                MyUrl.add(String.valueOf(response.getData().get(i).get("photoName")));
            }

            for (int i = 0; i < response.getData().size(); i++) {
                Toast.makeText(myView.getViewContext(), "All photos will be downloaded", Toast.LENGTH_SHORT).show();
                ImageSize targetSize = new ImageSize(100, 120);
                final LatLng latLng;
                latLng = new LatLng((Double) response.getData().get(i).get("Latitude"), (Double) response.getData().get(i).get("Longitude"));
                imageLoader.loadImage(MyUrl.get(i)
                        , targetSize, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                try {
                                    Bitmap cropImage = getCroppedBitmap(loadedImage);
                                    MarkerOptions options = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(cropImage)).draggable(true);
                                    Log.d("PAPIN_TAG", "imageUri" + imageUri);
                                    if (myView != null)
                                        myView.getMyPhoto(options, imageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }
    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
