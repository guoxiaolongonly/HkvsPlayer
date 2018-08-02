package cn.xiaolongonly.hkvsdemo;

import android.app.Application;

import com.standards.libhikvision.ConfigInit;

/**
 * <描述功能>
 * @author linciping
 * @version v1.0
 * @since: 2018/6/11
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigInit.init(this);
    }
}
