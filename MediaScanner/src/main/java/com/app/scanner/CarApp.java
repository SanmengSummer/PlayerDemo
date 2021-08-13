package com.app.scanner;

import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.app.scanner.db.DaoMaster;
import com.app.scanner.db.DaoSession;


/**********************************************
 * Filename： CarApp
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1        wangyi   1) …
 ***********************************************/
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
