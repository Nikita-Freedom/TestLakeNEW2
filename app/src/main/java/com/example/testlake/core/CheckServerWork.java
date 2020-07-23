package com.example.testlake.core;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.testlake.R;

import com.example.testlake.storage.FileEntity;
import com.example.testlake.storage.FileRepository;
import com.example.testlake.storage.SettingsRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CheckServerWork extends Worker {

    public static final String TAG_RESPONSE_URL = "response_url";
    public static final String TAG_RESPONSE_ERROR_MSG = "response_error_msg";

    public static UUID enqueue(@NonNull Context appContext) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(CheckServerWork.class)
                .build();
        WorkManager.getInstance(appContext).enqueue(request);

        return request.getId();
    }

    public CheckServerWork(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        FileRepository repo = FileRepository.getInstance(getApplicationContext());
        SettingsRepository settings = SettingsRepository.getInstance(getApplicationContext());

        String url = settings.serverUrl();
        List<FileEntity> files;
        try {
            files = repo.getFilesList(FileEntity.ROOT);

        } catch (Exception e) {
            return Result.failure(makeResponse(url, e.toString()));
        }

        if (files.isEmpty()) {
            return Result.failure(makeResponse(url,
                    getApplicationContext().getString(R.string.error_no_files)));

        } else {
            return Result.success(makeResponse(url, null));
        }
    }

    private Data makeResponse(String url, String errMsg) {
        return new Data.Builder()
                .putString(TAG_RESPONSE_URL, url)
                .putString(TAG_RESPONSE_ERROR_MSG, errMsg)
                .build();
    }
}
