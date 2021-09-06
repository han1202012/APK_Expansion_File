package com.example.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    /**
     * 申请权限的 Activity 界面
     */
    private Activity mActivity;

    /**
     * "不再询问" 后的引导对话框
     */
    private AlertDialog mAlertDialog;

    /**
     * 申请权限的请求码, 要求必须 >0
     */
    public final int REQUEST_CODE = 100;

    /**
     * 需要申请的权限
     */
    protected String[] mRequestPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public PermissionManager(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 请求动态权限
     *
     * @return
     */
    public boolean requestPermission() {
        // Android 6.0 ( API 23 ) 才启用动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 用户是否不同意权限, 只要有 1 个不同意, 则为 true, 默认 false
            boolean isDisagree = false;

            // 判定是否有权限未获取
            for (int i = 0; i < mRequestPermissions.length; i++) {
                if (ContextCompat.checkSelfPermission(
                        mActivity,
                        mRequestPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    isDisagree = true;
                }
            }

            if (isDisagree) {
                // 存在权限没有通过，需要申请
                ActivityCompat.requestPermissions(mActivity, mRequestPermissions, REQUEST_CODE);
                return false;
            } else {
                // 所有权限都已同意
                return true;
            }
        } else {
            // 6.0 以下默认有动态权限
            return true;
        }
    }

    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (REQUEST_CODE != requestCode) {
            return;
        }

        // 权限是否赋予完毕, 如果有任意一个没有同意, 则判定权限申请失败
        boolean allAgree = true;

        // 遍历 grantResults 数组, 判定哪个权限被拒绝了
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == -1) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[i])) {
                    // 被用户拒绝了, 但是还可以申请, 说明没有设置 "不再询问" 选项
                } else {
                    // 被用户拒绝了, 不能弹出, 说明用户设置了 "不再询问" 选项
                    showDialog();
                }
                allAgree = false;
            }
        }

        // 如果都同意, 则执行相关操作
        if (allAgree) {
            Toast.makeText(mActivity, "权限设置完毕, 执行相关操作", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 用户选择 "不再询问" 后的提示方案
     */
    protected void showDialog() {
        // 不管同意/拒绝 , 只弹出一次
        if (mAlertDialog != null){
            return;
        }

        mAlertDialog = new AlertDialog.Builder(mActivity)
                .setMessage("手动设置权限")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到设置界面
                        Intent intent = new Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:com.example.permission")
                        );
                        mActivity.startActivity(intent);

                        mAlertDialog.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.cancel();
                    }
                })
                .create();
        mAlertDialog.show();
    }
}
