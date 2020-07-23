package com.example.testlake;

import android.app.Application;
import android.util.Log;

import com.example.testlake.TLS.InstallCAdESTestTrustCertExample;

import ru.CryptoPro.ssl.android.util.TLSSettings;


public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            //TLSSettings.setDefaultCRLRevocationOnline(false);
           // HttpTSLSreviceJava ht = new HttpTSLSreviceJava();
           InstallCAdESTestTrustCertExample inst = new InstallCAdESTestTrustCertExample(this);
           inst.getResult();
        } catch (Exception e) {
            Log.e(MainApplication.class.getSimpleName(), "Cannot init certificates");
        }
    }
}
