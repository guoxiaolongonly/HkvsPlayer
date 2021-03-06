package com.standards.libhikvision.activity.widget.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.widget.player.listener.OnPlayCallBack;
import com.standards.libhikvision.activity.widget.player.listener.OnVideoControlListener;
import com.standards.libhikvision.activity.widget.player.video.BaseMedia;
import com.standards.libhikvision.activity.widget.player.video.LiveMedia;
import com.standards.libhikvision.activity.widget.player.view.LuckyBehaviorView;
import com.standards.libhikvision.activity.widget.player.view.LuckyVideoControllerView;
import com.standards.libhikvision.activity.widget.player.view.LuckyVideoSystemOverlay;
import com.standards.libhikvision.util.DisplayUtils;
import com.standards.libhikvision.util.NetworkUtils;
import com.standards.libhikvision.util.StringUtils;


/**
 * luckyPlayer
 */
public class LivePlayer extends LuckyBehaviorView {

    private SurfaceView surfaceView;
    private View loadingView;
    private LuckyVideoSystemOverlay systemView;
    private LuckyVideoControllerView mediaController;

    private LiveMedia mMediaPlayer;

    private int initWidth;
    private int initHeight;

    private NetChangedReceiver netChangedReceiver;
    private SurfaceHolder.Callback mSurfaceCallBack;
    private OnPlayCallBack mOnPlayCallBack;


    public LivePlayer(Context context) {
        super(context);
        init();
    }

    public LivePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LivePlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.video_view, this);

        surfaceView = findViewById(R.id.video_surface);
        loadingView = findViewById(R.id.video_loading);
//        progressView = findViewById(R.id.video_progress_overlay);
        systemView = findViewById(R.id.video_system_overlay);
        mediaController = findViewById(R.id.video_controller);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initWidth = getWidth();
                initHeight = DisplayUtils.getScreenHeightPixels(getContext()) / 3;
                getLayoutParams().height = initHeight;
                requestLayout();
                if (mSurfaceCallBack != null) {
                    mSurfaceCallBack.surfaceCreated(holder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        // 注册网络状态变化广播
        registerNetChangedReceiver();
    }

    public void initPlayer(SubResourceNodeBean subResourceNodeBean, String title, int window) {
        mMediaPlayer = new LiveMedia(surfaceView, subResourceNodeBean, title, window);

        mSurfaceCallBack = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.resume();
                mMediaPlayer.setVideoWindowOpt(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mMediaPlayer.pause();
                mMediaPlayer.setVideoWindowOpt(null);
            }
        };
        OnPlayCallBack onPlayCallBack = new OnPlayCallBack() {
            @Override
            public void onFailure() {
                showHint("启动取流失败");
                if(mOnPlayCallBack!=null) {
                    hideLoading();
                    mOnPlayCallBack.onFailure();
                }
            }

            @Override
            public void onStatusCallback(int i) {
                showHint("播放失败" + i);
                hideLoading();
                if(mOnPlayCallBack!=null) {
                    mOnPlayCallBack.onStatusCallback(i);
                }

            }

            @Override
            public void onSuccess(Object o) {
                new Handler().postDelayed(() -> {
                    hideLoading();
                    if (mOnPlayCallBack != null) {
                        mOnPlayCallBack.onSuccess(o);
                    }
                },3000);
            }

            @Override
            public void onStart() {
                showLoading();
                if(mOnPlayCallBack!=null) {
                    mOnPlayCallBack.onStart();
                }
            }
        };

        mediaController.setPlayer(mMediaPlayer, onPlayCallBack);
    }

    public void setStreamType(int streamType) {
        if (mMediaPlayer.getStreamType() == streamType) {
            return;
        }
        mediaController.changeStreamType(streamType == SDKConstant.LiveSDKConstant.MAIN_HIGH_STREAM ? "高清" : "流畅");
        mMediaPlayer.setStreamType(streamType);
    }

    public void startPlay() {
        mediaController.play();
    }


    public void onStop() {
        if (mMediaPlayer.getPlayStatus() == BaseMedia.PLAY_STATUS_PLAYING) {
            // 如果已经开始且在播放，则暂停同时记录状态
            mMediaPlayer.pause();
        }
    }

    //    public void onStart() {
