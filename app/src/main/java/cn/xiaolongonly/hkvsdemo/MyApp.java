package cn.xiaolongonly.hkvsdemo;

import android.app.Application;

import com.standards.libhikvision.ConfigInit;

/**
 * @author linciping
 * @time 2018/6/11
 * @note
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigInit.init(this);
    }
}
