package com.example.shiju;

import android.app.Application;

import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.tabbar.EasyIndicator;

public class XuiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志

    }

}
