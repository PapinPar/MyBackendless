package papin_maps.maps.core;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;
import papin_maps.maps.R;

/**
 * Created by Papin on 19.10.2016.
 */

public class MyApplication extends Application {

    Tracker mTracker;

    @Override
    public void onCreate() {
        BackManager.init(this);
        Fabric.with(this, new Crashlytics());
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

}
