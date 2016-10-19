package papin_maps.maps;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Papin on 18.10.2016.
 */

public class DownoadPhoto {


    public void login() {
        BackManager.getInstance().login();
    }

    public void upload(Bitmap bitmap, String currUri) {
        BackManager.getInstance().upload(bitmap,currUri);

    }

    public void addIntoTable(LatLng latLng, String currUri) {
      BackManager.getInstance().addIntoTable(latLng,currUri);
    }

    public void dowloadPhotos(Context context, final GoogleMap map)  {
        BackManager.getInstance().downloadPhoto();
    }

}
