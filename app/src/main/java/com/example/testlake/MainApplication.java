package com.example.testlake;

import android.app.Application;
import android.util.Log;

import com.example.testlake.TLS.InstallCAdESTestTrustCertExample;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            InstallCAdESTestTrustCertExample inst = new InstallCAdESTestTrustCertExample(this);
            inst.getResult();
        } catch (Exception e) {
            Log.e(MainApplication.class.getSimpleName(), "Cannot init certificates");
        }
    }
}
