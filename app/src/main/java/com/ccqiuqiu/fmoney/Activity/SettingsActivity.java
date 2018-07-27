package com.ccqiuqiu.fmoney.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.TagFragment;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.kenumir.materialsettings.MaterialSettingsActivity;
import com.kenumir.materialsettings.MaterialSettingsFragment;
import com.kenumir.materialsettings.items.CheckboxItem;
import com.kenumir.materialsettings.items.DividerItem;
import com.kenumir.materialsettings.items.HeaderItem;
import com.kenumir.materialsettings.items.SwitcherItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.PreferencesStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


public class SettingsActivity extends MaterialSettingsActivity
        implements ColorChooserDialog.ColorCallback {

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;
    private MaterialSettingsFragment mMaterialSettingsFragment;
    private TagFragment mTagFragment;
    private SharedPreferences mySharedPreferences;
    private HeaderItem mTitle1, mTitle2, mTitle3;
    private TextItem mTvPrimary, mTvAccent;
    private Drawable mImgPrimary;
    private Drawable mImgAcctne;
    private SwitcherItem mSwitcherItemZhiChu, mSwitcherItemShare,mSwitcherItemAutoSync;
    private int mDrawableCompatCheckedThumb, mDrawableCompatCheckedTrack;//选中状态的按钮颜色和滑道颜色
    private int mDrawableCompatUnCheckedThumb, mDrawableCompatUnCheckedTrack;
    private boolean mIsShowCategory;
    private boolean IsOpenDev;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setTitle(getResources().getString(R.string.action_settings));
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(App.colorPrimary));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(App.colorPrimaryDark);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
        mMaterialSettingsFragment = getFragment();
        mImgPrimary = getResources().getDrawable(R.drawable.shape_point_red);
        mImgAcctne = getResources().getDrawable(R.drawable.shape_point_red);

        mDrawableCompatCheckedThumb = App.colorAccent;
        mDrawableCompatCheckedTrack = ViewUtils.modifyAlpha(App.colorAccent, 80);
        mDrawableCompatUnCheckedThumb = Color.parseColor("#FFEEEEEE");
        mDrawableCompatUnCheckedTrack = Color.parseColor("#FFAAAAAA");

        //标题1
        addItem(mTitle1 = new HeaderItem(mMaterialSettingsFragment).setTitle(getString(R.string.sett_h1)));
        //设置主色
        mImgPrimary.setColorFilter(App.colorPrimary, PorterDuff.Mode.SRC_ATOP);
        addItem(mTvPrimary = new TextItem(mMaterialSettingsFragment, "sett_color_primary")
                .setTitle(getResources().getString(R.string.sett_color_primary))
                .setIcon(mImgPrimary)
                .setSubtitle(getResources().getString(R.string.sett_color_primary_sub))
                .setOnclick(new TextItem.OnClickListener() {
                    @Override
                    public void onClick(TextItem textItem) {
                        new ColorChooserDialog.Builder(SettingsActivity.this,
                                R.string.sett_color_primary_desc)
                                .titleSub(R.string.sett_color_primary_desc)
                                .backButton(R.string.back)
                                .cancelButton(R.string.cancel)
                                .customButton(R.string.custom)
                                .doneButton(R.string.done)
                                .presetsButton(R.string.presets)
                                .preselect(App.colorPrimary)
                                .show();
                    }
                }));
        //分割线
        addItem(new DividerItem(mMaterialSettingsFragment));
        //强调色
        addItem(mTvAccent = new TextItem(mMaterialSettingsFragment, "sett_color_accent")
                .setTitle(getResources().getString(R.string.sett_color_accent))
                .setIcon(mImgAcctne)
                .setSubtitle(getResources().getString(R.string.sett_color_accent_sub))
                .setOnclick(new TextItem.OnClickListener() {
                    @Override
                    public void onClick(TextItem textItem) {
                        new ColorChooserDialog.Builder(SettingsActivity.this,
                                R.string.sett_color_accent_desc)
                                .titleSub(R.string.sett_color_primary_desc)
                                .backButton(R.string.back)
                                .cancelButton(R.string.cancel)
                                .customButton(R.string.custom)
                                .doneButton(R.string.done)
                                .presetsButton(R.string.presets)
                                .preselect(App.colorAccent)
                                .accentMode(true)
                                .show();
                    }
                }));
        //分割线
        addItem(new DividerItem(mMaterialSettingsFragment));
        //设置支出颜色
        int subTitleId = R.string.sett_zhichu_color_sub;
        if (ViewUtils.getBooleanBySharedPreferences("sett_liushui_color_switch")) {
            subTitleId = R.string.sett_zhichu_color_sub2;
        }
        addItem(mSwitcherItemZhiChu = (SwitcherItem) new SwitcherItem(getFragment(), "sett_liushui_color_switch")
                .setTitle(getString(R.string.sett_zhichu_color))
                .setSubtitle(getString(subTitleId))
                .setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                        changeSwitchCompatColor(mSwitcherItemZhiChu.getSwitchCompat());
                        if (isChecked) {
                            cbi.updateSubTitle(getString(R.string.sett_zhichu_color_sub2));
                            App.colorShouRu = getResources().getColor(R.color.colorLiuShui_0);
                            App.colorZhiChu = getResources().getColor(R.color.colorLiuShui_1);
                        } else {
                            cbi.updateSubTitle(getString(R.string.sett_zhichu_color_sub));
                            App.colorShouRu = getResources().getColor(R.color.colorLiuShui_1);
                            App.colorZhiChu = getResources().getColor(R.color.colorLiuShui_0);
                        }
                    }
                }));
        //分割线
        addItem(new DividerItem(mMaterialSettingsFragment));
        //设置自动同步
        addItem(mSwitcherItemAutoSync = (SwitcherItem) new SwitcherItem(getFragment(), "sett_auto_sync")
                .setTitle(getString(R.string.sett_auto_sync))
                .setSubtitle(getString(R.string.sett_auto_sync_conn))
                .setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                        changeSwitchCompatColor(mSwitcherItemAutoSync.getSwitchCompat());
                    }
                }));
        //分割线
        addItem(new DividerItem(mMaterialSettingsFragment));
        //标题2
        addItem(mTitle2 = new HeaderItem(mMaterialSettingsFragment).setTitle(getString(R.string.sett_h2)));
        //标签管理
        mTagFragment = TagFragment.newInstance(this);
        addItem(new TextItem(mMaterialSettingsFragment, "sett_tag")
                .setTitle(getResources().getString(R.string.sett_tag))
                .setSubtitle(getResources().getString(R.string.sett_tag_sub))
                .setOnclick(new TextItem.OnClickListener() {
                    @Override
                    public void onClick(TextItem textItem) {
                        showCategory();
                    }
                }));

