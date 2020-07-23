//package com.example.testlake.Scanner;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.content.pm.PackageManager;
//import android.os.Build;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//public abstract class CameraPermissionActivity extends AppCompatActivity {
//    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
//    private static final int CAMERA_PERMISSION_REQUEST = 0;
//
//    private boolean permissionDeniedOnce = false;
//    private boolean paused = true;
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        paused = true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        paused = false;
//    }
//
//    protected boolean hasCameraPermission() {
//        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
//                || checkSelfPermission(CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED);
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    protected void requestCameraPermission() {
//        // Здесь нам нужно запросить разрешение камеры у пользователя.
//        if (!hasCameraPermission()) {
//            if (!permissionDeniedOnce) {
//                requestPermissions(new String[] { CAMERA_PERMISSION }, CAMERA_PERMISSION_REQUEST);
//            }
//
//        } else {
//            // У нас уже есть разрешение.
//            onCameraPermissionGranted();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == CAMERA_PERMISSION_REQUEST) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                permissionDeniedOnce = false;
//                if (!paused) {
//                    //Вызывайте функцию только в том случае, если она не приостановлена ​​- иначе камера не должна использоваться.
//                    onCameraPermissionGranted();
//                }
//            } else {
//                // Пользователь отказал в разрешении.
//                permissionDeniedOnce = true;
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    public abstract void onCameraPermissionGranted();
//}
//