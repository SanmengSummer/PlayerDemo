package com.app.scanner;

import android.app.Application;
import android.content.Intent;


public class CarApp extends Application {
    public static CarApp contextApp;

    @Override
    public void onCreate() {
        super.onCreate();
        contextApp=this;
        Intent intent = new Intent(this, ScannerService.class);
        startService(intent);
    }

    public static CarApp getInstance(){
        return contextApp;
    }
}
