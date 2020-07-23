package com.example.testlake.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class SettingsRepository {

    private static SettingsRepository INSTANCE;
    private SharedPreferences pref;

    public static SettingsRepository getInstance(@NonNull Context appContext) {
        if (INSTANCE == null) {
            synchronized (FileRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SettingsRepository(appContext);
                }
            }
        }

        return INSTANCE;
    }

    private SettingsRepository(Context appContext) {
        pref = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    private static class Key {
        static final String SERVER_URL = "pref_key_server_url";
    }

    private static class Default {
        static final String serverUrl = "https://172.20.10.6/test_resource";
    }

    public String serverUrl() {
        return pref.getString(Key.SERVER_URL, Default.serverUrl);
    }

    public void serverUrl(String val) {
        pref.edit()
                .putString(Key.SERVER_URL, val)
                .apply();
    }
}
