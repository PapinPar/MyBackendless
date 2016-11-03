package papin_maps.maps.MVP.nearbyPhoto;

import com.backendless.BackendlessCollection;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import papin_maps.maps.core.BackManager;
import papin_maps.maps.model.Photo;

/**
 * Created by Papin on 26.10.2016.
 */

public class NearbyPresenter implements BackManager.getPhotoResponse {

    private NearbyInterface view;
    private ArrayList<Photo> photosList = new ArrayList<>();

    public NearbyPresenter(NearbyInterface view) {
        this.view = view;
    }

    public void getNearbyPhoto(LatLng latLng) {
        if (view != null) {
            BackManager.getInstance().downloadPhoto(latLng, NearbyPresenter.this);
        }
    }


    public void attach(NearbyInterface view) {
        this.view = view;
    }

    public void dettach() {
        view = null;
    }

    @Override
    public void getPhotosModel(BackendlessCollection<Map> response) throws IOException {
        List<String> MyUrl = new ArrayList<>();
        List<String> street = new ArrayList<>();
        for (int i = 0; i < response.getData().size(); i++) {
            MyUrl.add(String.valueOf(response.getData().get(i).get("photoName")));
            street.add(String.valueOf(response.getData().get(i).get("street")));
            photosList.add(new Photo(MyUrl.get(i), street.get(i)));
        }
        view.getNearbyPhoto(photosList);
    }


}
