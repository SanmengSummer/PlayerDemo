package com.app.scanner.util;

import android.content.Context;
import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    private static final String SEPARATOR = File.separator;

    public static void copyFilesFromRaw(Context context, int id, String fileName, String storagePath){
        InputStream inputStream=context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        readInputStream(storagePath + SEPARATOR + fileName, inputStream);
    }

    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);

                byte[] buffer = new byte[inputStream.available()];
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSymbolName(String name){
        return Pinyin.toPinyin(name,"");
    }

    public static boolean isEmpty(String text){
        return  TextUtils.isEmpty(text)||"null".equals(text);
    }
}
