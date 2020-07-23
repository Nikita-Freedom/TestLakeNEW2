package com.example.testlake.core.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import ru.CryptoPro.JCP.KeyStore.StoreInputStream;
import ru.CryptoPro.JCSP.JCSP;
import ru.CryptoPro.JCSP.support.BKSTrustStore;
import ru.CryptoPro.ssl.android.Provider;
import ru.cprocsp.ACSP.tools.common.Constants;

public class Utils {

    public static final String HTTP_PREFIX = "https";

    public static int getAttributeColor(@NonNull Context context, int attributeId)
    {
        TypedArray a = context.obtainStyledAttributes(new TypedValue().data, new int[]{ attributeId });
        int color = a.getColor(0, 0);
        a.recycle();

        return color;
    }

    public static SSLSocketFactory makeSSLSocketFactory(@NonNull Context context) throws Exception {

        KeyStore ts = KeyStore.getInstance(BKSTrustStore.STORAGE_TYPE, BouncyCastleProvider.PROVIDER_NAME);
        String trustStorePath = context.getApplicationInfo().dataDir +
                File.separator +
                BKSTrustStore.STORAGE_DIRECTORY +
                File.separator +
                BKSTrustStore.STORAGE_FILE_TRUST;
        FileInputStream stream = new FileInputStream(trustStorePath);

        try {
            ts.load(stream, BKSTrustStore.STORAGE_PASSWORD);
        } catch (Exception e) {
            Log.e(Constants.APP_LOGGER_TAG, "Cannot load key store", e);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(Provider.KEYMANGER_ALG, Provider.PROVIDER_NAME);
        tmf.init(ts);

        SSLContext sslContext = SSLContext.getInstance(Provider.ALGORITHM, Provider.PROVIDER_NAME);
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext.getSocketFactory();

    }
}
