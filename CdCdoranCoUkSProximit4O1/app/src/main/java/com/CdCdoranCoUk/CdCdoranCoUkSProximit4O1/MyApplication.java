package com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1;

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.cloud.EstimoteCloud;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            EstimoteSDK.initialize(getApplicationContext(), "cd-cdoran-co-uk-s-proximit-4o1", "039db0540ec7ef4633beff446dda3179");
            EstimoteSDK.enableRangingAnalytics(true);
            EstimoteSDK.enableMonitoringAnalytics(true);

            // uncomment to enable debug-level logging
            // it's usually only a good idea when troubleshooting issues with the Estimote SDK
            EstimoteSDK.enableDebugLogging(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}