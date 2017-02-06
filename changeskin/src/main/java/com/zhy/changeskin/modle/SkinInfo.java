package com.zhy.changeskin.modle;

/**
 * Created by YZL on 2017/2/6.
 */

public class SkinInfo {
    private String skinId;
    private boolean usePlugin;
    private String pckName;
    private String pluginPath;
    private String suffix;


    public SkinInfo() {
    }

    public SkinInfo(String skinId, String pckName, String pluginPath) {
        this(skinId, pckName, pluginPath, null);
    }

    /**
     * 使用插件
     *
     * @param skinId
     * @param pckName
     * @param pluginPath
     * @param suffix
     */
    public SkinInfo(String skinId, String pckName, String pluginPath, String suffix) {
        this.skinId = skinId;
        this.usePlugin = true;
        this.pckName = pckName;
        this.pluginPath = pluginPath;
        this.suffix = suffix;
    }

    /**
     * 不使用插件
     *
     * @param skinId
     * @param suffix
     */
    public SkinInfo(String skinId, String suffix) {
        this.skinId = skinId;
        this.usePlugin = false;
        this.suffix = suffix;
    }


    public String getSkinId() {
        return skinId;
    }

    public void setSkinId(String skinId) {
        skinId = skinId;
    }

    public boolean isUsePlugin() {
        return usePlugin;
    }

    public void setUsePlugin(boolean usePlugin) {
        this.usePlugin = usePlugin;
    }

    public String getPckName() {
        return pckName;
    }

    public void setPckName(String pckName) {
        this.pckName = pckName;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public String getSuffix() {
        if (suffix == null) {
            return "";
        }
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


    @Override
    public String toString() {
        return "SkinInfo{" +
                "SkinId='" + skinId + '\'' +
                ", usePlugin=" + usePlugin +
                ", pckName='" + pckName + '\'' +
                ", pluginPath='" + pluginPath + '\'' +
                ", suffix='" + suffix + '\'' +
                '}';
    }

    public static final SkinInfo defaultSkin = new SkinInfo("default", "");
}
