package com.standards.libhikvision.activity.widget.player.listener;

/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */

public interface OnPlayCallBack {
    void onFailure();

    void onStatusCallback(int var1);

    void onSuccess(Object var1);

    void onStart();
}
