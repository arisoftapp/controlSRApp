package com.example.arisoft.controlsr;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class barcode extends AppCompatActivity implements  ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    Context contexto=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        mScannerView=(ZXingScannerView)findViewById(R.id.zxscan);
    }
    @Override
    public void onBackPressed(){
        //do your stuff }

        mScannerView.stopCamera();
        /*
        Intent i=new Intent(contexto,MainActivity.class);
        startActivity(i);
        finish();
        */
        super.onBackPressed();
        this.finish();
    }
    @Override
    public void onResume() {

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();// Stop camera on pause

    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("camara", rawResult.getText()); // Prints scan results
        Log.v("camara", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        // If you would like to resume scanning, call this method below:
        MediaPlayer mp = MediaPlayer.create(this, R.raw.beepmicro);
        mp.start();

        //mScannerView.resumeCameraPreview(this);

        Intent i=new Intent(contexto,MainActivity.class);
        i.putExtra("barcode",""+rawResult.getText().trim());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        //super.onBackPressed();
        finish();

        mScannerView.stopCamera();

    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
