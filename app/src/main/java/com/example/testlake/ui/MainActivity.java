package com.example.testlake.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.example.testlake.R;
import com.example.testlake.TLS.InstallCAdESTestTrustCertExample;
import com.example.testlake.TLSResource.LogCallback;
import com.example.testlake.core.utils.Utils;
import com.example.testlake.databinding.ActivityMainBinding;
import java.io.File;
import java.security.Provider;
import java.security.Security;
import ru.CryptoPro.JCSP.CSPConfig;
import ru.CryptoPro.JCSP.JCSP;
import ru.CryptoPro.JCSP.support.BKSTrustStore;
import ru.CryptoPro.reprov.RevCheck;
import ru.CryptoPro.ssl.android.util.TLSSettings;
import ru.CryptoPro.ssl.android.util.cpSSLConfig;
import ru.cprocsp.ACSP.tools.common.Constants;


public class MainActivity extends AppCompatActivity {
    //Объявляем статическую константу для редактирование URL в диалоговом окне
    private static final String TAG_EDIT_SERVER_URL_DIALOG = "edit_server_url_dialog";
    Button button;
    Button buttonQR;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private EditServerUrlDialog editServerUrlDialog;

    /**
     * Объект для вывода логов и смены статуса.
     */
    private static LogCallback logCallback = null;

    /**
     * Java-провайдер Java CSP.
     */
    private static Provider defaultKeyStoreProvider = null;
    private boolean cAdESCAInstalled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TLSSettings.setDefaultEnableRevocation(false);
        //TLSSettings.setDefaultCRLRevocationOnline(false);
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


        final Context context = this;

        //button = (Button) findViewById(R.id.btnscan);
        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, MatrixScanActivity.class );
                //startActivity(intent);
                //Toast.makeText(getApplicationContext(), "Btn is clicked.", Toast.LENGTH_SHORT).show();

            }
        };



        Button btExecuteButton = (Button) findViewById(R.id.btExamplesExecute);
        // Выполнение примера.
        btExecuteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            InstallCAdESTestTrustCertExample inst = new InstallCAdESTestTrustCertExample(context);
                            inst.getResult();
                           // HttpTSLSreviceJava ht = new HttpTSLSreviceJava();
//                            ht.execute(context);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }

        });

        // присвоим обработчик кнопке OK (btnOk)
