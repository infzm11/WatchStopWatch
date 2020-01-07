package com.mltcode.android.stopwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.mltcode.android.stopwatch.StopWatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainPagerAdapter extends PagerAdapter {
    private static final String KEY_FRIST_ID = "frist_id";
    /* access modifiers changed from: private */
    public static TextView continuesTv;
    /* access modifiers changed from: private */
    public static TextView lapNumberTv;
    /* access modifiers changed from: private */
    public static Context mContext;
    public static Timer mTimer;
    /* access modifiers changed from: private */
    public static TextView meteringTv;
    /* access modifiers changed from: private */
    public static LinearLayout noMeteringLayout;
    /* access modifiers changed from: private */
    public static TextView pauseTv;
    /* access modifiers changed from: private */
    public static TextView resetTv;
    /* access modifiers changed from: private */
    public static TextView startTv;
    public static StopWatchService stopWatchService;
    public static ArrayList<String> timeLables = new ArrayList<>();
    /* access modifiers changed from: private */
    public static TextView timerCounterTv;
    /* access modifiers changed from: private */
    public MainActivity activity;
    /* access modifiers changed from: private */
    public int clo = 0;
    long currentTime = 0;
    /* access modifiers changed from: private */
    public String isFrist;
    /* access modifiers changed from: private */
    public CommonAdapter<String> mAdapter;
    public StopServiceConnection mStopServiceConnection;
    /* access modifiers changed from: private */
    public ListView recordListview;
    public TextView recordTv;
    long startTime;
    String timeString = "00:00:00";
    private List<View> viewLists;

    class Page1 implements View.OnClickListener {
        Page1() {
        }

        private void loadView(View view) {
            TextView unused = MainPagerAdapter.lapNumberTv = (TextView) view.findViewById(R.id.lap_number_tv);
            TextView unused2 = MainPagerAdapter.timerCounterTv = (TextView) view.findViewById(R.id.timer_counter_txt);
            TextView unused3 = MainPagerAdapter.startTv = (TextView) view.findViewById(R.id.tv_start);
            MainPagerAdapter.startTv.setOnClickListener(this);
            TextView unused4 = MainPagerAdapter.continuesTv = (TextView) view.findViewById(R.id.tv_continues);
            MainPagerAdapter.continuesTv.setOnClickListener(this);
            TextView unused5 = MainPagerAdapter.pauseTv = (TextView) view.findViewById(R.id.tv_pause);
            MainPagerAdapter.pauseTv.setOnClickListener(this);
            TextView unused6 = MainPagerAdapter.meteringTv = (TextView) view.findViewById(R.id.tv_metering);
            MainPagerAdapter.meteringTv.setOnClickListener(this);
            TextView unused7 = MainPagerAdapter.resetTv = (TextView) view.findViewById(R.id.tv_reset);
            MainPagerAdapter.resetTv.setOnClickListener(this);
            MainPagerAdapter.this.startService();
            MainPagerAdapter.this.bindService();
        }

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_continues:
                    MainPagerAdapter.this.continues();
                    return;
                case R.id.tv_metering:
                    MainPagerAdapter.this.meter();
                    return;
                case R.id.tv_pause:
                    MainPagerAdapter.stopWatchService.pauseTime();
                    MainPagerAdapter.continuesTv.setVisibility(View.VISIBLE);
                    MainPagerAdapter.resetTv.setVisibility(View.VISIBLE);
                    MainPagerAdapter.meteringTv.setVisibility(View.GONE);
                    MainPagerAdapter.pauseTv.setVisibility(View.GONE);
                    String unused = MainPagerAdapter.this.isFrist = "3";
                    SPUtils.setStringValue(MainPagerAdapter.mContext, MainPagerAdapter.KEY_FRIST_ID, MainPagerAdapter.this.isFrist);
                    MainPagerAdapter.this.flicker();
                    return;
                case R.id.tv_reset:
                    MainPagerAdapter.stopWatchService.resetTime();
                    MainPagerAdapter.timerCounterTv.setText("00:00.00");
                    MainPagerAdapter.pauseTv.setVisibility(View.GONE);
                    MainPagerAdapter.meteringTv.setVisibility(View.GONE);
                    MainPagerAdapter.continuesTv.setVisibility(View.GONE);
                    MainPagerAdapter.resetTv.setVisibility(View.GONE);
                    MainPagerAdapter.startTv.setVisibility(View.VISIBLE);
                    if (MainPagerAdapter.timeLables.size() > 0) {
                        MainPagerAdapter.this.mAdapter.notifyDataSetChanged();
                    }
                    MainPagerAdapter.noMeteringLayout.setVisibility(View.VISIBLE);
                    MainPagerAdapter.this.recordTv.setVisibility(View.GONE);
                    MainPagerAdapter.timerCounterTv.setTextColor(MainPagerAdapter.mContext.getResources().getColor(R.color.timerColor));
                    String unused2 = MainPagerAdapter.this.isFrist = "1";
                    SPUtils.setStringValue(MainPagerAdapter.mContext, MainPagerAdapter.KEY_FRIST_ID, MainPagerAdapter.this.isFrist);
                    MainPagerAdapter.mTimer.cancel();
                    return;
                case R.id.tv_start:
                    MainPagerAdapter.stopWatchService.startTime();
                    MainPagerAdapter.startTv.setVisibility(View.GONE);
                    MainPagerAdapter.pauseTv.setVisibility(View.VISIBLE);
                    MainPagerAdapter.meteringTv.setVisibility(View.GONE);
                    MainPagerAdapter.continuesTv.setVisibility(View.GONE);
                    MainPagerAdapter.resetTv.setVisibility(View.GONE);
                    return;
                default:
                    return;
            }
        }
    }

    class Page2 {
        Page2() {
        }

        private void loadView(View view) {
            ListView unused = MainPagerAdapter.this.recordListview = (ListView) view.findViewById(R.id.record_listview);
            MainPagerAdapter.this.recordTv = (TextView) view.findViewById(R.id.record_tv);
            LinearLayout unused2 = MainPagerAdapter.noMeteringLayout = (LinearLayout) view.findViewById(R.id.no_metering_layout);
            Log.e("timeLables", "timeLables page:" + MainPagerAdapter.timeLables);
            MainPagerAdapter.this.initAdapter();
        }
    }

    private class StopServiceConnection implements ServiceConnection {
        public void onServiceDisconnected(ComponentName componentName) {
        }

        private StopServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainPagerAdapter.stopWatchService = ((StopWatchService.StopBinder) iBinder).getService();
        }
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public MainPagerAdapter(Context context, List<View> list, MainActivity mainActivity) {
        mContext = context;
        this.viewLists = list;
        this.activity = mainActivity;
    }

    public void continues() {
        stopWatchService.continuesTime();
        pauseTv.setVisibility(View.VISIBLE);
        meteringTv.setVisibility(View.VISIBLE);
        continuesTv.setVisibility(View.GONE);
        resetTv.setVisibility(View.GONE);
        timerCounterTv.setTextColor(mContext.getResources().getColor(R.color.timerColor));
        mTimer.cancel();
    }

    public void meter() {
        stopWatchService.meteringTime();
        timeLables = stopWatchService.timeLables;
        initAdapter();
        Log.e("meter", "meter: " + new Gson().toJson((Object) timeLables));
        this.mAdapter.notifyDataSetChanged();
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

    public void flicker() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public void run() {
                MainPagerAdapter.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (MainPagerAdapter.this.clo == 0) {
                            int unused = MainPagerAdapter.this.clo = 1;
                            MainPagerAdapter.timerCounterTv.setTextColor(0);
                            return;
                        }
                        int unused2 = MainPagerAdapter.this.clo = 0;
                        MainPagerAdapter.timerCounterTv.setTextColor(MainPagerAdapter.mContext.getResources().getColor(R.color.timerColor));
                    }
                });
            }
        }, 1, 500);
    }

    public void startService() {
        mContext.startService(new Intent(mContext, StopWatchService.class));
    }

    /* access modifiers changed from: private */
    public void bindService() {
        Intent intent = new Intent(mContext, StopWatchService.class);
        this.mStopServiceConnection = new StopServiceConnection();
        mContext.bindService(intent, this.mStopServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void initAdapter() {
        this.mAdapter = new CommonAdapter<String>(mContext, timeLables, R.layout.item_record) {
            public void convert(ViewHolder viewHolder, String str) {
                Log.e("timeLables", "timeLables adapter:" + MainPagerAdapter.timeLables);
                if (MainPagerAdapter.timeLables.size() > 0) {
                    MainPagerAdapter.noMeteringLayout.setVisibility(View.GONE);
                    MainPagerAdapter.this.recordTv.setVisibility(View.VISIBLE);
                } else {
                    MainPagerAdapter.noMeteringLayout.setVisibility(View.VISIBLE);
                    MainPagerAdapter.this.recordTv.setVisibility(View.GONE);
                }
                viewHolder.setTextView((int) R.id.record_time_tv, str);
            }
        };
        this.recordListview.setAdapter(this.mAdapter);
    }

    public int getCount() {
        return this.viewLists.size();
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView(this.viewLists.get(i));
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        View view = this.viewLists.get(i);
        String.valueOf(view.getTag());
        viewGroup.addView(this.viewLists.get(i));
        return view;
    }
}
