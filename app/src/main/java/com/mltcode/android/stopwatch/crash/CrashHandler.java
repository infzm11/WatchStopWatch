package com.mltcode.android.stopwatch.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import androidx.core.content.ContextCompat;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler INSTANCE = new CrashHandler();
    private static final String PATH = "/Android/crash/";
    private static String mRelPath;
    private final String TAG = "CrashHandler";
    private DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    private Map<String, String> infos = new HashMap();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        this.mContext = context;
        initFilePath();
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable th) {
        if (handleException(th) || this.mDefaultHandler == null) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e("CrashHandler", "error : ", e);
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
            return;
        }
        this.mDefaultHandler.uncaughtException(thread, th);
    }

    private boolean handleException(Throwable th) {
        if (th == null) {
            return false;
        }
        collectDeviceInfo(this.mContext);
        saveCrashInfo2File(th);
        return true;
    }

    public void collectDeviceInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                this.infos.put("versionName", packageInfo.versionName == null ? "null" : packageInfo.versionName);
                this.infos.put("versionCode", packageInfo.versionCode + "");
                this.infos.put("packageName", context.getPackageName());
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("CrashHandler", "an error occured when collect package info", e);
        }
        for (Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get((Object) null).toString());
                Log.d("CrashHandler", field.getName() + " : " + field.get((Object) null));
            } catch (Exception e2) {
                Log.e("CrashHandler", "an error occured when collect crash info", e2);
            }
        }
    }

    private String saveCrashInfo2File(Throwable th) {
        if (!isGranted("android.permission.WRITE_EXTERNAL_STORAGE")) {
            Log.d("CrashHandler", "saveCrashInfo2File: WRITE_EXTERNAL_STORAGE no permission");
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry next : this.infos.entrySet()) {
            stringBuffer.append(((String) next.getKey()) + "=" + ((String) next.getValue()) + "\n");
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        stringBuffer.append(stringWriter.toString());
        th.printStackTrace();
        try {
            String format = this.formatter.format(new Date());
            String str = "crash-" + format + ".log";
            if (Environment.getExternalStorageState().equals("mounted")) {
                if (mRelPath != null) {
                    if (!mRelPath.equals("")) {
                        File file = new File(mRelPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(mRelPath + str);
                        fileOutputStream.write(stringBuffer.toString().getBytes());
                        fileOutputStream.close();
                    }
                }
                return null;
            }
            writeFileData(str, stringBuffer.toString());
            return str;
        } catch (Exception e) {
            Log.e("CrashHandler", "an error occured while writing file...", e);
            return null;
        }
    }

    private void initFilePath() {
        if (!Environment.getExternalStorageState().equals("mounted")) {
            Log.d("FileUtils", "SD card is not avaiable/writeable right now.");
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getInnerSDCardPath());
        stringBuffer.append(PATH);
        stringBuffer.append(this.mContext.getPackageName());
        stringBuffer.append("/");
        mRelPath = stringBuffer.toString();
    }

    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public void writeFileData(String str, String str2) {
        try {
            FileOutputStream openFileOutput = this.mContext.openFileOutput(str, 0);
            openFileOutput.write(str2.getBytes());
            openFileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isGranted(String str) {
        return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(this.mContext, str) == 0;
    }
}
