package com.standards.libhikvision.activity.widget.player.video;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.CustomRect;
import com.hikvision.sdk.net.bean.PlaybackSpeed;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.standards.libhikvision.activity.widget.player.listener.OnPlayCallBack;

import java.util.Calendar;

/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */

public abstract class BaseMedia {
    public static final int MODE_PLAY = 1;
    public static final int MODE_PLAY_BACK = 2;
    public static final int MODE_LOCAL_PLAY = 3;

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


    /**
     * 播放视频标题
     *
     * @return
     */
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

    /**
     * 这个用户surfaceViewHolder改变的时候用，比如列表中的视频如何做到全屏？
     * 通过传递一个全屏的SurfaceView的Holder就可以。替换回列表就在全屏中传入列表的surfaceHolder
     *
     * @param surfaceHolder
     */
    public void setViewHolder(SurfaceHolder surfaceHolder) {
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

    /**
     * 本地播放相关
     *
     * @return
     */
    public long getTotalTime() {
        return 0;
    }

    /**
     * 当前播放时间 这个功能只有本地播放和回放需要加入
     *
     * @return
     */
    public long getCurrentPlayTime() {
        return 0;
    }

    /**
     * 起始时间 这个功能只有本地播放录像和回放需要加入
     * 1.回放的过程中的时间基本是 一个日期的时间戳
     * 2.录像的起始时间就默认为0
     *
     * @return
     */
    public long getStartTime() {
        return 0;
    }

    /**
     * 设置当前播放时间 这个功能只有本地播放录像和回放需要加入
     * 值得注意的是海康在本地录像和回放中两个需要传入的参数不一样(或者说根本没有回放选择时间的接口)
     * {@link VMSNetSDK#setLocalCurrentFrame(double)}
     * {@link VMSNetSDK#startPlayBackOpt(int, SurfaceView, String, Calendar, Calendar, OnVMSNetSDKBusiness)}
     * 在获取了当前播放时间后，录像播放是需要传入当前时间和总时间的比例
     * 回放则是停止，并以这个时间节点为开始时间播放~~(淡淡的忧伤）
     *
     * @return
     */
    public boolean setCurrentTime(long currentTime) {
        return false;
    }

    /**
     * 播放状态，适配播放器状态目前有三种模式{@link #PLAY_STATUS_PLAYING,#PLAY_STATUS_PAUSE,#PLAY_STATUS_STOP}
     * 分别是播放中，暂停播放，停止播放
     *
     * @return
     */
    public int getPlayStatus() {
        return playStatus;
    }

    /**
     * 实现中在需要切换播放状态的地方调用这个方法
     *
     * @param playStatus
     */
    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    /**
     * 这个主要用来标志当前是直播还是回放
     * 其实通过具体实现对象的instanceof 就能看出来的。这里为了避免混淆特别添加了两种模式
     * {@link #MODE_PLAY} 直播
     * {@link #MODE_PLAY_BACK} 回放
     * {@link #MODE_LOCAL_PLAY} 本地播放
     *
     * @return
     */
    public abstract int getPlayMode();

    /**
     * 获取播放窗口，预览和回放需要这个接口
     * @return
     */

    public abstract int getWindow();

    /**
     * 锁定播放，在解锁前不能做任何播放操作。
     * @param lockPlay
     */
    public void setLockPlay(boolean lockPlay) {
        isLockPlay = lockPlay;
    }

    /**
     * 当前是否处于录像状态
     * @return
     */
    public boolean isRecording() {
        return isRecorDing;
    }

    /**
     * 设置录像状态
     * @param recorDing
     */
    public void setRecording(boolean recorDing) {
        isRecorDing = recorDing;
    }

    public void setOnPlayCallBack(OnPlayCallBack onPlayCallBack) {
        this.mOnPlayCallBack = onPlayCallBack;
    }


}
