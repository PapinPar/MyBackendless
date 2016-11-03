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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import papin_maps.maps.model.MyTable;
import papin_maps.maps.model.map.MainAddress;
import papin_maps.maps.retrofit.API;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Papin on 19.10.2016.
 */

public class BackManager {

    private Gson gson;
    private Retrofit retrofit;
    private API api;
    private String sLat;

    public interface getLoginModelResponse {
        void getLoginModelResponse(boolean answer, BackendlessFault error);
    }

    public interface getPhotoResponse {
        void getPhotosModel(BackendlessCollection<Map> rsponse) throws IOException;
    }

    public interface getRegistResponseModel {
        void getRegistAnswerCompleted(BackendlessUser respone) throws IOException;
        void getRegistAnswerFail(BackendlessFault fault) throws IOException;
    }

    public interface getUploadResponse {
        void uploadResponse(boolean answer);
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

    public boolean login(String Email, String Password, final getLoginModelResponse answer) {
        Backendless.UserService.login(Email, Password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                answer.getLoginModelResponse(true,null);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                answer.getLoginModelResponse(false,backendlessFault);
            }
        });
        return true;
    }

    public void upload(final String email, Bitmap bitmap, final String fileName, LatLng latLng, final String street, final getUploadResponse answer) {
        final LatLng finalLatLng = latLng;
        Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.JPEG, 70, fileName, "photos",
                new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(final BackendlessFile backendlessFile) {
                        Log.d("asd", "asd");
                        addIntoTable(email, finalLatLng, fileName, street, answer);
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.d("asd", "FAIl " + backendlessFault.toString());
                    }
                });
    }

    public void addIntoTable(String email, LatLng latLng, String currUri, String street, final getUploadResponse answer) {
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
        myTable.photoName = "https://api.backendless.com/EAFC4783-5BFF-C828-FF19-DB52ABFEE300/v1/files/photos/" + pathPhoto;
        myTable.Latitude = String.valueOf(latLng.latitude);
        myTable.Longitude = String.valueOf(latLng.longitude);
        myTable.category = "Photos";
        myTable.geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);
        myTable.street = street;
        Backendless.Data.of(MyTable.class).save(myTable, new AsyncCallback<MyTable>() {
            @Override
            public void handleResponse(MyTable myTable) {
                answer.uploadResponse(true);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("PAPIN_TAG", "fail" + backendlessFault.toString());

            }
        });
    }

    public void downloadPhoto(LatLng latLng, final getPhotoResponse response1) {
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
                    response1.getPhotosModel(mapBackendlessCollection);
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

    public void dowloadMyPhoto(String email, final getPhotoResponse response1) {
        String whereClause = "email = '" + email + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Persistence.of("MyTable").find(dataQuery, new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> mapBackendlessCollection) {
                try {
                    response1.getPhotosModel(mapBackendlessCollection);
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

    public void register(String sEmail, String sPassword, final getRegistResponseModel answer) throws IOException {
        BackendlessUser user = new BackendlessUser();
        user.setEmail(sEmail);
        user.setPassword(sPassword);

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser registeredUser) {
                try {
                    answer.getRegistAnswerCompleted(registeredUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void handleFault(BackendlessFault fault) {
                try {
                    answer.getRegistAnswerFail(fault);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    public void getStreet(final String email, final Bitmap bitmap, final String filePath, final LatLng mylatlng, final getUploadResponse response)
    {
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
                BackManager.getInstance().upload(email, bitmap, filePath, mylatlng, mainAddress.getResults().get(0).getFormattedAddress(),response);
            }

            @Override
            public void onFailure(Call<MainAddress> call, Throwable t) {
            }
        });
    }
}
