package com.standards.libhikvision.activity.widget.player.video;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.CustomRect;
import com.hikvision.sdk.net.bean.PlaybackSpeed;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.SDKUtil;

import java.util.Calendar;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function 思考了一下  在播放器中其实并没有多大需求要加入OnVMSNetSDKBusiness
 * 部分特殊情况可能会有这些需求，比如在开始播放的时候我们可能需要通过回调来知道这次的开启是成功还是失败的
 * 从而确定我们的UI加载状态这边先给几个地方加上回调如果后续有这方面需求，可以继承此类，重写相应方法插入回调。
 * @date: 2018/6/25 11:10
 */

public class PlayBackMedia extends BaseMedia {
    private SurfaceView mSurfaceView;
    private SubResourceNodeBean mSubResourceNodeBean;
    private int mWindow;
    private Context mContext;
    private CameraInfo mCameraInfo;
    private RecordInfo mRecordInfo;

    private Calendar playStartTime;
    private Calendar playEndTime;

    private Calendar preTime;
    private int storageType;
    private String guid;

    public PlayBackMedia(SurfaceView surfaceView, SubResourceNodeBean subResourceNodeBean, String title, int window, Calendar startTime, Calendar endTime) {
        super(title);
        mSurfaceView = surfaceView;
        mSubResourceNodeBean = subResourceNodeBean;
        mWindow = window;
        mContext = surfaceView.getContext();
        playStartTime = startTime;
        preTime = startTime;
        playEndTime = endTime;
        getBackPlayCameraInfo();
    }

