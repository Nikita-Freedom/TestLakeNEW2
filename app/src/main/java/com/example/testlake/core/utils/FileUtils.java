package com.example.testlake.core.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private static final int BUFFER_SIZE = 8192;

    public static File getTmpDir(@NonNull Context appContext) {
        File tmpDir = appContext.getExternalCacheDir();
        if (!tmpDir.exists()) {
            if (!tmpDir.mkdirs()) {
                return null;
            }
        }

        return tmpDir;
    }

    public static void cleanTmpDir(@NonNull Context appContext) throws IOException {
        File tmpDir = getTmpDir(appContext);
        if (tmpDir == null) {
            throw new FileNotFoundException("Temp dir not found");
        }

        for (File f : tmpDir.listFiles()) {
            f.delete();
        }
    }

    public static void copyBytes(@NonNull InputStream is, @NonNull OutputStream os) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        int len;

        try {
            while ((len = is.read(buf)) >= 0) {
                os.write(buf, 0, len);
            }

        } finally {
            try {
                is.close();
                os.flush();
                os.close();

            } catch (IOException e) {
                // Ignore
            }
        }
    }
}
