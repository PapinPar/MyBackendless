package papin_maps.maps.core;

import android.app.Application;

/**
 * Created by Papin on 19.10.2016.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        BackManager.init(this);
    }
}
