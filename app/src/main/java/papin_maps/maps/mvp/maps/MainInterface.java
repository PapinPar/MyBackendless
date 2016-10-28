package papin_maps.maps.MVP.maps;

import android.content.Context;

import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by Papin on 26.10.2016.
 */

public interface MainInterface {
    Context getViewContext();
    void uploadPhoto(boolean answer);
    void getMyPhoto(MarkerOptions options, String imageUri) throws IOException;
    void getPhotoFromMemmory(List<MarkerOptions> listMarker, List<String> imageList);
}
