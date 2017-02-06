package com.zhy.changeskin.base;

/**
 * Created by YZL on 2017/2/6.
 */
public interface SkinInterface {

    /**
     * 换肤
     */
    void apply();

    /**
     * 是否需要换肤
     * @return
     */
    boolean isNeedSkin();

    /**
     * 懒加载 ,在onResume方法中进行换肤?
     * @return
     */
    boolean isNeedLazyApply();
}
