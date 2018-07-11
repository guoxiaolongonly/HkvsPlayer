package com.standards.libhikvision.activity.widget.player.video;

import android.content.Context;
import android.view.SurfaceView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.CustomRect;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function 思考了一下  在播放器中其实并没有多大需求要加入OnVMSNetSDKBusiness
 * 部分特殊情况可能会有这些需求，比如在开始播放的时候我们可能需要通过回调来知道这次的开启是成功还是失败的
 * 从而确定我们的UI加载状态这边先给几个地方加上回调如果后续有这方面需求，可以继承此类，重写相应方法插入回调。
 * @date: 2018/6/25 11:10
 */

public class LiveMedia extends BaseMedia {
    private SurfaceView mSurfaceView;
    private SubResourceNodeBean mSubResourceNodeBean;
    private int mWindow;
    private Context mContext;
    private int streamType = 0;//码流为 1：主码流  2：子码流2  以此类推

    public LiveMedia(SurfaceView surfaceView, SubResourceNodeBean subResourceNodeBean, String title, int window) {
        super(title);
        mSurfaceView = surfaceView;
        mSubResourceNodeBean = subResourceNodeBean;
        mWindow = window;
        mContext = surfaceView.getContext();
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public int getStreamType() {
        return streamType;
    }

    @Override
    public void play() {
        if (isLockPlay) {
            return;
        }
        mOnPlayCallBack.onStart();
        new Thread(() -> VMSNetSDK.getInstance().startLiveOpt(mWindow, mSubResourceNodeBean.getSysCode(), mSurfaceView,
                streamType, new OnVMSNetSDKBusiness() {
                    @Override
                    public void onFailure() {
                        if (mOnPlayCallBack != null) {
                            mOnPlayCallBack.onFailure();
                        }
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        setPlayStatus(PLAY_STATUS_PLAYING);
                        if (mOnPlayCallBack != null) {
                            mOnPlayCallBack.onSuccess(obj);
                        }
                    }

                })).start();

    }


    @Override
    public void stop() {
        VMSNetSDK.getInstance().stopLiveOpt(mWindow);
        setPlayStatus(PLAY_STATUS_STOP);
    }

    @Override
    public int capture(String filePath, String picName) {
        return VMSNetSDK.getInstance().captureLiveOpt(mWindow, filePath, picName);
    }

    @Override
    public int record(String filePath, String recordName) {
        int recordState = VMSNetSDK.getInstance().startLiveRecordOpt(mWindow, filePath, recordName);
        setRecording(recordState == SDKConstant.LiveSDKConstant.RECORD_SUCCESS);
        return recordState;
    }

    @Override
    public void stopRecord() {
        VMSNetSDK.getInstance().stopLiveRecordOpt(mWindow);
        setRecording(false);
    }

    @Override
    public boolean openAudio() {
        return VMSNetSDK.getInstance().startLiveAudioOpt(mWindow);
    }

    @Override
    public boolean closeAudio() {
        return VMSNetSDK.getInstance().stopLiveAudioOpt(mWindow);
    }

    @Override
    public boolean zoom(boolean enable, CustomRect original, CustomRect current) {
        return VMSNetSDK.getInstance().zoomLiveOpt(mWindow, enable, original, current);
    }

    /**
     * 云台控制 该功能为预览时的实现，回放无需实现此功能
     *
     * @return
     */
    @Override
    public void sendPTZCtrlCommand(boolean isNormal, String action, int command, int presetIndex, OnVMSNetSDKBusiness vmsNetSDKBusiness) {
        VMSNetSDK.getInstance().sendPTZCtrlCommand(mWindow, isNormal, action, command, presetIndex, vmsNetSDKBusiness);
    }

    /**
     * 获取对讲通道总数
     *
     * @return
     */
    @Override
    public int getTalkChannel() {
        return VMSNetSDK.getInstance().getTalkChannelsOpt(mWindow);
    }

    /**
     * 开启对讲
     *
     * @param channel
     */
    @Override
    public void openTalk(int channel) {
        VMSNetSDK.getInstance().openLiveTalkOpt(mWindow, channel, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                super.onFailure();
            }

            @Override
            public void onStatusCallback(int i) {
                super.onStatusCallback(i);
            }

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
            }
        });
    }

    @Override
    public void closeTalk() {
        VMSNetSDK.getInstance().closeLiveTalkOpt(mWindow);
    }

    @Override
    public int getPlayMode() {
        return BaseMedia.MODE_PLAY;
    }

    @Override
    public int getWindow() {
        return mWindow;
    }


    @Override
    public void pause() {
        stop();
    }

}