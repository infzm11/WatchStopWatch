package com.mltcode.android.stopwatch;

import android.app.Application;
import com.mltcode.android.stopwatch.crash.CrashHandler;

public class App extends Application {
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }
}
