//package com.example.testlake;
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import com.example.testlake.Scanner.CameraPermissionActivity;
//import com.example.testlake.Scanner.ScanResult;
//import com.scandit.datacapture.barcode.data.Symbology;
//import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking;
//import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingListener;
//import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSession;
//import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSettings;
//import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
//import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay;
//import com.scandit.datacapture.core.capture.DataCaptureContext;
//import com.scandit.datacapture.core.data.FrameData;
//import com.scandit.datacapture.core.source.Camera;
//import com.scandit.datacapture.core.source.CameraSettings;
//import com.scandit.datacapture.core.source.FrameSourceState;
//import com.scandit.datacapture.core.source.VideoResolution;
//import com.scandit.datacapture.core.ui.DataCaptureView;
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Locale;
//
//public class MatrixScanActivity extends CameraPermissionActivity
//        implements BarcodeTrackingListener {
//ImageView imageView;
//    // Введите здесь свой лицензионный ключ.
//    //Ваш лицензионный ключ Scandit доступен через веб-аккаунт Scandit SDK
//   // public static final String SCANDIT_LICENSE_KEY = "Abwu8CisNrAxLhGzHzFChC4x3h3gN0BBXDoCvalHQ3KXBSB9RmtqTw1XPQ9nR9Lqv2DKdGweA0rBf06xfGi6Hht31we8b+v4diwPq3FEQFp0Zj89h31G5nB/1Mb0JURz1UEV36gfOZQWkOYEZh1CbW0Pc/b4YXw1TnYtXoZcfu7fy774T7RN9PW2fSGnT5yvsXhHnEiMnZw7iNLaN5oJtPgpfYRFAZ/4voPaeLHJMTkUjtGgzo/VU/YohSZ6vmcyWrdS8wllCqAOgHpDDcVXna6wyRTpvxnviz/M+ULHLB/djXCZ8A7arzb2R5qX03MTMwol559OrvLjrcx+FXIJAIHFKbeLHQqBXFVxgMurNXeracilDKA9eouAiJUjfahKXYq+lFKxXaGh4Fyqf+UkSQlwc3VWB1pZzDr91I7Ba8Fxdz/V7NH488okqWNGgCSy+CYzC7Zeshtro66/qY5jBbhzHWV50bp1QHx/VTG2czjwEKnBV4QQD22rkM5sb6DFkNJ9Np+omcTsFbGOonoT6NVGNgT9V9yvtANlOHv4ROuSyHYizpli7nvI9PmnEJcUM63xlZVt5/S66OqMBXggKEId/yxQXpHJ2aQmr10aHVcaWULwV1UKnhSZCxv6kFjz5/Uzyr9ft9tPZk/SdYwlyStqB1dXipeU3wyZpJN1LAaieBIOrJ4q8IdMlUHrHsan48oQF9FWSDgAJYPQocISW+D3OfJJRsQwXmmzv3uNA7I7kMkLijr7Up+jxqaz/LCCQs+woKzoDAodgnaz9SJtBBgL+h9xj0aN8LG8rziTHIzzXn8/dZnS/g==";
//    File photoFile = null;
//    public static final int REQUEST_CODE_SCAN_RESULTS = 1;
//    private String imageFilePath = "";
//    public static final int REQUEST_IMAGE = 100;
//    private Camera camera;
//    Uri photoUri = null;
//    private BarcodeTracking barcodeTracking;
//    private DataCaptureContext dataCaptureContext;
//    private static final String TAG = "myLogs";
//    private final HashSet<ScanResult> scanResults = new HashSet<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_matrix_scan);
//
//        // Инициализируйте и запустите распознавание штрих-кода.
//        initialize();
//
//        Button doneButton = findViewById(R.id.done_button);
//        doneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                synchronized (scanResults) {
//                    Log.i(TAG, "КНОПКА НАЖАТА!!!!!!!!!!!!!!");
//                    // Показать новый экран со списком всех сканированных штрих-кодов.
//                    Intent intent = ResultsActivity.getIntent(MatrixScanActivity.this, scanResults);
//                    startActivityForResult(intent, REQUEST_CODE_SCAN_RESULTS);
//                }
//            }
//        });
//        Button doneButton2 = findViewById(R.id.done_button2);
//
//        doneButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    openCameraIntent();
//                }
//                else
//                {
//                    captureImage2();
//                }
//            }
//
//        });
//    }
//    private void openCameraIntent() {
//        if(ContextCompat.checkSelfPermission(MatrixScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MatrixScanActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//        }else {
//            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (pictureIntent.resolveActivity(getPackageManager()) != null) {
//                // Создание файла, куда должна идти фотография
//                try {
//
//                    photoFile = createImageFile();
//                    displayMessage(getBaseContext(), photoFile.getAbsolutePath());
//                    Log.i("Mayank", photoFile.getAbsolutePath());
//
//                    // Продолжить, только если файл был успешно создан
//                    if (photoFile != null) {
//                        Uri photoURI = FileProvider.getUriForFile(this,
//                                "com.example.testlake.fileprovider",
//                                photoFile);
//                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                        startActivityForResult(pictureIntent, REQUEST_IMAGE);
//                    }
//                } catch (Exception ex) {
//                    //Произошла ошибка при создании файла
//                    displayMessage(getBaseContext(), ex.getMessage().toString());
//                }
//
//
//            } else {
//                displayMessage(getBaseContext(), "Nullll");
//            }
//        }
//
//        }
//
//    private void captureImage2() {
//
//        try {
//            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            photoFile = createImageFile4();
//            if(photoFile!=null)
//            {
//                displayMessage(getBaseContext(),photoFile.getAbsolutePath());
//                Log.i("Mayank",photoFile.getAbsolutePath());
//                Uri photoURI  = Uri.fromFile(photoFile);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(cameraIntent, REQUEST_IMAGE);
//            }
//        }
//        catch (Exception e)
//        {
//            displayMessage(getBaseContext(),"Camera is not available."+e.toString());
//        }
//    }
//    private static final String IMAGE_DIRECTORY_NAME = "VLEMONN";
//    private File createImageFile4()
//    {
//        //Расположение внешней SDCard
//        File mediaStorageDir = new File(
//                Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                IMAGE_DIRECTORY_NAME);
//        // Создаем каталог хранения, если он не существует
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                displayMessage(getBaseContext(),"Unable to create directory.");
//                return null;
//            }
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
//                Locale.getDefault()).format(new Date());
//        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");
//
//        return mediaFile;
//
//    }
//
//
//
//    private void displayMessage(Context context, String message)
//    {
//        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
//    }
//    private File createImageFile() throws IOException {
//        //Создать имя файла изображения
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* префикс */
//                ".jpg",         /* суфикс */
//                storageDir      /* директория */
//        );
//
//        //Сохранить файл: путь для использования с намерениями ACTION_VIEW
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//    private void initialize() {
//        //Создается контекст захвата данных, используя ваш лицензионный ключ.
//        //dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);
//
//        // Используется камера по умолчанию и устанавливается в качестве источника кадра контекста.
//        // Камера выключена по умолчанию и должна быть включена, чтобы начать потоковую передачу кадров к данным.
//        // захватить контекст для распознавания.
//        camera = Camera.getDefaultCamera();
//        if (camera != null) {
//            //Используйте рекомендуемые настройки камеры для режима BarcodeTracking.
//            CameraSettings cameraSettings = BarcodeTracking.createRecommendedCameraSettings();
//            // Настройте параметры камеры - установите разрешение Full HD.
//            cameraSettings.setPreferredResolution(VideoResolution.FULL_HD);
//            camera.applySettings(cameraSettings);
//            dataCaptureContext.setFrameSource(camera);
//        } else {
//            throw new IllegalStateException("Sample depends on a camera, which failed to initialize.");
//        }
//
//        // Процесс отслеживания штрих-кода настраивается через настройки отслеживания штрих-кода
//        // которые затем применяются к экземпляру отслеживания штрих-кода, который управляет отслеживанием штрих-кода.
//        BarcodeTrackingSettings barcodeTrackingSettings = new BarcodeTrackingSettings();
//
//        // В экземпляре настроек изначально отключены все типы штрих-кодов (символов).
//        //В этом примере мы включили очень набор символов.
//        // поскольку каждая дополнительная включенная символика влияет на время обработки.
//        HashSet<Symbology> symbologies = new HashSet<>();
//        symbologies.add(Symbology.EAN13_UPCA);
//        symbologies.add(Symbology.EAN8);
//        symbologies.add(Symbology.UPCE);
//        symbologies.add(Symbology.CODE39);
//        symbologies.add(Symbology.CODE128);
//        barcodeTrackingSettings.enableSymbologies(symbologies);
//
//
//        // Создать отслеживание штрих-кода и прикрепить к контексту.
//        barcodeTracking = BarcodeTracking.forDataCaptureContext(dataCaptureContext, barcodeTrackingSettings);
//
//        // Зарегистрируйте себя в качестве слушателя, чтобы получать информацию о отслеживаемых штрих-кодах.
//        barcodeTracking.addListener(this);
//
//        // Для визуализации текущего процесса отслеживания штрих-кода на экране настройте представление сбора данных.
//        // это делает предварительный просмотр камеры. Представление должно быть связано с контекстом захвата данных.
//        DataCaptureView dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);
//
//        //
//        // Добавьте наложение отслеживания штрих-кода в представление захвата данных, чтобы отобразить отслеживаемые штрих-коды на
//        // верхней части предварительного просмотра видео. Это необязательно, но рекомендуется для лучшей визуальной обратной связи.
//        BarcodeTrackingBasicOverlay.newInstance(barcodeTracking, dataCaptureView);
//
//        // Добавьте DataCaptureView в контейнер.
//        FrameLayout container = findViewById(R.id.data_capture_view_container);
//        container.addView(dataCaptureView);
//    }
//
//    @Override
//    protected void onPause() {
//        pauseFrameSource();
//        super.onPause();
//    }
//
//    private void pauseFrameSource() {
//        //Выключите камеру, чтобы остановить потоковую передачу кадров.
//        // Камера останавливается асинхронно и для полного выключения потребуется некоторое время.
//        // Пока он полностью не остановлен, все еще возможно получить дальнейшие результаты, следовательно
//        //также неплохо сначала отключить отслеживание штрих-кода.
//        barcodeTracking.setEnabled(false);
//        camera.switchToDesiredState(FrameSourceState.OFF, null);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //
//        //Проверьте разрешение камеры и запросите его, если оно еще не было предоставлено.
//        // Как только мы получим разрешение, будет вызван метод onCameraPermissionGranted ().
//        requestCameraPermission();
//    }
//
//    @Override
//    public void onCameraPermissionGranted() {
//        resumeFrameSource();
//    }
//
//    private void resumeFrameSource() {
//        // Включите камеру, чтобы начать потоковую передачу кадров.
//        // Камера запускается асинхронно и для полного включения потребуется некоторое время.
//        barcodeTracking.setEnabled(true);
//        camera.switchToDesiredState(FrameSourceState.ON, null);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_SCAN_RESULTS && resultCode == ResultsActivity.RESULT_CODE_CLEAN) {
//            if(requestCode == REQUEST_IMAGE && requestCode == RESULT_OK){
//                Bitmap mybitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                imageView.setImageBitmap(mybitmap);
//            }
//            synchronized (scanResults) {
//                scanResults.clear();
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    String mCurrentPhotoPath;
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 0) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                openCameraIntent();
//            }
//        }
//    }
//
//    @Override
//    public void onObservationStarted(@NonNull BarcodeTracking barcodeTracking) {
//    }
//
//    @Override
//    public void onObservationStopped(@NonNull BarcodeTracking barcodeTracking) {
//    }
//
//    // Эта функция вызывается всякий раз, когда объекты обновляются, и это правильное место, чтобы реагировать на результаты отслеживания.
//    @Override
//    public void onSessionUpdated(
//            @NonNull BarcodeTracking mode,
//            @NonNull BarcodeTrackingSession session,
//            @NonNull FrameData data
//    ) {
//        synchronized (scanResults) {
//            for (TrackedBarcode trackedBarcode : session.getAddedTrackedBarcodes()) {
//                scanResults.add(new ScanResult(trackedBarcode.getBarcode()));
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        dataCaptureContext.removeMode(barcodeTracking);
//        super.onDestroy();
//    }
//}
//