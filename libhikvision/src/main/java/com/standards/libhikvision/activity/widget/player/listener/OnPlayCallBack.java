package com.standards.libhikvision.activity.widget.player.listener;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/6/28 14:16
 */

public interface OnPlayCallBack {
    void onFailure();

    void onStatusCallback(int var1);

    void onSuccess(Object var1);

    void onStart();
}
