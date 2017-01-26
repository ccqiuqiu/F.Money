package com.ccqiuqiu.fmoney.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ccqiuqiu.fmoney.R;
import com.github.johnpersano.supertoasts.SuperToast;

import com.ccqiuqiu.fmoney.App;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cc on 2016/1/13.
 */
public class ViewUtils {
    public static final int MIN_ALPHA_SEARCH_MAX_ITERATIONS = 10;
    public static final int MIN_ALPHA_SEARCH_PRECISION = 10;


    public static Context getContext() {
        return App.mContext;
    }

    public static void toast(String content) {
        //Toast.makeText(getContext(),content,Toast.LENGTH_SHORT).show();
        SuperToast.cancelAllSuperToasts();
        SuperToast superToast = new SuperToast(getContext());
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setTextColor(Color.parseColor("#ffffff"));
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(content);
        //superToast.setBackground(SuperToast.Background.BLUE);
        superToast.show();
    }

    public static void snackbar(Context context, String text) {
        SnackbarManager.show(Snackbar.with(context)
                .type(SnackbarType.MULTI_LINE)
                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                .color(App.colorAccent)
                .text(text)
                .actionLabel(context.getString(R.string.close)));
    }

    /***
     * 修改颜色透明度
     **/
    public static int modifyAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * 加深颜色
     **/
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 2.0f) float by) {
        if (by == 1f) return color;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= by; // value component
        return Color.HSVToColor(hsv);
    }

    public static int shiftColorDown(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    /***
     * 通过背景色获取文字颜色
     **/
    public static int getTextColorForBackground(int backgroundColor, float minContrastRatio) {
        // First we will check white as most colors will be dark
        final int whiteMinAlpha = ViewUtils
                .findMinimumAlpha(Color.WHITE, backgroundColor, minContrastRatio);

        if (whiteMinAlpha >= 0) {
            return ViewUtils.modifyAlpha(Color.WHITE, whiteMinAlpha);
        }

        // If we hit here then there is not an translucent white which provides enough contrast,
        // so check black
        final int blackMinAlpha = ViewUtils
                .findMinimumAlpha(Color.BLACK, backgroundColor, minContrastRatio);

        if (blackMinAlpha >= 0) {
            return ViewUtils.modifyAlpha(Color.BLACK, blackMinAlpha);
        }

        // This should not happen!
        return -1;
    }

    public static int findMinimumAlpha(int foreground, int background, double minContrastRatio) {
        if (Color.alpha(background) != 255) {
            throw new IllegalArgumentException("background can not be translucent");
        }

        // First lets check that a fully opaque foreground has sufficient contrast
        int testForeground = modifyAlpha(foreground, 255);
        double testRatio = calculateContrast(testForeground, background);
        if (testRatio < minContrastRatio) {
            // Fully opaque foreground does not have sufficient contrast, return error
            return -1;
        }

        // Binary search to find a value with the minimum value which provides sufficient contrast
        int numIterations = 0;
        int minAlpha = 0;
        int maxAlpha = 255;

        while (numIterations <= MIN_ALPHA_SEARCH_MAX_ITERATIONS &&
                (maxAlpha - minAlpha) > MIN_ALPHA_SEARCH_PRECISION) {
            final int testAlpha = (minAlpha + maxAlpha) / 2;

            testForeground = modifyAlpha(foreground, testAlpha);
            testRatio = calculateContrast(testForeground, background);

            if (testRatio < minContrastRatio) {
                minAlpha = testAlpha;
            } else {
                maxAlpha = testAlpha;
            }

            numIterations++;
        }

        // Conservatively return the max of the range of possible alphas, which is known to pass.
        return maxAlpha;
    }

    public static double calculateContrast(int foreground, int background) {
        if (Color.alpha(background) != 255) {
            throw new IllegalArgumentException("background can not be translucent");
        }
        if (Color.alpha(foreground) < 255) {
            // If the foreground is translucent, composite the foreground over the background
            foreground = compositeColors(foreground, background);
        }

        final double luminance1 = calculateLuminance(foreground) + 0.05;
        final double luminance2 = calculateLuminance(background) + 0.05;

        // Now return the lighter luminance divided by the darker luminance
        return Math.max(luminance1, luminance2) / Math.min(luminance1, luminance2);
    }

    public static int compositeColors(int fg, int bg) {
        final float alpha1 = Color.alpha(fg) / 255f;
        final float alpha2 = Color.alpha(bg) / 255f;

        float a = (alpha1 + alpha2) * (1f - alpha1);
        float r = (Color.red(fg) * alpha1) + (Color.red(bg) * alpha2 * (1f - alpha1));
        float g = (Color.green(fg) * alpha1) + (Color.green(bg) * alpha2 * (1f - alpha1));
        float b = (Color.blue(fg) * alpha1) + (Color.blue(bg) * alpha2 * (1f - alpha1));

        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    public static double calculateLuminance(int color) {
        double red = Color.red(color) / 255d;
        red = red < 0.03928 ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);

        double green = Color.green(color) / 255d;
        green = green < 0.03928 ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);

        double blue = Color.blue(color) / 255d;
        blue = blue < 0.03928 ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);

        return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int sbarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbarHeight;
    }

    //    public static int getScreemHeight(Activity activity){
//        Display wm = activity.getWindowManager().getDefaultDisplay();
//        return wm.getHeight();
//    }
//    public static int getScreemWidth(Activity activity){
//        Display wm = activity.getWindowManager().getDefaultDisplay();
//        return wm.getWidth();
//    }
    public static int getScreemWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreemHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    //复制到剪贴板
    public static void copyToClipboard(String value) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(value);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Color", value);
            clipboard.setPrimaryClip(clip);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
//        final float scale = getContext().getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                getContext().getResources().getDisplayMetrics());

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * mipmap转Drawable
     **/
    public static Drawable mipmap2Drawable(int mipmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        //options.inSampleSize = 3;
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), mipmap, options);
        Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
        return drawable;
    }

    //显示虚拟键盘
    public static void ShowKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);

    }

    //隐藏虚拟键盘
    public static void HideKeyboard(View v) {
        if(v==null)return;
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        }
    }

    /**
     * 获取版本名
     */
    public static String getVersionName() {
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getContext().getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode() {
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getContext().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static SharedPreferences getSharedPreferences() {
        return getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public static boolean getBooleanBySharedPreferences(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public static String getStringBySharedPreferences(String key) {
        return getSharedPreferences().getString(key, null);
    }

    public static boolean putStringToSharedPreferences(String key, String val) {
        return getSharedPreferences().edit().putString(key, val).commit();
    }

    public static int getIntBySharedPreferences(String key) {
        return getSharedPreferences().getInt(key, 0);
    }

    public static boolean putBooleanToSharedPreferences(String key, boolean val) {
        return getSharedPreferences().edit().putBoolean(key, val).commit();
    }

    public static boolean putIntToSharedPreferences(String key, int val) {
        return getSharedPreferences().edit().putInt(key, val).commit();
    }
    public static boolean putLongToSharedPreferences(String key, long val) {
        return getSharedPreferences().edit().putLong(key, val).commit();
    }

    public static boolean matches(String regEx, String string) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static int getNetworkType() {
        int netType = -1;//ConnectivityManager.TYPE_MOBILE
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
       return networkInfo.getType();
    }
}
