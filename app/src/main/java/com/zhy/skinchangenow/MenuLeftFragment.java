package com.zhy.skinchangenow;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zhy.changeskin.SkinManager;
import com.zhy.changeskin.modle.SkinInfo;

import java.io.File;

public class MenuLeftFragment extends Fragment implements View.OnClickListener {
    private String mSkinPkgPath = Environment.getExternalStorageDirectory() + File.separator + "skin_plugin.skin";
    private View mInnerChange01;
    private View mInnerChange02;
    private SkinInfo skinInfo = new SkinInfo("ceshi1", "com.imooc.skin_plugin", mSkinPkgPath);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_left_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInnerChange01 = view.findViewById(R.id.id_rl_innerchange01);
        mInnerChange01.setOnClickListener(this);

        mInnerChange02 = view.findViewById(R.id.id_rl_innerchange02);
        mInnerChange02.setOnClickListener(this);

        view.findViewById(R.id.id_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.zhy.changeskin.SkinManager.getInstance().removeAnySkin();
            }
        });

        view.findViewById(R.id.id_changeskin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.zhy.changeskin.SkinManager.getInstance().changeSkin(skinInfo, new com.zhy.changeskin.callback.ISkinChangingCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getActivity(), "换肤失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(getActivity(), "换肤成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_rl_innerchange01:
                SkinInfo skinInfo = new SkinInfo("red", "red");
                SkinManager.getInstance().changeSkin(skinInfo);
                break;
            case R.id.id_rl_innerchange02:
                SkinInfo skinInfo1 = new SkinInfo("green", "green");
                SkinManager.getInstance().changeSkin(skinInfo1);
                break;
        }

    }
}
