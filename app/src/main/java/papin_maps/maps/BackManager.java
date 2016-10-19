package papin_maps.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.files.FileInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Papin on 19.10.2016.
 */

public class BackManager {

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

    public void login() {
        Backendless.UserService.login("ppapironi@mail.ru", "_papin_", new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    public void upload(Bitmap bitmap, String currUri) {
        BackendlessFile phFile = new BackendlessFile("");
        String[] name = currUri.split("/");
        int size = name.length;
        Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG, 100, name[size - 1], "photos",
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
        myTable.photoName ="https://api.backendless.com/EAFC4783-5BFF-C828-FF19-DB52ABFEE300/v1/files/photos/"+name[size - 1];
        myTable.Latitude = String.valueOf(latLng.latitude);
        myTable.Longitude = String.valueOf(latLng.longitude);
        myTable.category = "Photos";
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
        final ArrayList<String> myURL = new ArrayList<>();
        myURL.clear();
        Backendless.Files.listing( "/photos", new AsyncCallback<BackendlessCollection<FileInfo>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<FileInfo> response )
            {
                Iterator<FileInfo> filesIterator = response.getCurrentPage().iterator();
                while( filesIterator.hasNext() )
                {
                    FileInfo file = filesIterator.next();
                    String URL = file.getURL();
                    String publicURL = file.getPublicUrl();
                    myURL.add(file.getPublicUrl());
                    Log.d("MyUri","MyUri"+myURL);
                    Date createdOn = new Date( file.getCreatedOn() );
                    String name = file.getName();
                }
            }

            @Override
            public void handleFault( BackendlessFault fault )
            {
            }
        });
    }

    public void dowloadMyPhoto(){

    }

}
