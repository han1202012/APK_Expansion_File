package com.example.app;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 处理 APK 扩展文件
 */
public class ExpansionFileManager {

    public final static String TAG = "ExpansionFileManager";

    private Context mContext;

    public ExpansionFileManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取主扩展文件名称 main.6.com.example.app.obb
     *
     * @return
     */
    public String getMainExpansionFileName() {
        return "main." +
                BuildConfig.VERSION_CODE + "." +
                this.mContext.getApplicationInfo().packageName + "." +
                "obb";
    }

    /**
     * 获取补丁扩展文件名称 patch.6.com.example.app.obb
     *
     * @return
     */
    public String getPatchExpansionFileName() {
        return "patch." +
                BuildConfig.VERSION_CODE + "." +
                this.mContext.getApplicationInfo().packageName + "." +
                "obb";
    }

    /**
     * 将 obb 文件移动到内置存储中
     * 将 /sdcard/Android/obb/com.exapmple.app/main.6.com.example.app.obb
     * 移动到 /data/data/com.example.app/cache/main.6.com.example.app.obb 内置存储中
     */
    public void moveObb2Cache() {
        // /sdcard/Android/obb/com.exapmple.app/main.6.com.example.app.obb
        String srcFileName = Environment.getExternalStorageDirectory() +
                "/Android/obb/" +
                this.mContext.getApplicationInfo().packageName +
                "/" +
                getMainExpansionFileName();

        // /data/data/com.example.app/cache/main.6.com.example.app.obb
        String dstFileName = this.mContext.getCacheDir() +
                "/" +
                getMainExpansionFileName();

        Log.i(TAG, "移动文件 : srcFileName = " +
                srcFileName +
                " , dstFileName = " +
                dstFileName);

        File srcFile = new File(srcFileName);
        File dstFile = new File(dstFileName);

        Log.i(TAG, "srcFile = " + srcFile.exists() + " , dstFile = " + dstFile.exists());

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(srcFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dstFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte buffer[] = new byte[1024 * 16];
        int readLen = 0;

        try {
            while ((readLen = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, readLen);
            }
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.i(TAG, "文件移动完成");
        }
    }
}
