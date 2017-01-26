package com.ccqiuqiu.fmoney.Fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ccqiuqiu.fmoney.Activity.SettingsActivity;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.CategoryService;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import com.rey.material.widget.Button;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

/**
 * Created by cc on 2015/12/17.
 */
@ContentView(R.layout.fragment_tags)
public class TagFragment extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener {

    @ViewInject(R.id.cv_zhichu)
    private CardView mCvZhiChu;
    @ViewInject(R.id.tc_zhichu)
    private TagContainerLayout mTgZhiChu;
    @ViewInject(R.id.et_zhichu)
    private MaterialEditText mEtZhiChu;
    @ViewInject(R.id.btn_zhichu)
    private Button mBtnZhiChu;
    @ViewInject(R.id.cv_shouru)
    private CardView mCvShouRu;
    @ViewInject(R.id.tc_shouru)
    private TagContainerLayout mTgShouRu;
    @ViewInject(R.id.et_shouru)
    private MaterialEditText mEtShouRu;
    @ViewInject(R.id.btn_shouru)
    private Button mBtnShouRu;

    private boolean mIsEditZhiChu;
    private boolean mIsEditShouRu;
    private Category mEditCategory;


    private LiuShuiService mLiuShuiService = new LiuShuiService();
    private CategoryService mCategoryService = new CategoryService();
    private List<Category> mZhiChuList = new ArrayList<>();
    private List<Category> mShouRuList = new ArrayList<>();

    public static TagFragment newInstance(SettingsActivity settingsActivity) {
        return new TagFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        x.view().inject(this, this.getView());

        mZhiChuList = mCategoryService.getByFlg(Category.FLG_ZHICHU);
        mShouRuList = mCategoryService.getByFlg(Category.FLG_SHOURU);

    initColor();

        //标签点击和长按监听
        mTgZhiChu.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagFragment.this.onTagClick(position, text, Category.FLG_ZHICHU);
            }

