package com.lhd.appframe.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lhd.appframe.base.BaseFragment;

/**
 * Created by lihuaidong on 2017/5/16 10:02.
 * 微信：lhd520ssp
 * QQ:414320737
 * 作用：政要
 */
public class AffairFragment extends BaseFragment
{
    private static final String TAG = AffairFragment.class.getSimpleName();
    private TextView textView;
    @Override
    public View initView()
    {

        textView = new TextView(mContext);
        textView.setTextColor(Color.RED);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);

        return textView;

    }

    @Override
    public void initData()
    {
        super.initData();
        Log.e(TAG, "政要数据被初始化了");
        textView.setText("我是政要");
    }
}
