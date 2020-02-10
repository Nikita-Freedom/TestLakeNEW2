package com.example.testlake.storage;

import androidx.annotation.NonNull;

public class FileEntity {

    public static final FileEntity ROOT = new FileEntity("", "", true);

    @NonNull
    private String fileName;
    @NonNull
    private String path;
    private boolean isDirectory;

    public FileEntity(@NonNull String fileName, @NonNull String path, boolean isDirectory) {
        this.fileName = fileName;
        this.path = path;
        this.isDirectory = isDirectory;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntity that = (FileEntity) o;

        if (isDirectory != that.isDirectory) return false;
        if (!fileName.equals(that.fileName)) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + (isDirectory ? 1 : 0);
        return result;
    }
}
