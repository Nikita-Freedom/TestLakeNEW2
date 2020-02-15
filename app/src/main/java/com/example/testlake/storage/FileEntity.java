package com.example.testlake.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FileEntity implements Comparable<FileEntity> {

    public static final FileEntity ROOT = new FileEntity("", "/", null, true);

    @NonNull
    private String name;
    @NonNull
    private String path;
    private boolean isDirectory;
    @Nullable
    private String mimeType;

    public FileEntity(
            @NonNull String name,
            @NonNull String path,
            @Nullable String mimeType,
            boolean isDirectory
    ) {
        this.name = name;
        this.path = path;
        this.mimeType = mimeType;
        this.isDirectory = isDirectory;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    @Nullable
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileEntity that = (FileEntity)o;

        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public int compareTo(FileEntity o) {
        return path.compareTo(o.path);
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isDirectory=" + isDirectory +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
