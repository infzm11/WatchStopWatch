package com.mltcode.android.stopwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.mltcode.android.stopwatch.StopWatchService;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends SlidrBaseActivity implements View.OnClickListener {
    private static final String KEY_FRIST_ID = "frist_id";
    /* access modifiers changed from: private */
    public static TextView continuesTv;
    public static Timer mTimer;
    /* access modifiers changed from: private */
    public static TextView meteringTv;
    /* access modifiers changed from: private */
    public static TextView pauseTv;
    /* access modifiers changed from: private */
    public static TextView resetTv;
    /* access modifiers changed from: private */
    public static TextView startTv;
    public static StopWatchService stopWatchService;
    public static Handler timeHandler = new Handler() {
        public void handleMessage(Message message) {
            String str = (String) message.obj;
            if (str.equals("00:00:00")) {
                MainActivity.startTv.setVisibility(View.VISIBLE);
                MainActivity.continuesTv.setVisibility(View.GONE);
                MainActivity.resetTv.setVisibility(View.GONE);
                MainActivity.meteringTv.setVisibility(View.GONE);
                MainActivity.pauseTv.setVisibility(View.GONE);
            } else {
                if (!MainActivity.stopWatchService.isAlive) {
                    MainActivity.continuesTv.setVisibility(View.VISIBLE);
                    MainActivity.resetTv.setVisibility(View.VISIBLE);
                    MainActivity.meteringTv.setVisibility(View.GONE);
                    MainActivity.pauseTv.setVisibility(View.GONE);
                    MainActivity.startTv.setVisibility(View.GONE);
                } else {
                    MainActivity.pauseTv.setVisibility(View.VISIBLE);
                    MainActivity.meteringTv.setVisibility(View.VISIBLE);
                    MainActivity.continuesTv.setVisibility(View.GONE);
                    MainActivity.resetTv.setVisibility(View.GONE);
                    MainActivity.startTv.setVisibility(View.GONE);
                    MainActivity.mTimer = new Timer();
                }
                MainActivity.timerCounterTv.setText(str);
            }
            super.handleMessage(message);
        }
    };
    public static ArrayList<String> timeLables = new ArrayList<>();
    /* access modifiers changed from: private */
    public static TextView timerCounterTv;
    /* access modifiers changed from: private */
    public MainActivity activity;
    /* access modifiers changed from: private */
    public int clo = 0;
    long currentTime = 0;
    private String isFrist = "1";
    private CommonAdapter<String> mAdapter;
    /* access modifiers changed from: private */
    public Context mContext;
    public StopServiceConnection mStopServiceConnection;
    private ListView recordListview;
    long startTime;
    String timeString = "00:00:00";

    private class StopServiceConnection implements ServiceConnection {
        public void onServiceDisconnected(ComponentName componentName) {
        }

        private StopServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainActivity.stopWatchService = ((StopWatchService.StopBinder) iBinder).getService();
            MainActivity.timeLables = MainActivity.stopWatchService.timeLables;
            if (MainActivity.timeLables != null && MainActivity.timeLables.size() > 0) {
                MainActivity.this.initAdapter();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        this.mContext = this;
        this.activity = this;
        initView();
        SPUtils.setStringValue(this.mContext, KEY_FRIST_ID, this.isFrist);
    }

    private void initView() {
        timerCounterTv = (TextView) findViewById(R.id.timer_counter_txt);
        startTv = (TextView) findViewById(R.id.tv_start);
        startTv.setOnClickListener(this);
        continuesTv = (TextView) findViewById(R.id.tv_continues);
        continuesTv.setOnClickListener(this);
        pauseTv = (TextView) findViewById(R.id.tv_pause);
        pauseTv.setOnClickListener(this);
        meteringTv = (TextView) findViewById(R.id.tv_metering);
        meteringTv.setOnClickListener(this);
        resetTv = (TextView) findViewById(R.id.tv_reset);
        resetTv.setOnClickListener(this);
        this.recordListview = (ListView) findViewById(R.id.record_listview);
        Log.e("timeLables", "timeLables page:" + timeLables);
        startService();
        bindService();
    }

    public void initAdapter() {
        this.mAdapter = new CommonAdapter<String>(this.mContext, timeLables, R.layout.item_record) {
            public void convert(ViewHolder viewHolder, String str) {
                Log.e("timeLables", "timeLables adapter:" + MainActivity.timeLables);
                viewHolder.setTextView((int) R.id.record_time_tv, str);
            }
        };
        this.recordListview.setAdapter(this.mAdapter);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i != 66) {
            return super.onKeyUp(i, keyEvent);
        }
        Log.e("onKeyUp", "onKeyUp KEYCODE_ENTER");
        this.isFrist = SPUtils.getStringValue(this.mContext, KEY_FRIST_ID);
        if ("1".equals(this.isFrist)) {
            enter();
            this.isFrist = "2";
            SPUtils.setStringValue(this.mContext, KEY_FRIST_ID, this.isFrist);
            return true;
        } else if ("2".equals(this.isFrist)) {
            meter();
            this.isFrist = "2";
            SPUtils.setStringValue(this.mContext, KEY_FRIST_ID, this.isFrist);
            return true;
        } else if (!"3".equals(this.isFrist)) {
            return true;
        } else {
            continues();
            this.isFrist = "2";
            SPUtils.setStringValue(this.mContext, KEY_FRIST_ID, this.isFrist);
            return true;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_continues:
                continues();
                return;
            case R.id.tv_metering:
                meter();
                return;
            case R.id.tv_pause:
                stopWatchService.pauseTime();
                continuesTv.setVisibility(View.VISIBLE);
                resetTv.setVisibility(View.VISIBLE);
                meteringTv.setVisibility(View.GONE);
                pauseTv.setVisibility(View.GONE);
                this.isFrist = "3";
                SPUtils.setStringValue(this.mContext, KEY_FRIST_ID, this.isFrist);
                flicker();
                return;
            case R.id.tv_reset:
                stopWatchService.resetTime();
                timerCounterTv.setText("00:00.00");
                pauseTv.setVisibility(View.GONE);
                meteringTv.setVisibility(View.GONE);
                continuesTv.setVisibility(View.GONE);
                resetTv.setVisibility(View.GONE);
                startTv.setVisibility(View.VISIBLE);
                if (timeLables.size() > 0) {
                    this.mAdapter.notifyDataSetChanged();
                }
                timerCounterTv.setTextColor(this.mContext.getResources().getColor(R.color.timerColor));
                this.isFrist = "1";
                SPUtils.setStringValue(this.mContext, KEY_FRIST_ID, this.isFrist);
                mTimer.cancel();
                return;
            case R.id.tv_start:
                stopWatchService.startTime();
                startTv.setVisibility(View.GONE);
                pauseTv.setVisibility(View.VISIBLE);
                meteringTv.setVisibility(View.VISIBLE);
                continuesTv.setVisibility(View.GONE);
                resetTv.setVisibility(View.GONE);
                return;
            default:
                return;
        }
    }

    public void continues() {
        stopWatchService.continuesTime();
        pauseTv.setVisibility(View.VISIBLE);
        meteringTv.setVisibility(View.VISIBLE);
        continuesTv.setVisibility(View.GONE);
        resetTv.setVisibility(View.GONE);
        timerCounterTv.setTextColor(this.mContext.getResources().getColor(R.color.timerColor));
        mTimer.cancel();
    }

    public void meter() {
        stopWatchService.meteringTime();
        timeLables = stopWatchService.timeLables;
        Log.e("meter", "meter: " + new Gson().toJson((Object) timeLables));
        if (timeLables.size() <= 0 || timeLables == null) {
            this.mAdapter.notifyDataSetChanged();
        } else {
            initAdapter();
        }
    }

    public void enter() {
        if (!stopWatchService.isRun) {
            stopWatchService.startTime();
            startTv.setVisibility(View.GONE);
            pauseTv.setVisibility(View.VISIBLE);
            meteringTv.setVisibility(View.VISIBLE);
            continuesTv.setVisibility(View.GONE);
            resetTv.setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        unbindService(this.mStopServiceConnection);
    }

    public void flicker() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public void run() {
                MainActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (MainActivity.this.clo == 0) {
                            int unused = MainActivity.this.clo = 1;
                            MainActivity.timerCounterTv.setTextColor(0);
                            return;
                        }
                        int unused2 = MainActivity.this.clo = 0;
                        MainActivity.timerCounterTv.setTextColor(MainActivity.this.mContext.getResources().getColor(R.color.timerColor));
                    }
                });
            }
        }, 1, 500);
    }

    public void startService() {
        this.mContext.startService(new Intent(this.mContext, StopWatchService.class));
    }

    private void bindService() {
        Intent intent = new Intent(this.mContext, StopWatchService.class);
        this.mStopServiceConnection = new StopServiceConnection();
        this.mContext.bindService(intent, this.mStopServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
