package com.mltcode.android.stopwatch;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;

public class StopWatchService extends Service {
    long currentTime = 0;
    public boolean isAlive = false;
    public boolean isRun = false;
    /* access modifiers changed from: private */
    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long elapsedRealtime = (StopWatchService.this.currentTime + SystemClock.elapsedRealtime()) - StopWatchService.this.startTime;
            Log.e("StopWatchService", "run time:" + elapsedRealtime);
            String timeText = Stopwatches.getTimeText(StopWatchService.this.getApplicationContext(), elapsedRealtime, false);
            StopWatchService.this.timeString = timeText;
            Log.e("StopWatchService", "run timeString:" + StopWatchService.this.timeString);
            Message message = new Message();
            message.obj = timeText;
            MainActivity.timeHandler.sendMessage(message);
            MainActivity.timeHandler.postDelayed(StopWatchService.this.mUpdateTimeTask, 50);
            StopWatchService.this.isAlive = true;
            StopWatchService.this.isRun = true;
        }
    };
    long startTime;
    ArrayList<String> timeLables = new ArrayList<>();
    String timeString = "00:00:00";

    public class StopBinder extends Binder {
        public StopBinder() {
        }

        public StopWatchService getService() {
            return StopWatchService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return new StopBinder();
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Log.e("onStartCommand", "onStartCommand");
        if (this.isRun) {
            Message message = new Message();
            message.obj = this.timeString;
            MainActivity.timeHandler.sendMessage(message);
        }
        return super.onStartCommand(intent, i, i2);
    }

    public void startTime() {
        this.startTime = SystemClock.elapsedRealtime();
        MainActivity.timeHandler.postDelayed(this.mUpdateTimeTask, 100);
        this.isAlive = true;
        Log.e("startTime", "startTime:" + this.startTime);
    }

    public void continuesTime() {
        this.startTime = SystemClock.elapsedRealtime();
        MainActivity.timeHandler.postDelayed(this.mUpdateTimeTask, 100);
        this.isAlive = true;
        Log.e("continuesTime", "continuesTime:" + this.startTime);
    }

    public void meteringTime() {
        StringBuilder sb;
        String str;
        ArrayList<String> arrayList = this.timeLables;
        if (this.timeLables.size() < 9) {
            sb = new StringBuilder();
            str = "0";
        } else {
            sb = new StringBuilder();
            str = "";
        }
        sb.append(str);
        sb.append(this.timeLables.size() + 1);
        sb.append(getResources().getString(R.string.number));
        sb.append("  ");
        sb.append(this.timeString);
        arrayList.add(0, sb.toString());
        Log.e("timeLables", "timeLables:" + new Gson().toJson((Object) this.timeLables));
    }

    public void pauseTime() {
        this.currentTime = (long) ((int) ((this.currentTime + SystemClock.elapsedRealtime()) - this.startTime));
        MainActivity.timeHandler.removeCallbacks(this.mUpdateTimeTask);
        this.isAlive = false;
        Log.e("pauseTime", "pauseTime:" + this.currentTime);
    }

    public void resetTime() {
        MainActivity.timeHandler.removeCallbacks(this.mUpdateTimeTask);
        this.timeLables.removeAll(this.timeLables);
        this.isAlive = false;
        this.isRun = false;
        this.currentTime = 0;
        Log.e("resetTime", "resetTime");
        Log.e("timeLables", "timeLables:" + new Gson().toJson((Object) this.timeLables));
    }
}
