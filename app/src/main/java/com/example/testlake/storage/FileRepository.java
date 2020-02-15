package com.example.testlake.storage;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testlake.core.ReadableHttpConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class FileRepository {

    private static final String TAG = FileRepository.class.getSimpleName();

    private static final String PARENT_DIR_NAME = "Parent Directory";

    private static FileRepository INSTANCE;
    private SettingsRepository settings;

    public static FileRepository getInstance(@NonNull Context appContext) {
        if (INSTANCE == null) {
            synchronized (FileRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FileRepository(appContext);
                }
            }
        }

        return INSTANCE;
    }

    private FileRepository(Context appContext) {
        settings = SettingsRepository.getInstance(appContext);
    }

    public List<FileEntity> getFilesList(@NonNull FileEntity dir) throws IOException {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir.getPath() + "not a directory");
        }

        ArrayList<FileEntity> fileList = new ArrayList<>();
        Document document = Jsoup.connect(makeUrl(dir)).get();

        for (Element e : document.select("a[href]")) {
            // Игнорируем переход в папку уровнем выше
            if (e.text().equals(PARENT_DIR_NAME)) {
                continue;
            }
            String name = e.attr("href");
            // Декодируем строку из кодировки процента
            name = URLDecoder.decode(name, "UTF-8");
            boolean isDirectory = name.endsWith("/");
            // Игнорируем не-файлы
            if (!isDirectory && !hasExtension(name)) {
                continue;
            }
            String mimeType = (isDirectory ? null : getMimeType(name));
            String path = buildPath(dir, name);
            if (isDirectory) {
                // Удаляем слеш из имени
                name = name.substring(0, name.length() - 1);
            }

            fileList.add(new FileEntity(name, path, mimeType, isDirectory));
        }

        return fileList;
    }

    private String makeUrl(FileEntity file) {
        String baseUrl = settings.serverUrl();
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("Please provide server URL");
        }

        return baseUrl + file.getPath();
    }

    private String getExtension(String fileName) {
        int extensionPos = fileName.lastIndexOf(".");
        int lastSeparator = fileName.lastIndexOf("/");
        int index = (lastSeparator > extensionPos ? -1 : extensionPos);

        if (index == -1)
            return null;
        else
            return fileName.substring(index + 1);
    }

    private boolean hasExtension(String fileName) {
        return getExtension(fileName) != null;
    }

    private String getMimeType(String fileName) {
        String extension = getExtension(fileName);
        return (extension == null ? null : MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
    }

    private String buildPath(FileEntity dir, String fileName) {
        StringBuilder sb = new StringBuilder(dir.getPath());
        if (!dir.getPath().endsWith("/")) {
            sb.append("/");
        }
        sb.append(fileName);

        return sb.toString();
    }

    public InputStream openFile(@NonNull FileEntity file) throws IOException, GeneralSecurityException {
        if (file.isDirectory()) {
            throw new IllegalArgumentException(file.getPath() + "not a file");
        }

        return new ReadableHttpConnection(makeUrl(file));
    }

    @Nullable
    public FileEntity makeDirEntity(@NonNull String dirPath) {
        boolean isDirectory = dirPath.endsWith("/");
        if (!isDirectory) {
            return null;
        }

        // Удаляем слеш с конца
        String path = dirPath.substring(0, dirPath.length() - 1);

        // Копируем имя из пути, иначе присваиваем пустую строку
        int slashPos = path.lastIndexOf("/");
        String name;
        if (slashPos == -1 || slashPos + 1 >= path.length()) {
            name = "";
        } else {
            name = path.substring(slashPos + 1);
        }

        return new FileEntity(name, dirPath, null, true);
    }
}