//        IsOpenDev = ViewUtils.getBooleanBySharedPreferences("dev_open");
//        changeDev(IsOpenDev, false);
        //分割线
        addItem(new DividerItem(mMaterialSettingsFragment));
        //关于
        addItem(mTitle3 = new HeaderItem(mMaterialSettingsFragment).setTitle(getString(R.string.about)));
//        //欢迎页
//        addItem(new TextItem(mMaterialSettingsFragment, "welcome")
//                .setTitle(getString(R.string.show_welcome))
//                .setOnclick(new TextItem.OnClickListener() {
//                    @Override
//                    public void onClick(TextItem textItem) {
//                        ViewUtils.putIntToSharedPreferences("per_ver", 0);
//                        Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
//                        intent.putExtra("noStartMain", true);
//                        startActivity(intent);
////                        Intent intent = getIntent();
////                        intent.putExtra("isShowWelcome", true);
//                        setResult(App.CODE_SETTINGS);
//                        finish();
//                    }
//                }));
        //关于
        final long[] mHits = new long[5];
        addItem(new TextItem(mMaterialSettingsFragment, "version")
                        .setTitle(getString(R.string.version) + "：V" + ViewUtils.getVersionName())
                        .setSubtitle(getString(R.string.auth_email))
                .setOnclick(new TextItem.OnClickListener() {
                    @Override
                    public void onClick(TextItem textItem) {
                        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                        mHits[mHits.length - 1] = System.currentTimeMillis();
                        if (mHits[0] >= System.currentTimeMillis() - 1000) {
                            App.initTestData();
                        }
                    }
                })
        );

        changeViewColor(App.colorAccent);

        Intent intent = getIntent();
        mIsShowCategory = intent.getBooleanExtra("isShowCategory", false);
        if (mIsShowCategory) {
            showCategory();
        }
    }

