package com.mltcode.android.stopwatch;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;

public class SlidrBaseActivity extends Activity {
    protected SlidrInterface slidr;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.slidr = Slidr.attach(this, new SlidrConfig.Builder().sensitivity(0.2f).velocityThreshold(250.0f).build());
    }
}
