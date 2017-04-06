package com.willy.nfc.nfcoperator.util;

public class Logger {
    private static final String TAG_PACKAGE = "MichaelKnight";
    private static final String FORMAT_LOG_TAG = "%s.%s";
    private static final int MAX_LOG_LENGTH = 23;
    private static final String TRUNCATE_MARK = "_";

    public static String getLogTag(Class<?> clz) {
        String fullTAG = String.format(FORMAT_LOG_TAG, TAG_PACKAGE, clz.getSimpleName());

        StringBuffer normalizedTAG = new StringBuffer(fullTAG);
        if (normalizedTAG.length() > MAX_LOG_LENGTH) {
            normalizedTAG.replace(MAX_LOG_LENGTH - TRUNCATE_MARK.length(), normalizedTAG.length(), TRUNCATE_MARK);
        }

        return normalizedTAG.toString();
    }

    public static void v(String tag, String msg) {
        android.util.Log.v(tag, msg);
    }

    public static void v(String tag, String format, Object... args) {
        android.util.Log.v(tag, String.format(format, args));
    }

    public static void d(String tag, String msg) {
        android.util.Log.d(tag, msg);
    }

    public static void d(String tag, String format, Object... args) {
        android.util.Log.d(tag, String.format(format, args));
    }

    public static void d(String tag, String msg, Throwable tr) {
        android.util.Log.d(tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        android.util.Log.i(tag, msg);
    }

    public static void i(String tag, String format, Object... args) {
        android.util.Log.i(tag, String.format(format, args));
    }

    public static void w(String tag, String msg) {
        android.util.Log.w(tag, msg);
    }

    public static void w(String tag, String format, Object... args) {
        android.util.Log.w(tag, String.format(format, args));
    }

    public static void w(String tag, String msg, Throwable tr) {
        android.util.Log.w(tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(tag, msg);
    }

    public static void e(String tag, String format, Object... args) {
        android.util.Log.e(tag, String.format(format, args));
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(tag, msg, tr);
    }

    public static String showStack(int level, String tag) {
        final StackTraceElement[] ste = new Throwable().getStackTrace();
        String result = "";
        if (ste == null) {
            return result;
        }
        int deep = ste.length;
        if (deep < 2) {
            return result;
        }
        if (deep > level) {
            deep = level;
        }
        for (int i = 2; i < deep; i++) {
            d(tag, "    [%s] %s, %s(), Line:%d", tag, ste[i].getFileName(), ste[i].getMethodName(),
                    ste[i].getLineNumber());
        }
        return result;
    }
}
