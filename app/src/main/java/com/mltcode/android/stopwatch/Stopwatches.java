package com.mltcode.android.stopwatch;

import android.content.Context;
import android.text.format.DateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stopwatches {
    private static Locale slocale = Locale.getDefault();

    public static void updateLocal(Locale locale) {
        slocale = locale;
    }

    public static String getTimeText(Context context, long j, boolean z) {
        long j2;
        String[] timePoint = getTimePoint(context);
        if (j < 0) {
            j = 0;
        }
        long j3 = (j % 1000) / 10;
        long j4 = (j % 60000) / 1000;
        long j5 = (j % 3600000) / 60000;
        if (z) {
            j2 = (j % 86400000) / 3600000;
        } else {
            j2 = j / 3600000;
        }
        if (j2 > 0) {
            return String.format(slocale, "%02d" + timePoint[0] + "%02d" + timePoint[1] + "%02d.%02d", new Object[]{Long.valueOf(j2), Long.valueOf(j5), Long.valueOf(j4), Long.valueOf(j3)});
        }
        return String.format(slocale, "%02d" + timePoint[1] + "%02d.%02d", new Object[]{Long.valueOf(j5), Long.valueOf(j4), Long.valueOf(j3)});
    }

    public static String[] getTimePoint(Context context) {
        String[] strArr = new String[2];
        Matcher matcher = Pattern.compile("(\\w+)(.|:)(\\w+)(.|:)(\\w+)").matcher(DateFormat.getBestDateTimePattern(context.getResources().getConfiguration().locale, "Hms"));
        if (matcher.find()) {
            strArr[0] = matcher.group(2);
            strArr[1] = matcher.group(4);
        }
        return strArr;
    }
}
