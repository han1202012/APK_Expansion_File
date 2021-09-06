package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /**
     * 动态权限管理
     */
    private PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionManager = new PermissionManager(MainActivity.this);
        //mPermissionManager.requestPermission();

        findViewById(R.id.obb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 移动文件
                ExpansionFileManager manager = new ExpansionFileManager(MainActivity.this);
                manager.moveObb2Cache();

                // 解压文件
                File cacheObb = new File(getCacheDir(), manager.getMainExpansionFileName());
                File unzipDir = new File(getCacheDir(), "unzip");
                ZipUtils.unZip(cacheObb, unzipDir);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionManager.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
    }
}