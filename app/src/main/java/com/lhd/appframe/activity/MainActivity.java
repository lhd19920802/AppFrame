package com.lhd.appframe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lhd.appframe.R;
import com.lhd.appframe.base.BaseFragment;
import com.lhd.appframe.domain.NewsCenterBean;
import com.lhd.appframe.fragment.AffairFragment;
import com.lhd.appframe.fragment.HomeFragment;
import com.lhd.appframe.fragment.NewsFragment;
import com.lhd.appframe.fragment.ServiceFragment;
import com.lhd.appframe.fragment.SettingFragment;
import com.lhd.appframe.utils.CacheUtils;
import com.lhd.appframe.utils.DensityUtil;
import com.lhd.appframe.utils.Url;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SlidingFragmentActivity
{

    private FrameLayout fl_main;
    private RadioGroup rg_main;
    private BaseFragment preFragment;
    private int currentPosition;

    private ListView lv_left_menu;
    private List<BaseFragment> baseFragments;
    private List<NewsCenterBean.DataBean> data;
    //点击某个ListView item的位置
    private int curPosition;
    private MyBaseAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置主页
        setContentView(R.layout.activity_main);

        baseFragments = new ArrayList<>();
        initFragments();
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);

        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        //设置左侧菜单
        setBehindContentView(R.layout.left_menu);
        lv_left_menu = (ListView) findViewById(R.id.lv_left_menu);



        SlidingMenu slidingMenu = getSlidingMenu();

        slidingMenu.setMode(SlidingMenu.LEFT);

        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        slidingMenu.setBehindOffset(DensityUtil.dip2px(this, 200));

        rg_main.check(R.id.rb_home);

        String saveJson = CacheUtils.getString(this, Url.NEWS_URL);
        if (!TextUtils.isEmpty(saveJson))
        {
            processData(saveJson);
        }
        //提供数据
        getDataFromNet();

    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener

    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            //变色
            curPosition = position;
            adapter.notifyDataSetChanged();

            //开关变换
            getSlidingMenu().toggle();
        }
    }

    private void getDataFromNet()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Url.NEWS_URL, new Response
                .Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                Log.e("TAG", "请求数据成功====" + s);
                //保存数据
                CacheUtils.putStrig(MainActivity.this, Url.NEWS_URL, s);
                processData(s);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                Log.e("TAG", "请求数据失败===" + volleyError.getMessage());
            }
        })
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response)
            {
                try
                {
                    String parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }

        };

        requestQueue.add(stringRequest);


    }

    //解析数据并显示
    private void processData(String json)
    {
        NewsCenterBean newsCenterBean = parseJson(json);
        Log.e("TAG", "解析数据成功==" + newsCenterBean.getData().get(1).getTitle());
        data = newsCenterBean.getData();
        adapter = new MyBaseAdapter();
        lv_left_menu.setAdapter(adapter);


        lv_left_menu.setOnItemClickListener(new MyOnItemClickListener());

    }

    //解析json
    private NewsCenterBean parseJson(String json)
    {

        return new Gson().fromJson(json, NewsCenterBean.class);
    }


    private void initFragments()
    {

        baseFragments.add(new HomeFragment());
        baseFragments.add(new NewsFragment());
        baseFragments.add(new ServiceFragment());
        baseFragments.add(new AffairFragment());
        baseFragments.add(new SettingFragment());

    }

    class MyBaseAdapter extends BaseAdapter

    {
        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder;
            if (convertView == null)
            {
                viewHolder = new ViewHolder();
                convertView = View.inflate(MainActivity.this, R.layout.item_main, null);
                viewHolder.tv_item_title = (TextView) convertView.findViewById(R.id.tv_item_title);
                convertView.setTag(viewHolder);

            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }



            NewsCenterBean.DataBean dataBean = data.get(position);
            viewHolder.tv_item_title.setText(dataBean.getTitle());

                        if (curPosition == position)
                        {
                            viewHolder.tv_item_title.setEnabled(true);
                        }
                        else
                        {
                            viewHolder.tv_item_title.setEnabled(false);
                        }
            return convertView;
        }
        class ViewHolder
        {
            TextView tv_item_title;
        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener

    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            switch (checkedId)
            {
                case R.id.rb_home:
                    currentPosition = 0;
                    //只有新闻页面能滑出左侧菜单
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_newscenter:
                    currentPosition = 1;
                    isEnableSlidingMenu(true);
                    break;
                case R.id.rb_smartservice:
                    currentPosition = 2;
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_govaffair:
                    currentPosition = 3;
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_setting:
                    currentPosition = 4;
                    isEnableSlidingMenu(false);
                    break;
            }
            BaseFragment baseFragment = baseFragments.get(currentPosition);
            switchFragment(preFragment, baseFragment);
        }


    }

    /**
     * 是否滑出菜单
     *
     * @param isSliding
     */
    private void isEnableSlidingMenu(boolean isSliding)
    {
        SlidingMenu slidingMenu = getSlidingMenu();
        if (isSliding)
        {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }
        else
        {

            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

    }

    private void switchFragment(BaseFragment tempFragment, BaseFragment fragment)
    {
        if (tempFragment != fragment)
        {
            preFragment = fragment;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!fragment.isAdded())
            {
                //隐藏tempFragment
                if (tempFragment != null)
                {
                    transaction.hide(tempFragment);
                }
                //添加新的fragment 并提交
                if (fragment != null)
                {
                    transaction.add(R.id.fl_main, fragment).commit();
                }


            }
            else
            {//隐藏tempFragment
                if (tempFragment != null)
                {
                    transaction.hide(tempFragment);
                }
                //显示新的fragment 并提交
                if (fragment != null)
                {
                    transaction.show(fragment).commit();
                }


            }
        }
    }
}
