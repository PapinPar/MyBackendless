package papin_maps.maps.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Map;

import papin_maps.maps.model.MyTable;

/**
 * Created by Papin on 19.10.2016.
 */

public class BackManager {

    public interface getLoginAnswer {
        void getLoginANswer(boolean answer);
    }

    public interface getPhotoListner {
        void getMyPhoto(BackendlessCollection<Map> rsponse) throws IOException;
    }

    private static BackManager instance;

    BackManager(Context context) {
        Backendless.initApp(context, "EAFC4783-5BFF-C828-FF19-DB52ABFEE300", "11D8A3BF-C9FD-E716-FF3F-BA96B7D43C00", "v1");
    }

    public static BackManager getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new BackManager(context);
    }

    public boolean login(String Email, String Password, final getLoginAnswer answer) {
        Backendless.UserService.login(Email, Password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                answer.getLoginANswer(true);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                answer.getLoginANswer(false);
            }
        });
        return true;
    }

    public void upload(Bitmap bitmap, String currUri) {
        BackendlessFile phFile = new BackendlessFile("");
        String[] name = currUri.split("/");
        int size = name.length;
        Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG, 50, name[size - 1], "photos",
                new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(final BackendlessFile backendlessFile) {
                        Log.d("asd", "asd");
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.d("asd", "FAIl " + backendlessFault.toString());
                    }
                });
    }

    public void addIntoTable(LatLng latLng, String currUri) {
        String[] name = currUri.split("/");
        int size = name.length;
        MyTable myTable = new MyTable();
        myTable.email = "ppapironi@mail.ru";
        myTable.photoName = "https://api.backendless.com/EAFC4783-5BFF-C828-FF19-DB52ABFEE300/v1/files/photos/" + name[size - 1];
        myTable.Latitude = String.valueOf(latLng.latitude);
        myTable.Longitude = String.valueOf(latLng.longitude);
        myTable.category = "Photos";
        myTable.geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);
        Backendless.Data.of(MyTable.class).save(myTable, new AsyncCallback<MyTable>() {
            @Override
            public void handleResponse(MyTable myTable) {

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    public void downloadPhoto() {
        String whereClause = "email = 'ppapironi@mail.ru'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Persistence.of("MyTable").find(dataQuery, new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> mapBackendlessCollection) {
//https://api.backendless.com/EAFC4783-5BFF-C828-FF19-DB52ABFEE300/v1/files/photos/photo_1476868985668.png
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    public void dowloadMyPhoto(LatLng latLng,final getPhotoListner rsponse1) {
        String whereClause = "distance( "+latLng.latitude+","+latLng.longitude+", geoPoint.latitude, geoPoint.longitude ) < km(1)";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery(whereClause);
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setRelationsDepth(1);
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Data.of("MyTable").find(dataQuery, new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> mapBackendlessCollection) {
                Log.d("myUri", "myUri" + mapBackendlessCollection);
                try {
                    rsponse1.getMyPhoto(mapBackendlessCollection);
                } catch (IOException e) {
                    Log.d("PAPIN_TAG","e"+e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("PAPIN_TAG", "fail" + backendlessFault.toString());

            }
        });

    }

    public void register() {
        BackendlessUser user = new BackendlessUser();
        user.setProperty("email", "james.bond@mi6.co.uk");
        user.setPassword("iAmWatchingU");

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser registeredUser) {
                // user has been registered and now can login
            }

            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });

    }
}
