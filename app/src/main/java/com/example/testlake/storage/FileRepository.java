package com.example.testlake.storage;


import androidx.annotation.NonNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileRepository {

    private static FileRepository INSTANCE;
    // TODO: задать базовый URL, используется во всех методах
    private String BASE_URL = "";

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

    public List<FileEntity> getFilesList(@NonNull String path) {
        return new ArrayList<>();
    }

    // TODO: можно использовать HttpUrlConnection,
    //       чтобы открыть файл на сервере и передать полученный InputStream в провайдер
    public InputStream openFile(@NonNull String path) {
        return null;
    }
}