    @Override
    public void getBackPlayCameraInfo() {
        VMSNetSDK.getInstance().getPlayBackCameraInfo(mWindow, mSubResourceNodeBean.getSysCode(), new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                showHint("加载监控点详细信息失败!");
            }

            @Override
            public void onSuccess(Object obj) {
                mCameraInfo = (CameraInfo) obj;
                //解析监控点录像信息
                int[] recordPos = SDKUtil.processStorageType(mCameraInfo);
                String[] guids = SDKUtil.processGuid(mCameraInfo);
                storageType = 1;
                //默认选取第一种存储类型进行查询
                if (null != recordPos && 0 < recordPos.length) {
                    storageType = recordPos[0];
                }
                if (null != guids && 0 < guids.length) {
                    guid = guids[0];
                }
                queryRecordSegment(playStartTime, playEndTime, storageType, guid);
            }
        });
    }

    @Override
    public void queryRecordSegment(Calendar queryStartTime, Calendar queryEndTime, int storageType, String guid) {
        VMSNetSDK.getInstance().queryRecordSegment(mWindow, mCameraInfo, queryStartTime, queryEndTime, storageType, guid, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                showHint("查找录像片段失败");
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof RecordInfo) {
                    mRecordInfo = (RecordInfo) obj;
                }
            }
        });
    }

    public void queryRecordSegmentThenPlay(Calendar playStartTime, Calendar playEndTime, Calendar currentTime) {
        preTime = currentTime;
        this.playStartTime = playStartTime;
        this.playEndTime = playEndTime;
        VMSNetSDK.getInstance().queryRecordSegment(mWindow, mCameraInfo, playStartTime, playEndTime, storageType, guid, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                showHint("查找录像片段失败！");
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof RecordInfo) {
                    mRecordInfo = (RecordInfo) obj;
                    if (isLockPlay) {
                        return;
                    }
                    play();
                }
            }
        });
    }


    @Override
    public void play() {
        if (isLockPlay) {
            return;
        }
        if (mRecordInfo == null) {
            queryRecordSegmentThenPlay(playStartTime, playEndTime, preTime);
            return;
        }
        mOnPlayCallBack.onStart();
        new Thread(() -> {
            VMSNetSDK.getInstance().startPlayBackOpt(mWindow, mSurfaceView, mRecordInfo.getSegmentListPlayUrl(),
                    preTime, getPlayEndTime(), new OnVMSNetSDKBusiness() {
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
    public boolean setCurrentTime(long time) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(time);
        stop();
        queryRecordSegmentThenPlay(playStartTime, playEndTime, currentTime);
        return true;
    }

    public void playTime(long time) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(time);
        stop();
        queryRecordSegmentThenPlay(playStartTime, playEndTime, currentTime);
    }

    @Override
    public void stop() {
        VMSNetSDK.getInstance().stopPlayBackOpt(mWindow);
        setPlayStatus(PLAY_STATUS_STOP);
    }

    @Override
    public int capture(String filePath, String picName) {
        return VMSNetSDK.getInstance().capturePlaybackOpt(mWindow, filePath, picName);
    }

    @Override
    public int record(String filePath, String recordName) {
        int recordState = VMSNetSDK.getInstance().startPlayBackRecordOpt(mWindow, filePath, recordName);
        setRecording(recordState == SDKConstant.PlayBackSDKConstant.RECORD_SUCCESS);
        return recordState;
    }

    @Override
    public void stopRecord() {
        VMSNetSDK.getInstance().stopPlayBackRecordOpt(mWindow);
        setRecording(false);
    }

    @Override
    public boolean openAudio() {
        return VMSNetSDK.getInstance().startPlayBackAudioOpt(mWindow);
    }

    @Override
    public boolean closeAudio() {
        return VMSNetSDK.getInstance().stopLiveAudioOpt(mWindow);
    }

    @Override
    public boolean zoom(boolean enable, CustomRect original, CustomRect current) {
        return VMSNetSDK.getInstance().zoomPlayBackOpt(mWindow, enable, original, current);
    }

    /**
     * 回放暂停
     */
    @Override
    public void pause() {
        VMSNetSDK.getInstance().pausePlayBackOpt(mWindow);
        setPlayStatus(PLAY_STATUS_PAUSE);
    }

    /**
     * 回放恢复
     */
    @Override
    public void resume() {
        VMSNetSDK.getInstance().resumePlayBackOpt(mWindow);
        setPlayStatus(PLAY_STATUS_PLAYING);
    }



    /**
     * 回放更新容器
     *
     * @param surfaceHolder
     * @return
     */
    @Override
    public boolean setVideoWindowOpt(SurfaceHolder surfaceHolder) {
        return VMSNetSDK.getInstance().setVideoWindowOpt(mWindow, surfaceHolder);
    }

    /**
     * 回放倍数控制
     *
     * @param speed 回放速度
     * @return
     */
    @Override
    public void setPlayBackSpeed(@PlaybackSpeed.Speed int speed) {
        VMSNetSDK.getInstance().setPlaybackSpeed(mWindow, speed);
    }

    @Override
    public int getPlayMode() {
        return BaseMedia.MODE_PLAY_BACK;
    }

    @Override
    public int getWindow() {
        return mWindow;
    }

    public void showHint(String hint) {
        Toast.makeText(mContext, hint, Toast.LENGTH_LONG).show();
    }


    public Calendar getPlayStartTime() {
        return playStartTime;
    }

    @Override
    public long getCurrentPlayTime() {
        long playerTime = VMSNetSDK.getInstance().getOSDTimeOpt(mWindow);
        return -1 == playerTime ? preTime.getTimeInMillis() : playerTime;
    }

    @Override
    public long getStartTime() {
        return playStartTime.getTimeInMillis();
    }

    @Override
    public long getTotalTime() {
        return playEndTime.getTimeInMillis();
    }

    public void setPlayStartTime(Calendar playStartTime) {
        this.playStartTime = playStartTime;
    }


    /**
     * 播放开始时间
     *
     * @return
     */
    public Calendar getPlayEndTime() {
        return playEndTime;
    }

    /**
     * 播放结束时间
     *
     * @param playEndTime
     */
    public void setPlayEndTime(Calendar playEndTime) {
        this.playEndTime = playEndTime;
    }

//

    public CameraInfo getCameraInfo() {
        return mCameraInfo;
    }

    public RecordInfo getRecordInfo() {
        return mRecordInfo;
    }

    public int getStorageType() {
        return storageType;
    }

    public String getGuid() {
        return guid;
    }
}