//        button.setOnClickListener(oclBtnOk);

        // 2. Инициализация провайдеров: CSP и java-провайдеров
        // (Обязательная часть).

        if (!initProviders()) {

            Log.e(Constants.APP_LOGGER_TAG, "Couldn't initialize CSP.");
            return;

        }
    }

    /************************ Инициализация провайдера ************************/

    @Override
    public void onResume() {

        super.onResume();

        // Необходимо для отображения диалоговых окон
        // ДСЧ, ввода пин-кода и сообщений.

        CSPConfig.registerActivityContext(this);

    }

    /**
     * Инициализация CSP провайдера. Добавление
     * нативного провайдера Java CSP, SSL-провайдера
     * и Revocation-провайдера в список Security.
     *
     * Происходит один раз при инициализации.
     * Возможно только после инициализации в CSPConfig!
     *
     * @return true в случае успешной инициализации.
     */
    private boolean initProviders() {

        //
        // Инициализация провайдера CSP. Должна выполняться
        // один раз в главном потоке приложения, т.к. использует
        // статические переменные.
        //
        // Далее может быть использована версия функции инициализации:
        // 1) расширенная - initEx(): она содержит init() и дополнительно
        // выполняет загрузку java-провайдеров (Java CSP, RevCheck, Java TLS)
        // и настройку некоторых параметров, например, Java TLS;
        // 2) обычная - init(): без загрузки java-провайдеров и настройки
        // параметров.
        //
        // Для совместного использования ГОСТ и не-ГОСТ TLS НЕ следует
        // переопределять свойства System.getProperty(javax.net.*) и
        // Security.setProperty(ssl.*).
        //
        // Ниже используется обычная версия init() функции инициализации
        // и свойства TLS НЕ переопределяются, т.к. в приложении нет примеров
        // работы с УЦ 1.5, которые обращаются к свойствам по умолчанию.
        //
        // 1. Создаем инфраструктуру CSP и копируем ресурсы
        // в папку. В случае ошибки мы, например, выводим окошко
        // (или как-то иначе сообщаем) и завершаем работу.
        //

        int initCode  = CSPConfig.initEx(this);
        if (initCode != CSPConfig.CSP_INIT_OK) {
            Log.d(Constants.APP_LOGGER_TAG, "Инициализация CSP НЕуспешна ");
            //Logger.INSTANCE.append("Error occurred during CSP initiation: " + initCode);
            return false;

        }else
            Log.d(Constants.APP_LOGGER_TAG, "Инициализация CSP успешна ");// if

        // %%% Инициализация остальных провайдеров %%%

        //
        // 2. Загрузка Java CSP (хеш, подпись, шифрование,
        // генерация контейнеров).
        //

        if (Security.getProvider(JCSP.PROVIDER_NAME) == null) {
            Security.addProvider(new JCSP());
            Log.d(Constants.APP_LOGGER_TAG, "Инициализация JCSP успешна ");
        } // if

        //
        // 3. Загрузка Java TLS (TLS).
        //
        // Необходимо переопределить свойства, чтобы
        // использовались менеджеры из cpSSL, а не
        // Harmony.
        //
        // Внимание!
        // Чтобы не мешать не-ГОСТовой реализации, ряд свойств внизу *.ssl не
        // следует переопределять. При этом не исключены проблемы в работе с
        // ГОСТом там, где TLS-реализация клиента обращается к дефолтным алгоритмам
        // реализаций этих factory (особенно: apache http client или HttpsURLConnection
        // без SSLSocketFactory и с System.setProperty(javax.net.*)).
        //
        // Если инициализировать провайдер в CSPConfig с помощью initEx(), то
        // свойства будут включены там, поэтому выше используется упрощенная
        // версия инициализации.
        //
        // Security.setProperty("ssl.KeyManagerFactory.algorithm",   ru.CryptoPro.ssl.android.Provider.KEYMANGER_ALG);
        // Security.setProperty("ssl.TrustManagerFactory.algorithm", ru.CryptoPro.ssl.android.Provider.KEYMANGER_ALG);
        //
        // Security.setProperty("ssl.SocketFactory.provider",       "ru.CryptoPro.ssl.android.SSLSocketFactoryImpl");
        // Security.setProperty("ssl.ServerSocketFactory.provider", "ru.CryptoPro.ssl.android.SSLServerSocketFactoryImpl");
        //

        if (Security.getProvider(ru.CryptoPro.ssl.android.Provider.PROVIDER_NAME) == null) {
            Security.addProvider(new ru.CryptoPro.ssl.android.Provider());
            Log.d(Constants.APP_LOGGER_TAG, "Инициализация Java TLS успешна");
        } // if

        //
        // 4. Провайдер хеширования, подписи, шифрования
        // по умолчанию.
        //

        cpSSLConfig.setDefaultSSLProvider(JCSP.PROVIDER_NAME);

        //
        // 5. Загрузка Revocation Provider (CRL, OCSP).
        //

        if (Security.getProvider(RevCheck.PROVIDER_NAME) == null) {
            Security.addProvider(new RevCheck());
            Log.d(Constants.APP_LOGGER_TAG, "Инициализация RevCheck успешна ");
        } // if

        //
        // 6. Отключаем проверку цепочки штампа времени (CAdES-T),
        // чтобы не требовать него CRL.
        //

        System.setProperty("ru.CryptoPro.CAdES.validate_tsp", "false");

        //
        // 7. Таймауты для CRL на всякий случай.
        //

        System.setProperty("com.sun.security.crl.timeout",  "5");
        System.setProperty("ru.CryptoPro.crl.read_timeout", "5");

        // 8. Включаем возможность онлайновой проверки
        // статуса сертификата.
        //
        // Для TLS проверку цепочки сертификатов другой стороны
        // можно отключить, если создать параметр
        // Enable_revocation_default=false в файле android_pref_store
        // (shared preferences), см.
        // {@link ru.CryptoPro.JCP.tools.pref_store#AndroidPrefStore}.
        TLSSettings.setDefaultEnableRevocation(false);
        System.setProperty("com.sun.security.enableCRLDP", "true");
        System.setProperty("com.ibm.security.enableCRLDP", "true");

        // Отключаем требование проверки сертификата и хоста.
        System.setProperty("tls_prohibit_disabled_validation", "false");

        // 9. Дополнительно задаем путь к хранилищу доверенных
        // сертификатов.
        // Не обязательно, если нет кода, использующего такой
        // способ получения списка доверенных сертификатов.
        //
        // Внимание!
        // Чтобы не мешать не-ГОСТовой реализации, ряд свойств внизу *.ssl и
        // javax.net.* НЕ следует переопределять. Но при этом не исключены проблемы
        // в работе с ГОСТом там, где TLS-реализация клиента обращается к дефолтным
        // алгоритмам реализаций этих factory (особенно: apache http client или
        // HttpsURLConnection без передачи SSLSocketFactory).
        // Здесь эти свойства НЕ включены, т.к. нет примеров работы с УЦ 1.5,
        // использующих алгоритмы по умолчанию.
        //
         final String trustStorePath = getLocalTrustStorePath();
         final String trustStorePassword = String.valueOf(getLocalTrustStorePassword());

         Log.d(Constants.APP_LOGGER_TAG, "Default trust store: " + trustStorePath);

         System.setProperty("javax.net.ssl.trustStoreType", getLocalTrustStoreType());
         System.setProperty("javax.net.ssl.trustStore", trustStorePath);
         System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        return true;

    }

    /**
     * Получение типа хранилища доверенных сертификатов.
     *
     * @return тип хранилища.
     */
    private String getLocalTrustStoreType() {
        return BKSTrustStore.STORAGE_TYPE;
    }

    /**
     * Получение пути к хранилищу доверенных сертификатов.
     *
     * @return путь к хранилищу.
     */
    private String getLocalTrustStorePath() {

        return getApplicationInfo().dataDir + File.separator +
                BKSTrustStore.STORAGE_DIRECTORY + File.separator +
                BKSTrustStore.STORAGE_FILE_TRUST;

    }

    /**
     * Получение пароля к хранилищу доверенных сертификатов.
     *
     * @return пароль к хранилищу.
     */
    private char[] getLocalTrustStorePassword() {
        return BKSTrustStore.STORAGE_PASSWORD;
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
