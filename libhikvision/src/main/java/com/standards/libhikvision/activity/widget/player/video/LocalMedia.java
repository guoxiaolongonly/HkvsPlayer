package com.standards.libhikvision.activity.widget.player.video;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.CustomRect;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;

/**
 * author: xiaolong
 * create on: 2018/7/10 16:30
 * description:
 */

public class LocalMedia extends BaseMedia {
    private String mFilePath;
    private SurfaceView mSurfaceView;
    private long preTotal;
    private long preCurrent = 0;

    public LocalMedia(String title, String filePath, SurfaceView surfaceView) {
        super(title);
        mFilePath = filePath;
        mSurfaceView = surfaceView;
    }

    @Override
    public void play() {
        new Thread(() -> {
            VMSNetSDK.getInstance().playLocalVideo(mFilePath, mSurfaceView, new OnVMSNetSDKBusiness() {
                @Override
                public void onFailure() {
                    if (mOnPlayCallBack != null) {
                        mOnPlayCallBack.onFailure();
                    }
                }

                @Override
                public void onSuccess(Object obj) {
                    if (mOnPlayCallBack != null) {
                        mOnPlayCallBack.onSuccess(obj);
                    }
                    preTotal = getTotalTime();
                    setPlayStatus(PLAY_STATUS_PLAYING);
                }

                @Override
                public void onStatusCallback(int status) {
                    if (mOnPlayCallBack != null) {
                        mOnPlayCallBack.onStatusCallback(status);
                    }
                }
            });
        }).start();
    }

    @Override
    public void stop() {
        VMSNetSDK.getInstance().stopLocalVideo();
        setPlayStatus(PLAY_STATUS_STOP);
    }

    @Override
    public void pause() {
        VMSNetSDK.getInstance().pauseLocalVideo();
        setPlayStatus(PLAY_STATUS_PAUSE);
    }

    @Override
    public void resume() {
        VMSNetSDK.getInstance().resumeLocalVideo();
        setPlayStatus(PLAY_STATUS_PLAYING);
    }

    @Override
    public long getTotalTime() {
        long totalTime = VMSNetSDK.getInstance().getLocalTotalTime();
        return totalTime == -1 ? preTotal : totalTime;
    }

    @Override
    public long getCurrentPlayTime() {
        long current = VMSNetSDK.getInstance().getLocalPlayedTime();
        if (current != -1) {
            preCurrent = current;
        }
        return preCurrent;
    }

    @Override
    public int capture(String filePath, String picName) {
        return VMSNetSDK.getInstance().localCapture(filePath, picName);
    }

    @Override
    public int record(String filePath, String recordName) {
        return 0;
    }

    @Override
    public void stopRecord() {

    }

    @Override
    public boolean openAudio() {
        return VMSNetSDK.getInstance().setLocalAudio(true);
    }

    @Override
    public boolean closeAudio() {
        return VMSNetSDK.getInstance().setLocalAudio(false);
    }


    @Override
    public boolean setCurrentTime(long currentTime) {
        return VMSNetSDK.getInstance().setLocalCurrentFrame((double) currentTime/preTotal);
    }

    @Override
    public void setViewHolder(SurfaceHolder surfaceHolder) {
        VMSNetSDK.getInstance().setLocalVideoHolder(surfaceHolder);
    }

    @Override
    public boolean zoom(boolean enable, CustomRect original, CustomRect current) {
        return false;
    }

    @Override
    public int getPlayMode() {
        return MODE_LOCAL_PLAY;
    }

    @Override
    public int getWindow() {
        return 0;
    }

}