//        startPlay();
//    }
    public void setOnPlayCallBack(OnPlayCallBack onPlayCallBack) {
        mOnPlayCallBack = onPlayCallBack;
    }

    public void onDestroy() {
        mMediaPlayer.stop();
        mediaController.release();
        unRegisterNetChangedReceiver();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mediaController.toggleDisplay();
        return super.onSingleTapUp(e);
    }

    // 对锁屏情况下进行处理
    @Override
    public boolean onDown(MotionEvent e) {
        if (isLock()) {
            return false;
        }
        return super.onDown(e);
    }

    // 对锁屏情况下进行处理
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isLock()) {
            return false;
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    protected void endGesture(int behaviorType) {
        switch (behaviorType) {
            case LuckyBehaviorView.FINGER_BEHAVIOR_BRIGHTNESS:
            case LuckyBehaviorView.FINGER_BEHAVIOR_VOLUME:
                systemView.hide();
                break;
        }
    }

    @Override
    protected void updateSeekUI(int delProgress) {
//        progressView.show(delProgress,  mMediaPlayer.getPlayStartTime(), mMediaPlayer.getPlayEndTime());
    }


    @Override
    protected void updateVolumeUI(int max, int progress) {
        systemView.show(LuckyVideoSystemOverlay.SystemType.VOLUME, max, progress);
    }

    @Override
    protected void updateLightUI(int max, int progress) {
        systemView.show(LuckyVideoSystemOverlay.SystemType.BRIGHTNESS, max, progress);
    }

    /**
     * 设置视频控制监听，有全屏监听、返回监听、重试监听
     * MediaController中对重试监听已经进行处理
     * 后期可以进行扩展：分享、收藏等
     *
     * @param onVideoControlListener
     */
    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        if (mediaController != null) {
            mediaController.setOnVideoControlListener(onVideoControlListener);
        }
    }

    /**
     * 在屏幕横竖屏切换时执行，
     * 全屏时转为横屏，播放器横纵向填充全屏
     * 竖屏时，播放器大小就是布局中设置的大小
     *
     * @param newConfig
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getLayoutParams().width = initWidth;
            getLayoutParams().height = initHeight;
        } else {
            getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        }
    }

    public int startRecord() {
        return mediaController.startRecord(StringUtils.formatDate(System.currentTimeMillis()) + ".mp4");
    }

    public void stopRecord() {
        mediaController.stopRecord();
    }

    public void setRecordPath(String recordPath) {
        mediaController.setRecordPath(recordPath);
    }

    public void setScreenShotPath(String sceenShotPath) {
        mediaController.setScreenShotPath(sceenShotPath);
    }

    public int screenShot() {
        if (mMediaPlayer.isRecording()) {
            return 6;
        }
        return mediaController.screenShot(StringUtils.formatDate(System.currentTimeMillis()) + ".jpg");
    }

    public int screenShot(String filePath, String fileName) {
        if (mMediaPlayer.isRecording()) {
            return 6;
        }
        return mediaController.screenShot(filePath, fileName);
    }

    /**
     * 显示加载中
     */
    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏加载中
     */
    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    /**
     * 是否锁屏
     *
     * @return
     */
    public boolean isLock() {
        return mediaController.isLock();
    }


    public LuckyVideoControllerView getMediaController() {
        return mediaController;
    }

    public void setMediaController(LuckyVideoControllerView mediaController) {
        this.mediaController = mediaController;
    }

    /**
     * 注册网络广播
     */
    public void registerNetChangedReceiver() {
        if (netChangedReceiver == null) {
            netChangedReceiver = new NetChangedReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            activity.registerReceiver(netChangedReceiver, filter);
        }
    }

    /**
     * 解注册广播
     */
    public void unRegisterNetChangedReceiver() {
        if (netChangedReceiver != null) {
            activity.unregisterReceiver(netChangedReceiver);
        }
    }

    /**
     * 网络变化广播接收器
     */
    private class NetChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Parcelable extra = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (extra != null && extra instanceof NetworkInfo) {
                NetworkInfo netInfo = (NetworkInfo) extra;

                if (NetworkUtils.isNetworkConnected(context) && netInfo.getState() != NetworkInfo.State.CONNECTED) {
                    // 网络连接的情况下只处理连接完成状态
                    return;
                }

                mediaController.checkShowError(true);
            }
        }
    }


    public void showHint(String hint) {
        Toast.makeText(getContext(), hint, Toast.LENGTH_LONG).show();
    }

}
