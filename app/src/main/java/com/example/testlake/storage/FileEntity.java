package com.example.testlake.storage;

import androidx.annotation.NonNull;

public class FileEntity {

    @NonNull
    private String fileName;
    private boolean isDirectory;

    public FileEntity(@NonNull String fileName, boolean isDirectory) {
        this.fileName = fileName;
        this.isDirectory = isDirectory;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntity that = (FileEntity) o;

        if (isDirectory != that.isDirectory) return false;
        return fileName.equals(that.fileName);
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + (isDirectory ? 1 : 0);
        return result;
    }
}