//    private void changeDev(boolean open, boolean showToast) {
//        IsOpenDev = open;
//        int stringId = R.string.dev_show;
//        if (open) {
//            mSwitcherItemShare.getView().setVisibility(View.VISIBLE);
//        } else {
//            mSwitcherItemShare.getView().setVisibility(View.GONE);
//            stringId = R.string.dev_hide;
//        }
//        if (showToast) {
//            ViewUtils.toast(getString(stringId));
//        }
//        ViewUtils.putBooleanToSharedPreferences("dev_open", open);
//    }

    private void showCategory() {
        mToolbar.setTitle(getResources().getString(R.string.sett_tag));
        if (mTagFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .hide(mMaterialSettingsFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.content, mTagFragment)
                    .hide(mMaterialSettingsFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public StorageInterface initStorageInterface() {
        return new PreferencesStorageInterface(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mIsShowCategory) {
            Intent intent = getIntent();
            intent.putExtra("isShowCategory", mIsShowCategory);
            setResult(App.CODE_SETTINGS, intent);
            finish();
        } else {
            mToolbar.setTitle(getString(R.string.action_settings));
            setResult(App.CODE_SETTINGS);
            super.onBackPressed();
        }
    }

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {
        if (dialog.isAccentMode()) {
            App.colorAccent = selectedColor;
            App.colorAccentDark = ViewUtils.shiftColorDown(selectedColor);
            ThemeSingleton.get().positiveColor = DialogUtils.getActionTextStateList(this,
                    App.colorAccent);
            ThemeSingleton.get().neutralColor = DialogUtils.getActionTextStateList(this,
                    App.colorAccent);
            ThemeSingleton.get().negativeColor = DialogUtils.getActionTextStateList(this,
                    App.colorAccent);
            ThemeSingleton.get().widgetColor = selectedColor;
            mySharedPreferences.edit()
                    .putInt("colorAccent", selectedColor)
                    .putInt("colorAccentDark", App.colorAccentDark)
                    .commit();

            changeViewColor(selectedColor);

        } else {
            App.colorPrimary = selectedColor;
            App.colorPrimaryDark = ViewUtils.shiftColorDown(selectedColor);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(App.colorPrimary));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(CircleView.shiftColorDown(App.colorPrimaryDark));
                getWindow().setNavigationBarColor(App.colorPrimary);
            }

            mySharedPreferences.edit()
                    .putInt("colorPrimary", App.colorPrimary)
                    .putInt("colorPrimaryDark", App.colorPrimaryDark)
                    .commit();

            mImgPrimary.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
            mTvPrimary.updateIcon(mImgPrimary);
        }
    }

    private void changeViewColor(int color) {
        mImgAcctne.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        mTvAccent.updateIcon(mImgAcctne);
        mDrawableCompatCheckedThumb = color;
        mDrawableCompatCheckedTrack = ViewUtils.modifyAlpha(color, 80);
        changeSwitchCompatColor(mSwitcherItemZhiChu.getSwitchCompat());
        changeSwitchCompatColor(mSwitcherItemAutoSync.getSwitchCompat());
//        changeSwitchCompatColor(mSwitcherItemShare.getSwitchCompat());
        mTitle1.setTextColcr(color);
        mTitle2.setTextColcr(color);
        mTitle3.setTextColcr(color);

    }

    private void changeSwitchCompatColor(SwitchCompat switchCompat) {
        if (switchCompat.isChecked()) {
            DrawableCompat.setTint(switchCompat.getThumbDrawable(), mDrawableCompatCheckedThumb);
            DrawableCompat.setTint(switchCompat.getTrackDrawable(), mDrawableCompatCheckedTrack);
        } else {
            DrawableCompat.setTint(switchCompat.getThumbDrawable(), mDrawableCompatUnCheckedThumb);
            DrawableCompat.setTint(switchCompat.getTrackDrawable(), mDrawableCompatUnCheckedTrack);
        }
    }
}
