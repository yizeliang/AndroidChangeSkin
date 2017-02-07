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
        /**
         * 如何没有获取到保存的皮肤,就设置为默认皮肤
         */
        SkinInfo skinInfo = mPrefUtils.getSkin();

        if (!validPluginParams(skinInfo)) {
            mCurSkinInfo = SkinInfo.defaultSkin;
        }
        try {
            loadPlugin(skinInfo);
        } catch (Exception e) {
            mPrefUtils.clear();
            e.printStackTrace();
        }
    }

    /**
     * 获取一个插件包的包名
     *
     * @param skinPluginPath
     * @return
     */
    private PackageInfo getPackageInfo(String skinPluginPath) {
        PackageManager pm = mContext.getPackageManager();
        return pm.getPackageArchiveInfo(skinPluginPath, PackageManager.GET_ACTIVITIES);
    }


    /**
     * 加载皮肤插件资源
     *
     * @throws Exception
     */
    private void loadPlugin(SkinInfo skinInfo) throws Exception {
        mResourceManager = generateResourceManager(skinInfo);
    }

    /**
     * 检查皮肤插件是否完整
     * 包名_路径_文件是否存在
     *
     * @param skinInfo
     */
    private boolean validPluginParams(SkinInfo skinInfo) {
        if (TextUtils.isEmpty(skinInfo.getPluginPath()) || TextUtils.isEmpty(skinInfo.getPckName())) {
            return false;
        }

        File file = new File(skinInfo.getPluginPath());
        if (!file.exists())
            return false;

        PackageInfo info = getPackageInfo(skinInfo.getPluginPath());
        if (!info.packageName.equals(skinInfo.getPckName()))
            return false;
        return true;
    }

    /**
     * 检查皮肤插件是否完整
     *
     * @param skinInfo
     */
    private void checkPluginParamsThrow(SkinInfo skinInfo) {
        if (!validPluginParams(skinInfo)) {
            throw new IllegalArgumentException("skinPluginPath or skinPkgName not valid ! ");
        }
    }

    /**
     * 清除皮肤
     */
    public void removeAnySkin() {
        mCurSkinInfo = SkinInfo.defaultSkin;
        clearPluginInfo();
        mResourceManager = generateResourceManager(mCurSkinInfo);
        notifyChangedListeners();
    }

    /**
     * 刷新皮肤
     */
    public void refrshSkin() {
        changeSkin(mCurSkinInfo);
    }

    public void changeSkin(SkinInfo skinInfo) {
        changeSkin(skinInfo, null);
    }

    public void changeSkin(SkinInfo skinInfo, ISkinChangingCallback callback) {

        if (callback == null) {
            callback = ISkinChangingCallback.DEFAULT_SKIN_CHANGING_CALLBACK;
        }

        if (skinInfo.isUsePlugin()) {
            changeSkinPlugin(skinInfo, callback);
        } else {
            changeSkinInside(skinInfo, callback);
        }
    }

    public ResourceManager getResourceManager() {
        return mResourceManager;
    }


    /**
     * 应用内换肤，传入资源区别的后缀
     */
    private void changeSkinInside(SkinInfo skinInfo, ISkinChangingCallback callback) {
        callback.onStart();
        clearPluginInfo();//clear before
        mCurSkinInfo = skinInfo;
        mResourceManager = generateResourceManager(skinInfo);
        mPrefUtils.saveSkin(skinInfo);
        notifyChangedListeners();
        callback.onComplete();
    }

    private void clearPluginInfo() {
        mCurSkinInfo = SkinInfo.defaultSkin;
        mPrefUtils.clear();
    }

    /**
     * 保存皮肤信息到本地
     */
    private void updatePluginInfo(SkinInfo skinInfo) {
        mPrefUtils.saveSkin(skinInfo);
        mCurSkinInfo = skinInfo;
    }

    /**
     * 根据suffix选择插件内某套皮肤，默认为""
     *
     * @param callback
     */
    private void changeSkinPlugin(final SkinInfo skinInfo, ISkinChangingCallback callback) {
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
                    notifyChangedListeners();
                    skinChangingCallback.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    skinChangingCallback.onError(e);
                }

            }
        }.execute();
    }

    public void register(SkinInterface skinInterface) {
        mInterFaces.add(skinInterface);
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

    public void injectSkin(View view, SkinInfo skinInfo) {
        List<SkinView> skinViews = SkinAttrSupport.getSkinViews(view);
        for (SkinView skinView : skinViews) {
            skinView.apply(generateResourceManager(skinInfo));
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
