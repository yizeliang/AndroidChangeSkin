package com.zhy.changeskin.attr;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.changeskin.ResourceManager;
import com.zhy.changeskin.SkinManager;


/**
 * Created by zhy on 15/9/28.
 */
public enum SkinAttrType {
    BACKGROUND("background") {
        @Override
        public void apply(View view, String resName) {
//            Drawable drawable = getResourceManager().getDrawableByName(resName);
//            if (drawable != null) {
//                view.setBackgroundDrawable(drawable);
//            } else {
//                try {
//                    int color = getResourceManager().getColor(resName);
//                    view.setBackgroundColor(color);
//                } catch (Resources.NotFoundException ex) {
//                    ex.printStackTrace();
//                }
//            }
            apply(view, resName, null);
        }

        @Override
        public void apply(View view, String resName, ResourceManager resourceManager) {
            int paddingLeft = view.getPaddingLeft();
            int paddingTop = view.getPaddingLeft();
            int paddingRight = view.getPaddingLeft();
            int paddingBottom = view.getPaddingLeft();

            Drawable drawable = null;
            if (resourceManager == null) {
                drawable = getResourceManager().getDrawableByName(resName);
            } else {
                drawable = resourceManager.getDrawableByName(resName);
            }
            if (drawable != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(drawable);
                } else {
                    view.setBackgroundDrawable(drawable);
                }
                view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            } else {
                try {
                    int color;
                    if (resourceManager == null) {
                        color = getResourceManager().getColor(resName);
                    } else {
                        color = resourceManager.getColor(resName);
                    }
                    view.setBackgroundColor(color);
                    view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                } catch (Resources.NotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    },
    COLOR("textColor") {
        @Override
        public void apply(View view, String resName) {
            apply(view, resName, null);
        }

        @Override
        public void apply(View view, String resName, ResourceManager resourceManager) {
            ColorStateList colorList = null;
            if (resourceManager == null) {
                colorList = getResourceManager().getColorStateList(resName);
            } else {
                colorList = resourceManager.getColorStateList(resName);
            }
            if (colorList == null) return;
            ((TextView) view).setTextColor(colorList);
        }
    },

    SRC("src") {
        @Override
        public void apply(View view, String resName) {
            if (view instanceof ImageView) {
                apply(view, resName, null);
            }

        }

        @Override
        public void apply(View view, String resName, ResourceManager resourceManager) {
            Drawable drawable = null;
            if (resourceManager == null) {
                drawable = getResourceManager().getDrawableByName(resName);
            } else {
                drawable = resourceManager.getDrawableByName(resName);
            }
            if (drawable == null) return;
            ((ImageView) view).setImageDrawable(drawable);
        }
    }, DIVIDER("divider") {
        @Override
        public void apply(View view, String resName) {
            apply(view, resName, null);
        }

        @Override
        public void apply(View view, String resName, ResourceManager resourceManager) {
            if (view instanceof ListView) {
                Drawable divider = null;
                if (resourceManager == null) {
                    divider = getResourceManager().getDrawableByName(resName);
                } else {
                    divider = getResourceManager().getDrawableByName(resName);
                }
                if (divider == null) return;
                ((ListView) view).setDivider(divider);
            }
        }
    };

    String attrType;

    SkinAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrType() {
        return attrType;
    }

    public abstract void apply(View view, String resName);

    public abstract void apply(View view, String resName, ResourceManager resourceManager);

    public ResourceManager getResourceManager() {
        return SkinManager.getInstance().getResourceManager();
    }

}
