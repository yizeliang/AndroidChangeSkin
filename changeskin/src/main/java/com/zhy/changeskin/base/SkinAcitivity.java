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

    private View contentView;

    /**
     * 记录皮肤ID
     */
    private String mCurSkinId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNeedSkin()) {
            SkinManager.getInstance().register(this);
            getContentView().post(new Runnable() {
                @Override
                public void run() {
                    apply();
                }
            });
        }
    }

    private View getContentView() {
        if (contentView == null) {
            contentView = findViewById(android.R.id.content);
        }
        return contentView;
    }


    @Override
    public void apply() {
        if (TextUtils.isEmpty(mCurSkinId)) {
            mCurSkinId = SkinManager.getInstance().getCurSkin().getSkinId();
            if (TextUtils.isEmpty(mCurSkinId)) {
                mCurSkinId = SkinInfo.defaultSkin.getSkinId();
            }
        }

        if (mCurSkinId.equals(SkinManager.getInstance().getCurSkin().getSkinId())) {
            return;
        } else {
            mCurSkinId = SkinManager.getInstance().getCurSkin().getSkinId();
        }

        SkinManager.getInstance().injectSkin(getContentView());
    }

    @Override
    protected void onDestroy() {
        if (isNeedSkin()) {
            SkinManager.getInstance().unregister(this);
        }
        super.onDestroy();
    }
}
