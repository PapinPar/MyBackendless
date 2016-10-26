package papin_maps.maps.MVP.nearbyPhoto;

import com.backendless.BackendlessCollection;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import papin_maps.maps.core.BackManager;
import papin_maps.maps.model.Product;

/**
 * Created by Papin on 26.10.2016.
 */

public class PresenterNearby implements BackManager.getPhotoListner {

    private InterfaceNearby view;
    private ArrayList<Product> products = new ArrayList<>();

    public PresenterNearby(InterfaceNearby view) {
        this.view = view;
    }

    public void getNearbyPhoto(LatLng latLng) {
        if (view != null) {
            BackManager.getInstance().downloadPhoto(latLng, PresenterNearby.this);
        }
    }


    public void attach(InterfaceNearby view) {
        this.view = view;
    }

    public void dettach() {
        view = null;
    }

    @Override
    public void getMyPhotoAnswer(BackendlessCollection<Map> response) throws IOException {
        List<String> MyUrl = new ArrayList<>();
        List<String> street = new ArrayList<>();
        for (int i = 0; i < response.getData().size(); i++) {
            MyUrl.add(String.valueOf(response.getData().get(i).get("photoName")));
            street.add(String.valueOf(response.getData().get(i).get("street")));
            products.add(new Product(MyUrl.get(i), street.get(i)));
        }
        view.NearbyRespone(products);
    }


}
