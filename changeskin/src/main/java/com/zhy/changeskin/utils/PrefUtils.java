package com.zhy.changeskin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.zhy.changeskin.constant.SkinConfig;
import com.zhy.changeskin.modle.SkinInfo;


/**
 * Created by zhy on 15/9/22.
 */
public class PrefUtils {
    private Context mContext;


    public PrefUtils(Context context) {
        this.mContext = context;
    }

    public SkinInfo getSkin() {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SkinInfo SkinInfo = new SkinInfo();
        for (int i = 0; i < SkinConfig.PREF_PARAMS.length - 2; i++) {
            String s = sp.getString(SkinConfig.PREF_PARAMS[i], "");
            if (TextUtils.isEmpty(s)) {
                continue;
            }
            switch (i) {
                case 0:
                    SkinInfo.setSkinId(s);
                    break;
                case 1:
                    SkinInfo.setPluginPath(s);
                    break;
                case 2:
                    SkinInfo.setPckName(s);
                    break;
                case 3:
                    SkinInfo.setSuffix(s);
                    break;
                case 4:
                    if (s.equals("Y")) {
                        SkinInfo.setUsePlugin(true);
                    } else {
                        SkinInfo.setUsePlugin(false);
                    }
                    break;
            }
        }
        return SkinInfo;
    }

    public boolean clear() {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sp.edit().clear().commit();
    }


    public void saveSkin(SkinInfo SkinInfo) {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_FILE_NAME, Context.MODE_PRIVATE);
        if (SkinInfo == null) {
            return;
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(SkinConfig.PREF_SKIN_ID, SkinInfo.getSkinId());
        if (SkinInfo.isUsePlugin()) {
            edit.putString(SkinConfig.PREF_SKIN_PATH, SkinInfo.getPluginPath());
            edit.putString(SkinConfig.PREF_SKIN_PCK, SkinInfo.getPckName());
            edit.putString(SkinConfig.PREF_USE_PLUGIN, "T");
        } else {
            edit.putString(SkinConfig.PREF_SUFFIX, SkinInfo.getSuffix());
            edit.putString(SkinConfig.PREF_USE_PLUGIN, "Y");
        }
        edit.commit();
    }

}
