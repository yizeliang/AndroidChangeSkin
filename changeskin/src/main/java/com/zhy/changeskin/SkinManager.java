package com.zhy.changeskin;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zhy.changeskin.attr.SkinAttrSupport;
import com.zhy.changeskin.attr.SkinView;
import com.zhy.changeskin.base.SkinInterface;
import com.zhy.changeskin.callback.ISkinChangingCallback;
import com.zhy.changeskin.modle.SkinInfo;
import com.zhy.changeskin.utils.PrefUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhy on 15/9/22.
 */
public class SkinManager {
    private Context mContext;
    private ResourceManager mResourceManager;
    private PrefUtils mPrefUtils;

    private SkinInfo mCurSkinInfo = SkinInfo.defaultSkin;


    private List<SkinInterface> mInterFaces = new ArrayList<>();

    private SkinManager() {
    }

    private static class SingletonHolder {
        static SkinManager sInstance = new SkinManager();
    }

    public static SkinManager getInstance() {
        return SingletonHolder.sInstance;
    }


    public void init(Context context) {
        mContext = context.getApplicationContext();
        mPrefUtils = new PrefUtils(mContext);

        SkinInfo SkinInfo = mPrefUtils.getSkin();

        if (!validPluginParams(SkinInfo)) {
            mCurSkinInfo = SkinInfo.defaultSkin;
            return;
        }
        try {
            loadPlugin(SkinInfo);
        } catch (Exception e) {
            mPrefUtils.clear();
            e.printStackTrace();
        }
    }

    private PackageInfo getPackageInfo(String skinPluginPath) {
        PackageManager pm = mContext.getPackageManager();
        return pm.getPackageArchiveInfo(skinPluginPath, PackageManager.GET_ACTIVITIES);
    }


    /**
     * 加载皮肤插件资源
     *
     * @throws Exception
     */
    private void loadPlugin(SkinInfo SkinInfo) throws Exception {
        mResourceManager = generateResourceManager(SkinInfo);
    }

    private boolean validPluginParams(SkinInfo SkinInfo) {
        if (TextUtils.isEmpty(SkinInfo.getPluginPath()) || TextUtils.isEmpty(SkinInfo.getPckName())) {
            return false;
        }

        File file = new File(SkinInfo.getPluginPath());
        if (!file.exists())
            return false;

        PackageInfo info = getPackageInfo(SkinInfo.getPluginPath());
        if (!info.packageName.equals(SkinInfo.getPckName()))
            return false;
        return true;
    }

    private void checkPluginParamsThrow(SkinInfo SkinInfo) {
        if (!validPluginParams(SkinInfo)) {
            throw new IllegalArgumentException("skinPluginPath or skinPkgName not valid ! ");
        }
    }

    /**
     * 清除皮肤
     */
    public void removeAnySkin() {
        mCurSkinInfo = SkinInfo.defaultSkin;
        clearPluginInfo();
        notifyChangedListeners();
    }


    public ResourceManager getResourceManager() {
        mResourceManager = generateResourceManager(mCurSkinInfo);
        return mResourceManager;
    }


    /**
     * 应用内换肤，传入资源区别的后缀
     */
    public void changeSkin(SkinInfo skinInfo) {
        clearPluginInfo();//clear before
        mCurSkinInfo = skinInfo;
        mPrefUtils.saveSkin(skinInfo);
        notifyChangedListeners();
    }

    private void clearPluginInfo() {
        mCurSkinInfo = SkinInfo.defaultSkin;
        mPrefUtils.clear();
    }

    /**
     * 保存皮肤信息到本地
     */
    private void updatePluginInfo(SkinInfo SkinInfo) {
        mPrefUtils.saveSkin(SkinInfo);
    }

    /**
     * 根据suffix选择插件内某套皮肤，默认为""
     *
     * @param callback
     */
    public void changeSkin(final SkinInfo skinInfo, ISkinChangingCallback callback) {
        if (callback == null)
            callback = ISkinChangingCallback.DEFAULT_SKIN_CHANGING_CALLBACK;
        final ISkinChangingCallback skinChangingCallback = callback;

        skinChangingCallback.onStart();

        try {
            checkPluginParamsThrow(skinInfo);
        } catch (IllegalArgumentException e) {
            skinChangingCallback.onError(new RuntimeException("checkPlugin occur error"));
            return;
        }

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    loadPlugin(skinInfo);
                    return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }

            }

            @Override
            protected void onPostExecute(Integer res) {
                if (res == 0) {
                    skinChangingCallback.onError(new RuntimeException("loadPlugin occur error"));
                    return;
                }
                try {
                    updatePluginInfo(skinInfo);
                    mCurSkinInfo = skinInfo;
                    notifyChangedListeners();
                    skinChangingCallback.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    skinChangingCallback.onError(e);
                }

            }
        }.execute();
    }

//    /**
//     * 为activity更新皮肤
//     * <p>
//     * 每次换肤,都会重复查找需要换肤的view__性能问题
//     *
//     * @param activity
//     */
//    public void apply(Activity activity) {
//        List<SkinView> skinViews = SkinAttrSupport.getSkinViews(activity);
//        if (skinViews == null) return;
//        for (SkinView skinView : skinViews) {
//            skinView.apply();
//        }
//    }

    public void register(final SkinInterface skinInterface) {
        mInterFaces.add(skinInterface);
        skinInterface.apply();
    }

    public void unregister(Activity activity) {
        mInterFaces.remove(activity);
    }

    public void notifyChangedListeners() {
        for (SkinInterface skinInterface : mInterFaces) {
            skinInterface.apply();
        }
    }

    /**
     * apply for dynamic construct view
     *
     * @param view
     */
    public void injectSkin(View view) {
        List<SkinView> skinViews = new ArrayList<SkinView>();
        SkinAttrSupport.addSkinViews(view, skinViews);
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }

    public void applyView(View view, SkinInfo SkinInfo) {
        List<SkinView> skinViews = SkinAttrSupport.getSkinViews(view);
        for (SkinView skinView : skinViews) {
            skinView.apply(generateResourceManager(SkinInfo));
        }
    }

    public ResourceManager generateResourceManager(SkinInfo skinInfo) {

        if (skinInfo.isUsePlugin()) {
            try {
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skinInfo.getPluginPath());

                Resources superRes = mContext.getResources();
                return
                        new ResourceManager(
                                new Resources(assetManager, superRes.getDisplayMetrics()
                                        , superRes.getConfiguration())
                                , skinInfo.getPckName()
                                , skinInfo.getSuffix());
            } catch (Exception e) {
                Log.e("skin", "加载皮肤插件失败,请检查皮肤信息");
                Log.e("skininfo", skinInfo.toString());
                e.printStackTrace();
                return null;
            }
        } else {
            return new ResourceManager(mContext.getResources(), mContext.getPackageName(), mCurSkinInfo.getSuffix());
        }
    }

    public SkinInfo getCurSkin() {
        return mCurSkinInfo;
    }
}
