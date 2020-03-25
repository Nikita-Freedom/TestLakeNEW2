package com.example.testlake.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.testlake.MatrixScanActivity;
import com.example.testlake.R;
import com.example.testlake.core.utils.Utils;
import com.example.testlake.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    //Объявляем статическую константу для редактирование URL в диалоговом окне
    private static final String TAG_EDIT_SERVER_URL_DIALOG = "edit_server_url_dialog";
    Button button;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private EditServerUrlDialog editServerUrlDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        editServerUrlDialog = (EditServerUrlDialog)fm.findFragmentByTag(TAG_EDIT_SERVER_URL_DIALOG);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(viewModel);

        binding.editUrl.setOnClickListener((v) -> showEditServerUrlDialog());
        binding.checkUrl.setOnClickListener((v) -> viewModel.forceCheckServer());

        viewModel.observeCheckingServer().observe(this, viewModel::handleCheckingServer);
        viewModel.forceCheckServer();
        button = (Button) findViewById(R.id.btnscan);
        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MatrixScanActivity.class );
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Btn is clicked.", Toast.LENGTH_SHORT).show();

            }
        };

        // присвоим обработчик кнопке OK (btnOk)
        button.setOnClickListener(oclBtnOk);

    }

    // Показываем диалоговое окно
    private void showEditServerUrlDialog() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(TAG_EDIT_SERVER_URL_DIALOG) == null) {
            editServerUrlDialog = EditServerUrlDialog.newInstance();
            editServerUrlDialog.show(fm, TAG_EDIT_SERVER_URL_DIALOG);
        }
    }

    public static int getServerStatusColor(@NonNull Context context, MainViewModel.CheckServerState state) {
        switch (state.status) {
            case OK:
                return ContextCompat.getColor(context, R.color.ok);
            case ERROR:
                return ContextCompat.getColor(context, R.color.error);
            default:
                return Utils.getAttributeColor(context, android.R.attr.textColorSecondary);
        }
    }
}
