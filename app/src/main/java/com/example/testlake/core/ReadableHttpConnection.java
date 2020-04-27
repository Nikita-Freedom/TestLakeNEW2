package com.example.testlake.core;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.testlake.core.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

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
    private SSLSocketFactory socketFactory;
    private InputStream is;
    private HttpURLConnection conn;
    public ReadableHttpConnection(@NonNull Context context, String url) throws Exception {
        // Кодируем пробелы. Некоторые версии Android некорректно работают с пробелами в URL
        url = url.replace(" ", "%20");
        this.url = new URL(url);
        this.socketFactory = Utils.makeSSLSocketFactory(context);
        run();
    }

    private void run() throws IOException {
        try {
            int redirectionCount = 0;
            while (redirectionCount++ < MAX_REDIRECTS) {
                conn = (HttpURLConnection)url.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(DEFAULT_TIMEOUT);
                conn.setReadTimeout(DEFAULT_TIMEOUT);
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

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
