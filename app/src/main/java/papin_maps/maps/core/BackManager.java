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

    public interface getLoginModelAnswer {
        void getLoginModelAnswer(boolean answer);
    }

    public interface getPhotoListner {
        void getMyPhoto(BackendlessCollection<Map> rsponse) throws IOException;
    }

    public interface registrationAnswer{
        void getRegistAnswer(BackendlessUser respone) throws IOException;
        void getRegistAnswer(BackendlessFault fault) throws IOException;
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

    public boolean login(String Email, String Password, final getLoginModelAnswer answer) {
        Backendless.UserService.login(Email, Password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                answer.getLoginModelAnswer(true);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                answer.getLoginModelAnswer(false);
            }
        });
        return true;
    }

    public void upload(final String email, Bitmap bitmap, final String fileName, LatLng latLng, final String street) {
        latLng = new LatLng(50.0028932, 36.2732864);
        final LatLng finalLatLng = latLng;
        Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.JPEG, 80, fileName, "photos",
                new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(final BackendlessFile backendlessFile) {
                        Log.d("asd", "asd");
                        addIntoTable(email,finalLatLng, fileName,street);
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.d("asd", "FAIl " + backendlessFault.toString());
                    }
                });
    }

    public void addIntoTable(String email,LatLng latLng, String currUri,String street) {
        String pathPhoto;
        if (currUri.contains("/")) {
            String[] name = currUri.split("/");
            int size = name.length;
            pathPhoto = name[size - 1];
        } else {
            pathPhoto = currUri;
        }
        MyTable myTable = new MyTable();
        myTable.email = email;
        myTable.photoName = "https://api.backendless.com/EAFC4783-5BFF-C828-FF19-DB52ABFEE300/v1/files/photos/" +pathPhoto;
        myTable.Latitude = String.valueOf(latLng.latitude);
        myTable.Longitude = String.valueOf(latLng.longitude);
        myTable.category = "Photos";
        myTable.geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);
        myTable.street = street;
        Backendless.Data.of(MyTable.class).save(myTable, new AsyncCallback<MyTable>() {
            @Override
            public void handleResponse(MyTable myTable) {

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("PAPIN_TAG","fail"+backendlessFault.toString());

            }
        });
    }

    public void downloadPhoto(LatLng latLng,final getPhotoListner response1) {
        String whereClause = "distance( " + latLng.latitude + "," + latLng.longitude + ", geoPoint.latitude, geoPoint.longitude ) < km(1)";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery(whereClause);
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setRelationsDepth(1);
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Data.of("MyTable").find(dataQuery, new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> mapBackendlessCollection) {
                Log.d("myUri", "myUri" + mapBackendlessCollection);
                try {
                    response1.getMyPhoto(mapBackendlessCollection);
                } catch (IOException e) {
                    Log.d("PAPIN_TAG", "e" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("PAPIN_TAG", "fail" + backendlessFault.toString());

            }
        });
    }

    public void dowloadMyPhoto(String email,final getPhotoListner response1) {
        String whereClause = "email = '"+email+"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Persistence.of("MyTable").find(dataQuery, new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> mapBackendlessCollection) {
                Log.d("myUri", "myUri" + mapBackendlessCollection);
                try {
                    response1.getMyPhoto(mapBackendlessCollection);
                } catch (IOException e) {
                    Log.d("PAPIN_TAG", "e" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });

    }
    public void register(String sEmail, String sPassword, final registrationAnswer answer) {
        BackendlessUser user = new BackendlessUser();
        user.setEmail(sEmail);
        user.setPassword(sPassword);

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser registeredUser) {
                try {
                    answer.getRegistAnswer(registeredUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void handleFault(BackendlessFault fault) {
                try {
                    answer.getRegistAnswer(fault);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }
}
