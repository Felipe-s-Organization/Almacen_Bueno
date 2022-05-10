package com.example.almacen_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Escanear extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView vistaScanner;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        vistaScanner = new ZXingScannerView(this);
        // Hacer que el contenido de la actividad sea el escaner
        setContentView(vistaScanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        // El "manejador" del resultado es esta misma clase, por eso implementamos ZXingScannerView.ResultHandler
        vistaScanner.setResultHandler(this);
        vistaScanner.startCamera(); // Comenzar la cámara en onResume
    }

    @Override
    public void onPause() {
        super.onPause();
        vistaScanner.stopCamera(); // Pausar en onPause
    }

    @Override
    public void handleResult(Result result) {
        vistaScanner.resumeCameraPreview(this);
        String resultado = result.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("codigo", resultado);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

}