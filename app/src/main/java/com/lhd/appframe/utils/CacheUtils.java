package com.lhd.appframe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lihuaidong on 2017/5/16 11:11.
 * 微信：lhd520ssp
 * QQ:414320737
 * 作用：缓存工具类
 */
public class CacheUtils
{

    public static void putStrig(Context context, String key, String value)
    {
        SharedPreferences sp = context.getSharedPreferences("news_center", Context
                .MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context context,String key)
    {
        return context.getSharedPreferences("news_center", Context.MODE_PRIVATE).getString(key, "");
    }
}
