```
[![PersonBlog](https://img.shields.io/badge/PersonBlog-@xiaolongonly-blue.svg?style=flat)](http://xiaolongonly.cn/)

# 海康威视视频监控播放器

# 界面效果如下

![直播](https://raw.githubusercontent.com/guoxiaolongonly/TravelGuaGuide/master/screen/launcher.png)
！[回放]()
    
# 使用方式

1. 需要设置界面功能点击回调，可以设置播放状态回调
​```
  player.setOnVideoControlListener(new OnVideoControlListener() {
            @Override
            public void onStartPlayClick() {
                player.startPlay();
            }

            @Override
            public void onBackClick() {
                onBackPressed();
            }

            @Override
            public void onFullScreenClick() {
                DisplayUtils.toggleScreenOrientation(PlayerActivity.this);
            }

            @Override
            public void onScreenShotClick() {
                int opt = player.screenShot();
                showHint(HintUtil.getLiveScreenShotHint(opt));
            }

            @Override
            public void onRecordClick() {
                recordOpt();
            }

            @Override
            public void onErrorClick(int errorStatus) {
                player.startPlay();
            }

            @Override
            public void onHiQualityClick() {
                player.setStreamType(SDKConstant.LiveSDKConstant.MAIN_HIGH_STREAM);
                player.startPlay();
            }

            @Override
            public void onFluencyClick() {
                player.setStreamType(SDKConstant.LiveSDKConstant.SUB_STANDARD_STREAM);
                player.startPlay();
            }
        });
        player.setOnPlayCallBack(new OnPlayCallBack() {
            @Override
            public void onFailure() {
            
            }

            @Override
            public void onStatusCallback(int var1) {

            }

            @Override
            public void onSuccess(Object var1) {
              
            }

            @Override
            public void onStart() {

            }
        });
​```

Activity需要在Manifest中添加横竖屏切换配置

​``` java
 <activity
            android:name="com.standards.libhikvision.ui.PlayerActivity"
            android:configChanges="orientation|screenSize|keyboardHidden|keyboard|screenLayout"
            android:screenOrientation="portrait" />
​```

3.基础实现带截图，录像等功能，可以根据需求进行扩展，详情请看
​```
com.standards.libhikvision.activity.widget.player.video.BaseMedia;
播放器基类

com.standards.libhikvision.activity.widget.player.view.LuckyVideoControllerView;
控制类

​```

#附

有其他问题欢迎交流。

# License

Apache2.0


​```
Copyright 2018 Xiaolong 

​```
```

