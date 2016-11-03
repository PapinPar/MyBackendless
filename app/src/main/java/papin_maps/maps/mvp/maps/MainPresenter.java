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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import papin_maps.maps.core.BackManager;

/**
 * Created by Papin on 26.10.2016.
 */

public class MainPresenter implements BackManager.getUploadResponse, BackManager.getPhotoResponse {
    MainInterface myView;
    private List<MarkerOptions> markerList = new ArrayList<>();
    private List<String> imageList = new ArrayList<>();
    private Boolean size = false;

    public MainPresenter(MainInterface answer) {
        this.myView = answer;
    }


    public void upload(final String email, final Bitmap bitmap, final String filePath, final LatLng mylatlng) {
        if (myView != null) {
            BackManager.getInstance().getStreet(email,bitmap,filePath,mylatlng,MainPresenter.this);
        }
    }

    public void dowloadMyPhoto(String emial) {
        if (myView != null) {
            BackManager.getInstance().dowloadMyPhoto(emial, MainPresenter.this);
        }
    }

    public void attach(MainInterface view) {
        myView = view;
        if (size == true)
            savePhoto();
    }

    public void dettach() {
        myView = null;
    }

    @Override
    public void uploadResponse(boolean answer) {
        if (myView != null)
            myView.uploadPhoto(answer);
    }

    @Override
    public void getPhotosModel(final BackendlessCollection<Map> response) throws IOException {
        imageList.clear();
        markerList.clear();
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
                                    markerList.add(options);
                                    imageList.add(imageUri);
                                    if (imageList.size() == response.getData().size()) {
                                        size = true;
                                        if(myView!=null)
                                            myView.getPhotoFromMemmory(markerList,imageList);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
            }
        }
    }

    private void savePhoto() {
        myView.getPhotoFromMemmory(markerList, imageList);
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
