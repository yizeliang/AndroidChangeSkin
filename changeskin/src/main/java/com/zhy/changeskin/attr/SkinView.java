package com.zhy.changeskin.attr;

import android.content.res.Resources;
import android.view.View;

import com.zhy.changeskin.ResourceManager;

import java.util.List;

/**
 * Created by zhy on 15/9/22.
 */
public class SkinView {
    //    SoftReference<View> viewRef;
    public View view;
    public List<SkinAttr> attrs;

    public SkinView(View view, List<SkinAttr> skinAttrs) {
        this.view = view;
        this.attrs = skinAttrs;
    }

    public void apply() {
        // View view = viewRef.get();
        if (view == null) return;

        for (SkinAttr attr : attrs) {
            attr.apply(view);
        }
    }

    public void apply(ResourceManager resourceManager) {
        // View view = viewRef.get();
        if (view == null) return;

        for (SkinAttr attr : attrs) {
            attr.apply(view,resourceManager);
        }
    }
}
