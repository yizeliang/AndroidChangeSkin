package com.zhy.skinchangenow;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.changeskin.SkinManager;
import com.zhy.changeskin.base.SkinAcitivity;
import com.zhy.changeskin.modle.SkinInfo;

import java.io.File;

public class TestTagActivity extends SkinAcitivity {
    private String mSkinPkgPath = Environment.getExternalStorageDirectory() + File.separator + "skin_plugin.skin";
    private SkinInfo skinInfo = new SkinInfo("ceshi1", "com.imooc.skin_plugin", mSkinPkgPath);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tag);

        findViewById(R.id.test_lazy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurSkinId == null) {
                    return;
                }
                if (mCurSkinId.equals("red")) {
                    SkinManager.getInstance().changeSkin(new SkinInfo("green", "green"));
                } else {
                    SkinManager.getInstance().changeSkin(new SkinInfo("red", "red"));
                }
//                SkinManager.getInstance().changeSkin(skinInfo, null);

            }
        });
    }

    public void addNewView(View view) {
        //建议通过xml inflater
        TextView tv = new TextView(this);
        tv.setTag("skin:item_text_color:textColor");
        tv.setTextColor(getResources().getColorStateList(R.color.item_text_color));
        tv.setText("dymaic add!");

        ((ViewGroup) findViewById(R.id.id_container)).addView(tv);
        SkinManager.getInstance().injectSkin(tv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isNeedSkin() {
        return true;
    }

    @Override
    public boolean isSkinLazyApply() {
        return false;
    }
}
