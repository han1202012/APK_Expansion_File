package com.example.app;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static final String TAG = "ZipUtils";

    /**
     * 删除文件, 如果有目录, 则递归删除
     */
    private static void deleteFile(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f: files) {
                deleteFile(f);
            }
        }else{
            file.delete();
        }
    }

    /**
     * 解压文件
     * @param zip 被解压的压缩包文件
     * @param dir 解压后的文件存放目录
     */
    public static void unZipApk(File zip, File dir) {
        try {
            // 如果存放文件目录存在, 删除该目录
            deleteFile(dir);
            // 获取 zip 压缩包文件
            ZipFile zipFile = new ZipFile(zip);
            // 获取 zip 压缩包中每一个文件条目
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            // 遍历压缩包中的文件
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                // zip 压缩包中的文件名称 或 目录名称
                String name = zipEntry.getName();
                // 如果 apk 压缩包中含有以下文件 , 这些文件是 V1 签名文件保存目录 , 不需要解压 , 跳过即可
                if (name.equals("META-INF/CERT.RSA") || name.equals("META-INF/CERT.SF") || name
                        .equals("META-INF/MANIFEST.MF")) {
                    continue;
                }
                // 如果该文件条目 , 不是目录 , 说明就是文件
                if (!zipEntry.isDirectory()) {
                    File file = new File(dir, name);
                    //创建目录
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    // 向刚才创建的目录中写出文件
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = zipFile.getInputStream(zipEntry);
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                }
            }

            // 关闭 zip 文件
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压文件
     * @param zip 被解压的压缩包文件
     * @param dir 解压后的文件存放目录
     */
    public static void unZip(File zip, File dir) {
        try {
            // 如果存放文件目录存在, 删除该目录
            deleteFile(dir);
            // 获取 zip 压缩包文件
            ZipFile zipFile = new ZipFile(zip);
            // 获取 zip 压缩包中每一个文件条目
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            // 遍历压缩包中的文件
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                // zip 压缩包中的文件名称 或 目录名称
                String name = zipEntry.getName();

                Log.i(TAG, "解压文件 " + name);

                // 如果该文件条目 , 不是目录 , 说明就是文件
                if (!zipEntry.isDirectory()) {
                    File file = new File(dir, name);
                    //创建目录
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    // 向刚才创建的目录中写出文件
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = zipFile.getInputStream(zipEntry);
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                }
            }

            // 关闭 zip 文件
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.i(TAG, "文件解压完成");
        }
    }

    /**
     * 压缩目录为zip
     * @param dir 待压缩目录
     * @param zip 输出的zip文件
     * @throws Exception
     */
    public static void zip(File dir, File zip) throws Exception {
        zip.delete();
        // 对输出文件做CRC32校验
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(
                zip), new CRC32());
        ZipOutputStream zos = new ZipOutputStream(cos);
        //压缩
        compress(dir, zos, "");
        zos.flush();
        zos.close();
    }

    /**
     * 添加目录/文件 至zip中
     * @param srcFile 需要添加的目录/文件
     * @param zos   zip输出流
     * @param basePath  递归子目录时的完整目录 如 lib/x86
     * @throws Exception
     */
    private static void compress(File srcFile, ZipOutputStream zos,
                                 String basePath) throws Exception {
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File file : files) {
                // zip 递归添加目录中的文件
                compress(file, zos, basePath + srcFile.getName() + "/");
            }
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    private static void compressFile(File file, ZipOutputStream zos, String dir)
            throws Exception {
        // temp/lib/x86/libdn_ssl.so
        String fullName = dir + file.getName();
        // 需要去掉temp
        String[] fileNames = fullName.split("/");
        //正确的文件目录名 (去掉了temp)
        StringBuffer sb = new StringBuffer();
        if (fileNames.length > 1){
            for (int i = 1;i<fileNames.length;++i){
                sb.append("/");
                sb.append(fileNames[i]);
            }
        }else{
            sb.append("/");
        }
        //添加一个zip条目
        ZipEntry entry = new ZipEntry(sb.substring(1));
        zos.putNextEntry(entry);
        //读取条目输出到zip中
        FileInputStream fis = new FileInputStream(file);
        int len;
        byte data[] = new byte[2048];
        while ((len = fis.read(data, 0, 2048)) != -1) {
            zos.write(data, 0, len);
        }
        fis.close();
        zos.closeEntry();
    }

}