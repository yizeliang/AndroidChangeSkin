package com.zhy.changeskin.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.zhy.changeskin.SkinManager;
import com.zhy.changeskin.modle.SkinInfo;

/**
 * Created by YZL on 2017/2/6.
 */
public abstract class SkinAcitivity extends AppCompatActivity implements SkinInterface {

    protected View contentView;

    /**
     * 记录皮肤ID
     */
    protected String mCurSkinId;

    /**
     * 皮肤是否更换_用于 延迟换肤
     */
    protected boolean isSkinChanged;

    /**
     * 当前Acitivity 是否显示
     */
    protected boolean isActShowing;

    /**
     * 是否第一次运行
     */
    protected boolean isFirstRun = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNeedSkin()) {
            SkinManager.getInstance().register(this);
        }
    }

    protected View getContentView() {
        if (contentView == null) {
            contentView = findViewById(android.R.id.content);
        }
        return contentView;
    }


    @Override
    public void apply() {
        //在后台的情况
        if (isSkinLazyApply() && (!isActShowing)) {
            isSkinChanged = true;
            return;
        }
        isSkinChanged = false;
        //执行换肤
        if (TextUtils.isEmpty(mCurSkinId)) {
            mCurSkinId = SkinInfo.defaultSkin.getSkinId();
        }
        if (mCurSkinId.equals(SkinManager.getInstance().getCurSkin().getSkinId())) {
            return;
        } else {
            mCurSkinId = SkinManager.getInstance().getCurSkin().getSkinId();
            SkinManager.getInstance().injectSkin(getContentView());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActShowing = true;

        //初次运行
        if (isFirstRun) {
            apply();
            isFirstRun = false;
        }

        if (isNeedSkin() && isSkinLazyApply() && isSkinChanged) {
            apply();
            isSkinChanged = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActShowing = false;
    }

    @Override
    protected void onDestroy() {
        if (isNeedSkin()) {
            SkinManager.getInstance().unregister(this);
        }
        super.onDestroy();
    }

}
