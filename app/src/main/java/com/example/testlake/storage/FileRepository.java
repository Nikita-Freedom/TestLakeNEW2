package com.example.testlake.storage;


import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FileRepository {

    private static FileRepository INSTANCE;
    // TODO: задать базовый URL, используется во всех методах
    private String BASE_URL = "localhost/test";

    public static FileRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (FileRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FileRepository();
                }
            }
        }

        return INSTANCE;
    }

    public List<FileEntity> getFilesList(@NonNull FileEntity dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir.getPath() + "not a directory");
        }
        Document document = null;

        try {
            document = Jsoup.connect(BASE_URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (document != null) {
            for (Element e : document.select("a")) {

            }

        } else {
            return new ArrayList<>();
        }


    // Работаем с путём
    String path = dir.getPath();
    return new ArrayList<>();
}

    // TODO: можно использовать HttpUrlConnection,
    //       чтобы открыть файл на сервере и передать полученный InputStream в провайдер
    public InputStream openFile(@NonNull FileEntity file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException(file.getPath() + "not a file");
        }

        return null;
    }
}
