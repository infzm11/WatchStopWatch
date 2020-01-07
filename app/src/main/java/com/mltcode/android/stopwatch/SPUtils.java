package com.mltcode.android.stopwatch;

import android.content.Context;
import android.content.SharedPreferences;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SPUtils {
    public static final String FILE_NAME = "PIPIWIRISTBAND";
    private static int MODE;

    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        private SharedPreferencesCompat() {
        }

        private static Method findApplyMethod() {
            try {
                return SharedPreferences.Editor.class.getMethod("apply", new Class[0]);
            } catch (NoSuchMethodException unused) {
                return null;
            }
        }

        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor, new Object[0]);
                    return;
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException unused) {
            }
            editor.commit();
        }
    }

    public SPUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void put(Context context, String str, Object obj) {
        SharedPreferences.Editor edit = context.getSharedPreferences(FILE_NAME, MODE).edit();
        if (obj instanceof String) {
            edit.putString(str, (String) obj);
        } else if (obj instanceof Integer) {
            edit.putInt(str, ((Integer) obj).intValue());
        } else if (obj instanceof Boolean) {
            edit.putBoolean(str, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Float) {
            edit.putFloat(str, ((Float) obj).floatValue());
        } else if (obj instanceof Long) {
            edit.putLong(str, ((Long) obj).longValue());
        } else {
            edit.putString(str, obj.toString());
        }
        SharedPreferencesCompat.apply(edit);
    }

    public static Object get(Context context, String str, Object obj) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, MODE);
        if (obj instanceof String) {
            return sharedPreferences.getString(str, (String) obj);
        }
        if (obj instanceof Integer) {
            return Integer.valueOf(sharedPreferences.getInt(str, ((Integer) obj).intValue()));
        }
        if (obj instanceof Boolean) {
            return Boolean.valueOf(sharedPreferences.getBoolean(str, ((Boolean) obj).booleanValue()));
        }
        if (obj instanceof Float) {
            return Float.valueOf(sharedPreferences.getFloat(str, ((Float) obj).floatValue()));
        }
        if (obj instanceof Long) {
            return Long.valueOf(sharedPreferences.getLong(str, ((Long) obj).longValue()));
        }
        return null;
    }

    public static void remove(Context context, String str) {
        SharedPreferences.Editor edit = context.getSharedPreferences(FILE_NAME, MODE).edit();
        edit.remove(str);
        SharedPreferencesCompat.apply(edit);
    }

    public static void clear(Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(FILE_NAME, MODE).edit();
        edit.clear();
        SharedPreferencesCompat.apply(edit);
    }

    public static boolean contains(Context context, String str) {
        return context.getSharedPreferences(FILE_NAME, MODE).contains(str);
    }

    public static Map<String, ?> getAll(Context context) {
        return context.getSharedPreferences(FILE_NAME, MODE).getAll();
    }

    public static String getStringValue(Context context, String str) {
        return context.getApplicationContext().getSharedPreferences(FILE_NAME, MODE).getString(str, "");
    }

    public static void setStringValue(Context context, String str, String str2) {
        SharedPreferences.Editor edit = context.getSharedPreferences(FILE_NAME, MODE).edit();
        edit.putString(str, str2);
        edit.commit();
    }

    public static boolean getBooleanValue(Context context, String str, boolean z) {
        return context.getApplicationContext().getSharedPreferences(FILE_NAME, MODE).getBoolean(str, z);
    }

    public static void setBooleanValue(Context context, String str, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(FILE_NAME, MODE).edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public static void removeValue(Context context, String str) {
        SharedPreferences.Editor edit = context.getSharedPreferences(FILE_NAME, MODE).edit();
        edit.remove(str);
        edit.commit();
    }
}
