package com.example.testlake.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testlake.R;
import com.example.testlake.core.utils.Utils;
import com.example.testlake.databinding.DialogEditServerUrlBinding;
import com.example.testlake.storage.SettingsRepository;
// Диалоговое окно для редактирования URL сервера
public class EditServerUrlDialog extends DialogFragment {

    private AppCompatActivity activity;
    private DialogEditServerUrlBinding binding;
    private MainViewModel viewModel;
    private AlertDialog alert;
    private SettingsRepository settings;

    public static EditServerUrlDialog newInstance() {
        EditServerUrlDialog frag = new EditServerUrlDialog();

        Bundle args = new Bundle();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            activity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Back button handle */
        getDialog().setOnKeyListener((DialogInterface dialog, int keyCode, KeyEvent event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return true;
                } else {
                    finish();
                    return true;
                }
            } else {
                return false;
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (activity == null) {
            activity = (AppCompatActivity) getActivity();
        }

        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);

        LayoutInflater i = LayoutInflater.from(activity);
        binding = DataBindingUtil.inflate(i, R.layout.dialog_edit_server_url, null, false);
        settings = SettingsRepository.getInstance(activity.getApplicationContext());

        initLayoutView(binding.getRoot());

        return alert;
    }

    private void initLayoutView(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.edit_server_url)
                .setPositiveButton(R.string.ok, (dialog, which) -> editServerUrl())
                .setNegativeButton(R.string.cancel, (dialog, which) -> finish())
                .setView(view);

        alert = builder.create();

        binding.url.setText(settings.serverUrl());
    }

    private void editServerUrl() {
        String url;
        Editable e = binding.url.getText();
        if (TextUtils.isEmpty(e)) {
            url = "";
        } else {
            url = e.toString();
            if (!url.startsWith(Utils.HTTP_PREFIX)) {
                url = Utils.HTTP_PREFIX + "://" + url;
            }
        }

        settings.serverUrl(url);
        viewModel.url.set(url);

        finish();
    }

    private void finish() {
        alert.dismiss();
    }
}
