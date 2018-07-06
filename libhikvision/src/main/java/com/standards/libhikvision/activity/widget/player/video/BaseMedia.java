package com.standards.libhikvision.activity.widget.player.video;

import android.view.SurfaceHolder;

import com.hikvision.sdk.net.bean.CustomRect;
import com.hikvision.sdk.net.bean.PlaybackSpeed;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.standards.libhikvision.activity.widget.player.listener.OnPlayCallBack;

import java.util.Calendar;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <这个类是播放器的基类，主要是用于抽取播放器经常使用的几个方法，播放，暂停等，
 * 有部分方法是直播专用的实现，部分则是回放需要做的实现，具体可以看注释。
 * 虽然有很多接口暂时没用到，先开放了。防止以后播放器功能需要持续集成>
 * @date: 2018/6/25 10:35
 */

public abstract class BaseMedia {
    public static final int MODE_PLAY = 1;
    public static final int MODE_PLAY_BACK = 2;

    public static final int PLAY_STATUS_PLAYING = 1;
    public static final int PLAY_STATUS_PAUSE = 2;
    public static final int PLAY_STATUS_STOP = 3;


    private int playStatus = 3;
    private String title;
    protected boolean isLockPlay = false;
    protected boolean isRecorDing;
    protected OnPlayCallBack mOnPlayCallBack;

    public BaseMedia(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

    /**
     * 播放
     */
    public abstract void play();

    /**
     * 停止
     */
    public abstract void stop();

    /**
     * 拍照
     *
     * @param filePath
     * @param picName
     * @return
     */
    public abstract int capture(String filePath, String picName);

    /**
     * 录像
     *
     * @param filePath
     * @param recordName
     * @return
     */
    public abstract int record(String filePath, String recordName);

    /**
     * 关闭录像
     *
     * @return
     */
    public abstract void stopRecord();

    /**
     * 开启音频
     *
     * @return
     */
    public abstract boolean openAudio();

    /**
     * 关闭音频
     *
     * @return
     */
    public abstract boolean closeAudio();

    /**
     * 电子放大操作
     *
     * @return
     */
    public abstract boolean zoom(boolean enable, CustomRect original, CustomRect current);


    //以下功能为预览相关功能

    /**
     * 云台控制 该功能为预览时的实现，回放无需实现此功能
     *
     * @return
     */
    public void sendPTZCtrlCommand(boolean isNormal, String action, int command, int presetIndex, OnVMSNetSDKBusiness vmsNetSDKBusiness) {


    }

    /**
     * 获取对讲通道总数
     *
     * @return
     */
    public int getTalkChannel() {
        return 0;
    }

    /**
     * 开启对讲
     *
     * @param channel
     */
    public void openTalk(int channel) {

    }

    /**
     * 关闭对讲
     */
    public void closeTalk() {

    }

    //以下功能为回放相关功能


    /**
     * 获取回放监控点详情
     *
     * @param
     */
    public void getBackPlayCameraInfo() {

    }

    /**
     * 查询回放录像片段
     */
    public void queryRecordSegment(Calendar queryStartTime,
                                   Calendar queryEndTime,
                                   int storageType,
                                   String guid) {
    }

    /**
     * 回放暂停
     */
    public void pause() {
    }

    /**
     * 回放恢复
     */
    public void resume() {
    }


    /**
     * 回放获取当前播放时间
     */
    public long getCurrentPlayTime() {
        return 0;
    }

    /**
     * 回放更新容器
     *
     * @param surfaceHolder
     * @return
     */
    public boolean setVideoWindowOpt(SurfaceHolder surfaceHolder) {

        return false;
    }

    /**
     * 回放倍数控制
     *
     * @param speed 回放速度
     * @return
     */
    public void setPlayBackSpeed(@PlaybackSpeed.Speed int speed) {

    }

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    /**
     * 这个主要用来标志当前是直播还是回放
     * 其实通过具体实现对象的instanceof 就能看出来的。这里为了避免混淆特别添加了两种模式
     * {@link #MODE_PLAY} 直播
     * {@link #MODE_PLAY_BACK} 回放
     *
     * @return
     */
    public abstract int getPlayMode();


    public abstract int getWindow();

    public void setLockPlay(boolean lockPlay) {
        isLockPlay = lockPlay;
    }


    public boolean isRecording() {
        return isRecorDing;
    }

    public void setRecording(boolean recorDing) {
        isRecorDing = recorDing;
    }

    public void setOnPlayCallBack(OnPlayCallBack onPlayCallBack) {
        this.mOnPlayCallBack = onPlayCallBack;
    }
}
