package com.ccqiuqiu.fmoney.Activity;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;

import org.xutils.x;

/**
 * Created by cc on 2016/1/13.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initColor();
    }

    private void initColor() {
        SharedPreferences sp = ViewUtils.getSharedPreferences();
        App.colorPrimary = sp.getInt("colorPrimary", getResources().getColor(R.color.colorPrimary));
        App.colorPrimaryDark = sp.getInt("colorPrimaryDark", getResources().getColor(R.color.colorPrimaryDark));
        App.colorAccent = sp.getInt("colorAccent", getResources().getColor(R.color.colorAccent));
        App.colorAccentDark = sp.getInt("colorAccentDark", ViewUtils.shiftColorDown(App.colorAccent));

        ThemeSingleton.get().positiveColor = DialogUtils.getActionTextStateList(this, App.colorAccent);
        ThemeSingleton.get().neutralColor = DialogUtils.getActionTextStateList(this, App.colorAccent);
        ThemeSingleton.get().negativeColor = DialogUtils.getActionTextStateList(this, App.colorAccent);
        ThemeSingleton.get().widgetColor = App.colorAccent;

        //初始化收入支出颜色
        if (ViewUtils.getBooleanBySharedPreferences("sett_liushui_color_switch")) {
            App.colorShouRu = getResources().getColor(R.color.colorLiuShui_0);
            App.colorZhiChu = getResources().getColor(R.color.colorLiuShui_1);
        } else {
            App.colorShouRu = getResources().getColor(R.color.colorLiuShui_1);
            App.colorZhiChu = getResources().getColor(R.color.colorLiuShui_0);
        }


    }

    public void setPrimaryColor() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(App.colorPrimary));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(App.colorPrimaryDark));
            getWindow().setNavigationBarColor(App.colorPrimary);
        }
    }
}
