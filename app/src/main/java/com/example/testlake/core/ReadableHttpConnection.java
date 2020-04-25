package com.example.testlake.core;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.testlake.TLS.ClientSample;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HttpsURLConnection;

import ru.CryptoPro.JCSP.support.BKSTrustStore;
import ru.cprocsp.ACSP.tools.common.Constants;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;

/**
 * Осуществляет соединение с сервером и позволяет читать из файла
 */

public class ReadableHttpConnection extends InputStream {

    private static final int MAX_REDIRECTS = 5;
    private static final int DEFAULT_TIMEOUT = (int)(50 * SECOND_IN_MILLIS);

    private URL url;
    private TLSSocketFactory socketFactory;
    private InputStream is;
    private HttpURLConnection conn;
    Context context;
    public ReadableHttpConnection(String url) throws IOException, GeneralSecurityException {
        ClientSample clientSample = new ClientSample();
        // Кодируем пробелы. Некоторые версии Android некорректно работают с пробелами в URL
        url = url.replace(" ", "%20");
        this.url = new URL(url);
        this.socketFactory = new TLSSocketFactory();
        run(context);
    }

    private void run(@NotNull Context context) throws IOException {
        try {
            KeyStore ts = KeyStore.getInstance(BKSTrustStore.STORAGE_TYPE, BouncyCastleProvider.PROVIDER_NAME);

            String trustStorePath = context.getApplicationInfo().dataDir + File.separator + BKSTrustStore.STORAGE_DIRECTORY + File.separator + BKSTrustStore.STORAGE_FILE_TRUST;

            //trustStorePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator;

            FileInputStream stream = new FileInputStream(trustStorePath);

            try {
                ts.load(stream, BKSTrustStore.STORAGE_PASSWORD);
            } catch (Exception e) {
                Log.e(Constants.APP_LOGGER_TAG,"ХУЙ", e);
            }

            KeyStore ks = null;
            int redirectionCount = 0;
            while (redirectionCount++ < MAX_REDIRECTS) {
                conn = (HttpURLConnection)url.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(DEFAULT_TIMEOUT);
                conn.setReadTimeout(DEFAULT_TIMEOUT);

                if (conn instanceof HttpsURLConnection)
                    ((HttpsURLConnection)conn).setSSLSocketFactory(socketFactory);

                int responseCode = conn.getResponseCode();
                switch (responseCode) {
                    case HTTP_OK:
                    case HTTP_PARTIAL:
                        is = conn.getInputStream();
                        return;
                    case HTTP_MOVED_PERM:
                    case HTTP_MOVED_TEMP:
                    case HTTP_SEE_OTHER:
                        String location = conn.getHeaderField("Location");
                        url = new URL(url, location);
                        disconnect();
                        continue;
                    default:
                        throw new IOException("Unhandled HTTP response: " +
                                responseCode + " " + conn.getResponseMessage());
                }
            }

            if (redirectionCount >= MAX_REDIRECTS) {
                throw new IOException("Too many redirects");
            }

        } catch (IOException e) {
            disconnect();
            throw e;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (conn != null) {
            conn.disconnect();
        }
    }

    @Override
    public int read(@NonNull byte[] b) throws IOException {
        try {
            return (is == null ? super.read(b) : is.read(b));

        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        try {
            return (is == null ? super.read(b, off, len) : is.read(b, off, len));

        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        try {
            return (is == null ? super.skip(n) : is.skip(n));

        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public int available() throws IOException {
        try {
            return (is == null ? super.available() : is.available());

        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        disconnect();

        if (is == null) {
            super.close();

        } else {
            is.close();
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (is == null) {
            super.mark(readlimit);

        } else {
            is.mark(readlimit);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        try {
            if (is == null) {
                super.reset();

            } else {
                is.reset();
            }

        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public boolean markSupported() {
        return (is == null ? super.markSupported() : is.markSupported());
    }

    @Override
    public int read() throws IOException {
        try {
            return (is == null ? 0 : is.read());

        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }
}
