package com.standards.libhikvision.activity.widget.player.listener;

import java.io.File;

/**
 * 视频控制监听
 */
public interface OnVideoControlListener {

    /**
     * 开始播放按钮
     */
    void onStartPlayClick();

    /**
     * 返回
     */
    void onBackClick();

    /**
     * 全屏
     */
    void onFullScreenClick();

    void onScreenShotClick();


    void onRecordClick();

    /**
     * 错误回调
     *
     * @param errorStatus
     */
    void onErrorClick(int errorStatus);


    void onHiQualityClick();

    void onFluencyClick();

    void onPreViewClick(File preImage);
}