            @Override
            public void onTagLongClick(int position, String text) {
                TagFragment.this.onTagLongClick(position, text, Category.FLG_ZHICHU);
            }
        });
        mTgShouRu.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagFragment.this.onTagClick(position, text, Category.FLG_SHOURU);
            }

            @Override
            public void onTagLongClick(int position, String text) {
                TagFragment.this.onTagLongClick(position, text, Category.FLG_SHOURU);
            }
        });
        //标签拖动监听
        mTgZhiChu.setOnTagDragListener(new TagContainerLayout.OnTagDragListener() {
            @Override
            public void onDrag(int fromPosition, int toPosition) {
                mCategoryService.changeOrder(mZhiChuList, fromPosition, toPosition);
                mZhiChuList.clear();
                mZhiChuList = mCategoryService.getByFlg(Category.FLG_ZHICHU);
            }
        });
        mTgShouRu.setOnTagDragListener(new TagContainerLayout.OnTagDragListener() {
            @Override
            public void onDrag(int fromPosition, int toPosition) {
                mCategoryService.changeOrder(mShouRuList, fromPosition, toPosition);
                mShouRuList.clear();
                mShouRuList = mCategoryService.getByFlg(Category.FLG_SHOURU);

            }
        });
        //添加按钮触摸监听
        mBtnZhiChu.setOnClickListener(this);
        mBtnShouRu.setOnClickListener(this);
        //软键盘发送按钮事件
        mEtZhiChu.setOnEditorActionListener(this);
        mEtShouRu.setOnEditorActionListener(this);

        //添加标签
        addCategorgToTagGroup(mZhiChuList);
        addCategorgToTagGroup(mShouRuList);
    }

    private void initColor() {
        Drawable drawable = getResources().getDrawable(R.drawable.btn_background_hong);
        drawable.setColorFilter(App.colorZhiChu, PorterDuff.Mode.SRC_ATOP);
        mBtnZhiChu.setBackground(drawable);
        //mCvZhiChu.setCardBackgroundColor(ViewUtils.modifyAlpha(App.colorZhiChu, 20));
        mTgZhiChu.setTagBackgroundColor(App.colorZhiChu);

        Drawable drawable2 = getResources().getDrawable(R.drawable.btn_background_hong);
        drawable2.setColorFilter(App.colorShouRu, PorterDuff.Mode.SRC_ATOP);
        mBtnShouRu.setBackground(drawable2);
        //mCvShouRu.setCardBackgroundColor(ViewUtils.modifyAlpha(App.colorShouRu, 20));
        mTgShouRu.setTagBackgroundColor(App.colorShouRu);
    }

    private void addCategorgToTagGroup(List<Category> categories) {
        for (Category category : categories) {
            if (category.getFlg() == Category.FLG_ZHICHU) {
                mTgZhiChu.setTags(category.getName());
            } else {
                mTgShouRu.setTags(category.getName());
            }

        }
    }

    private void saveTag(int flg) {
        if (flg == Category.FLG_ZHICHU) {
            String name = mEtZhiChu.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                mEtZhiChu.setError(getString(R.string.input_is_null));
                return;
            }
            //检查重复
            if (!mIsEditZhiChu && mCategoryService.isExist(name, Category.FLG_ZHICHU)) {
                mEtZhiChu.setError(getString(R.string.cate_is_exist));
                return;
            }
            if (mIsEditZhiChu) {
                mIsEditZhiChu = false;
                if (!mEditCategory.getName().equals(name)) {
                    mEditCategory.setName(name);
                    mTgZhiChu.removeAllTags();
                    addCategorgToTagGroup(mZhiChuList);
                    mCategoryService.update_my(mEditCategory);
                }
                mBtnZhiChu.setText(getString(R.string.save));
                mEtZhiChu.setText("");
                ViewUtils.toast(getString(R.string.edit_success));
            } else {
                Category category = new Category();
                mEtZhiChu.setText("");
                category.setName(name);
                category.setFlg(Category.FLG_ZHICHU);
                mTgZhiChu.addTag(name);
                mZhiChuList.add(category);
                mCategoryService.saveBindingId(category);
                ViewUtils.toast(getString(R.string.add_success));
            }

        } else {
            String name = mEtShouRu.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                mEtShouRu.setError(getString(R.string.input_is_null));
                return;
            }
            //检查重复
            if (!mIsEditShouRu && mCategoryService.isExist(name, Category.FLG_SHOURU)) {
                mEtShouRu.setError(getString(R.string.cate_is_exist));
                return;
            }
            if (mIsEditShouRu) {
                mIsEditShouRu = false;
                if (!mEditCategory.getName().equals(name)) {
                    mEditCategory.setName(name);
                    mTgShouRu.removeAllTags();
                    addCategorgToTagGroup(mShouRuList);
                    mCategoryService.update_my(mEditCategory);
                }
                mBtnShouRu.setText(getString(R.string.save));
                mEtShouRu.setText("");
                ViewUtils.toast(getString(R.string.edit_success));
            } else {
                Category category = new Category();
                mTgShouRu.addTag(name);
                category.setName(name);
                category.setFlg(Category.FLG_SHOURU);
                mShouRuList.add(category);
                mEtShouRu.setText("");
                mCategoryService.saveBindingId(category);
                ViewUtils.toast(getString(R.string.add_success));
            }

        }
    }

    private void onTagClick(int position, String text, int flg) {
        if (flg == Category.FLG_ZHICHU) {
            mIsEditZhiChu = true;
            mEtZhiChu.setText(text);
            mEditCategory = mZhiChuList.get(position);
            mEtZhiChu.requestFocus();
            mBtnZhiChu.setText(getString(R.string.edit));
            ViewUtils.ShowKeyboard(mEtZhiChu);
        } else {
            mIsEditShouRu = true;
            mEtShouRu.setText(text);
            mEditCategory = mShouRuList.get(position);
            mEtShouRu.requestFocus();
            mBtnShouRu.setText(getString(R.string.edit));
            ViewUtils.ShowKeyboard(mEtShouRu);
        }
    }
    private void onTagLongClick(final int position, final String text, int flg) {
        final Category category;
        long liushuiCount = 0;
        final TagContainerLayout tagContainerLayout;
        final List<Category> categories;
        if (flg == Category.FLG_ZHICHU) {
            category = mZhiChuList.get(position);
            tagContainerLayout = mTgZhiChu;
            categories = mZhiChuList;

        } else {
            category = mShouRuList.get(position);
            tagContainerLayout = mTgShouRu;
            categories = mShouRuList;
        }
        liushuiCount = mLiuShuiService.getByCategory(category);
        String content = getString(R.string.del_cate_qr);
        if (liushuiCount > 0) {
            content = MessageFormat.format(getString(R.string.del_cate_qr2), liushuiCount);
        }
        new MaterialDialog.Builder(getContext())
                .title(getString(R.string.del_cate))
                .content(content)
                .positiveText(R.string.commit)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        mCategoryService.del(category);
                        tagContainerLayout.removeTag(position);
                        categories.remove(position);
                        ViewUtils.toast(getString(R.string.del_success));

                        if(text.equals(mEtShouRu.getText().toString())){
                            mEtShouRu.setText("");
                            mBtnShouRu.setText(getString(R.string.save));
                        }
                        if(text.equals(mEtZhiChu.getText().toString())){
                            mEtZhiChu.setText("");
                            mBtnZhiChu.setText(getString(R.string.save));
                        }
                    }
                })
                .show();
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (v.getId() == R.id.et_zhichu) {
                saveTag(Category.FLG_ZHICHU);
            } else {
                saveTag(Category.FLG_SHOURU);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        ViewUtils.HideKeyboard(mTgZhiChu);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        ViewUtils.HideKeyboard(mTgZhiChu);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        mIsEditZhiChu = false;
        mIsEditShouRu = false;
        mEtZhiChu.setText("");
        mEtShouRu.setText("");
        //initColor();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_zhichu) {
            saveTag(Category.FLG_ZHICHU);
        } else {
            saveTag(Category.FLG_SHOURU);
        }
    }
}
