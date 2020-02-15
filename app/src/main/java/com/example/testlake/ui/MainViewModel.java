package com.example.testlake.ui;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.work.Data;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.testlake.R;
import com.example.testlake.core.CheckServerWork;
import com.example.testlake.storage.SettingsRepository;

import java.util.UUID;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private SettingsRepository settings;
    private MediatorLiveData<WorkInfo> checkServerWorkerResult = new MediatorLiveData<>();
    public ObservableField<CheckServerState> checkState = new ObservableField<>();
    public ObservableField<String> url = new ObservableField<>();

    public static class CheckServerState {
        public Status status;
        public String msg;

        public CheckServerState(Status status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public enum Status {
            UNKNOWN,
            CHECKING,
            OK,
            ERROR,
        }
    }

    public MainViewModel(@NonNull Application application) {
        super(application);

        settings = SettingsRepository.getInstance(application.getApplicationContext());
        url.set(settings.serverUrl());
        checkState.set(unknownState());
        url.addOnPropertyChangedCallback(urlCallback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        url.removeOnPropertyChangedCallback(urlCallback);
    }

    void forceCheckServer() {
        String urlStr = url.get();
        if (!TextUtils.isEmpty(urlStr)) {
            checkServer(urlStr);
        }
    }

    LiveData<WorkInfo> observeCheckingServer() {
        return checkServerWorkerResult;
    }

    void handleCheckingServer(@NonNull WorkInfo workInfo) {
        if (!workInfo.getState().isFinished()) {
            return;
        }

        Data data = workInfo.getOutputData();
        String responseUrl = data.getString(CheckServerWork.TAG_RESPONSE_URL);
        String currentUrl = this.url.get();
        if (currentUrl == null || !currentUrl.equals(responseUrl)) {
            return;
        }
        String responseMsg = data.getString(CheckServerWork.TAG_RESPONSE_ERROR_MSG);
        if (responseMsg != null) {
            Log.e(TAG, "[" + responseUrl + "] " + responseMsg);
            checkState.set(errorState(responseMsg));
        } else {
            checkState.set(okState());
        }
    }

    private Observable.OnPropertyChangedCallback urlCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            String urlStr = url.get();
            settings.serverUrl(url == null ? "" : urlStr);
            checkServer(urlStr);
        }
    };

    private void checkServer(String url) {
        if (TextUtils.isEmpty(url)) {
            checkState.set(unknownState());
            return;
        }
        checkState.set(checkingState());

        UUID id = CheckServerWork.enqueue(getApplication());
        LiveData<WorkInfo> result = WorkManager.getInstance(getApplication()).getWorkInfoByIdLiveData(id);
        checkServerWorkerResult.addSource(result, checkServerWorkerResult::postValue);
    }

    private CheckServerState unknownState() {
        return new CheckServerState(CheckServerState.Status.UNKNOWN, "");
    }

    private CheckServerState checkingState() {
        return new CheckServerState(CheckServerState.Status.CHECKING,
                getApplication().getString(R.string.server_connect_status_checking));
    }

    private CheckServerState errorState(String msg) {
        return new CheckServerState(CheckServerState.Status.ERROR, msg);
    }

    private CheckServerState okState() {
        return new CheckServerState(CheckServerState.Status.OK,
                getApplication().getString(R.string.server_connect_status_ok));
    }
}
