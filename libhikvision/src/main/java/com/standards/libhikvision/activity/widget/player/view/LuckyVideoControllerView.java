package com.standards.libhikvision.activity.widget.player.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.sdk.consts.SDKConstant;
import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.widget.player.listener.OnPlayCallBack;
import com.standards.libhikvision.activity.widget.player.listener.OnVideoControlListener;
import com.standards.libhikvision.activity.widget.player.video.BaseMedia;
import com.standards.libhikvision.util.DisplayUtils;
import com.standards.libhikvision.util.NetworkUtils;
import com.standards.libhikvision.util.StringUtils;

import java.io.File;

import static com.standards.libhikvision.activity.widget.player.video.BaseMedia.MODE_LOCAL_PLAY;
import static com.standards.libhikvision.activity.widget.player.video.BaseMedia.MODE_PLAY;
import static com.standards.libhikvision.activity.widget.player.video.BaseMedia.MODE_PLAY_BACK;
import static com.standards.libhikvision.activity.widget.player.video.BaseMedia.PLAY_STATUS_PAUSE;
import static com.standards.libhikvision.activity.widget.player.video.BaseMedia.PLAY_STATUS_PLAYING;


/**
 * 视频控制器，可替换或自定义样式
 */
public class LuckyVideoControllerView extends FrameLayout {

    // 默认显示时间3秒
    public static final int DEFAULT_SHOW_TIME = 3000;

    private View mControllerTitle;
    private View mControllerBottom;

    private View mControllerBack;

    private boolean mDragging;
    private long mDraggingProgress;

    /**
     * 标题
     */
    private TextView mVideoTitle;
    /**
     * 进度条
     */
    private SeekBar mPlayerSeekBar;
    /**
     * 播放状态 播放：停止
     */
    private ImageView ivPlayerState;

    private FrameLayout flPlayerState;
    /**
     * 当前播放时间
     */
    private TextView mVideoProgress;
    /**
     * 总时间
     */
    private TextView mVideoDuration;

    private RelativeLayout rlRecordTime;
    private ImageView ivPreImage;
    private ImageView ivScreenShot;
    private ImageView ivRecord;
    private TextView tvSourceStream;
    private Chronometer cnTimer;
    private LinearLayout llStreamType;
    private TextView tvHighQuality;
    private TextView tvFluency;
    /**
     * 全屏按钮
     */
    private ImageView mVideoFullScreen;
    /**
     * 屏幕锁定
     */
    private ImageView mScreenLock;

    private RelativeLayout rl_pre;
    private ImageView iv_pre_play;

    private LuckyVideoErrorView mErrorView;

    private boolean isScreenLock;
    private boolean mShowing;
    private boolean mAllowUnWifiPlay;


    private OnVideoControlListener onVideoControlListener;

    protected BaseMedia mMediaPlayer;


    /**
     * portrait true, landscape false
     */
    private boolean isPortrait = true;

    private String recordPath;
    private String screenShotPath;
    private File preImage;
    private LinearLayout llTimeBarView;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
    }

    public LuckyVideoControllerView(Context context) {
        super(context);
        init();
    }

    public LuckyVideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LuckyVideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_media_controller, this);
        initControllerPanel();
    }

    private void initControllerPanel() {
        // back
        mControllerBack = findViewById(R.id.video_back);
        mControllerBack.setOnClickListener(v -> {
            if (onVideoControlListener != null) {
                onVideoControlListener.onBackClick();
            }
        });
        // top
        mControllerTitle = findViewById(R.id.video_controller_title);
        mControllerTitle.setVisibility(GONE);
        mVideoTitle = mControllerTitle.findViewById(R.id.video_title);
        // bottom
        mControllerBottom = findViewById(R.id.video_controller_bottom);
        mPlayerSeekBar = mControllerBottom.findViewById(R.id.player_seek_bar);
        ivPlayerState = mControllerBottom.findViewById(R.id.ivPlayerState);
        flPlayerState = mControllerBottom.findViewById(R.id.flPlayerState);
        mVideoProgress = mControllerBottom.findViewById(R.id.player_progress);
        mVideoDuration = mControllerBottom.findViewById(R.id.player_duration);
        ivPreImage = mControllerBottom.findViewById(R.id.ivPreImage);
        llTimeBarView = findViewById(R.id.llTimeBarView);
        ivPreImage.setVisibility(GONE);
        ivScreenShot = mControllerBottom.findViewById(R.id.ivScreenShot);
        ivRecord = mControllerBottom.findViewById(R.id.ivRecord);

        tvSourceStream = mControllerBottom.findViewById(R.id.tvSourceStream);
        mVideoFullScreen = mControllerBottom.findViewById(R.id.video_full_screen);

        rl_pre = findViewById(R.id.rl_pre);
        iv_pre_play = findViewById(R.id.iv_pre_play);
        flPlayerState.setOnClickListener(mOnPlayerPauseClick);
        ivPlayerState.setImageResource(R.mipmap.ic_video_pause);

        llStreamType = mControllerBottom.findViewById(R.id.llStreamType);
        tvFluency = mControllerBottom.findViewById(R.id.tvFluency);
        tvHighQuality = mControllerBottom.findViewById(R.id.tvHighQuality);

        //timerView
        cnTimer = findViewById(R.id.cnTimer);
        rlRecordTime = findViewById(R.id.rlRecordTime);

        // lock
        mScreenLock = findViewById(R.id.player_lock_screen);
        // error
        mErrorView = findViewById(R.id.video_controller_error);
        mPlayerSeekBar.setMax(1000);
        setListener();
    }

    private void setListener() {
        iv_pre_play.setOnClickListener(view -> {
            if (onVideoControlListener != null) {
                onVideoControlListener.onStartPlayClick();
            }
        });

        mVideoFullScreen.setOnClickListener(v -> {
            if (onVideoControlListener != null) {
                onVideoControlListener.onFullScreenClick();
            }
        });

        mScreenLock.setOnClickListener(v -> {
            if (isScreenLock) unlock();
            else lock();
            show();
        });
        mErrorView.setOnVideoControlListener(new OnVideoControlListener() {
            @Override
            public void onStartPlayClick() {

            }

            @Override
            public void onBackClick() {

            }

            @Override
            public void onFullScreenClick() {

            }

            @Override
            public void onScreenShotClick() {

            }

            @Override
            public void onRecordClick() {

            }

            @Override
            public void onErrorClick(int errorStatus) {
                retry(errorStatus);
            }

            @Override
            public void onHiQualityClick() {
            }

            @Override
            public void onFluencyClick() {
            }
        });

        ivScreenShot.setOnClickListener(v -> {
            onVideoControlListener.onScreenShotClick();
        });

        ivRecord.setOnClickListener(v -> {
            onVideoControlListener.onRecordClick();
        });
        tvHighQuality.setOnClickListener(v -> {
            llStreamType.setVisibility(GONE);
            onVideoControlListener.onHiQualityClick();
        });
        tvFluency.setOnClickListener(v -> {
            llStreamType.setVisibility(GONE);
            onVideoControlListener.onFluencyClick();
        });
    }

    public void setPlayer(BaseMedia mediaPlayer, OnPlayCallBack onPlayCallBack) {
        this.mMediaPlayer = mediaPlayer;
        mMediaPlayer.setOnPlayCallBack(new OnPlayCallBack() {
            @Override
            public void onFailure() {
                ((Activity) getContext()).runOnUiThread(() -> onPlayCallBack.onFailure());
            }

            @Override
            public void onStatusCallback(int var1) {
                ((Activity) getContext()).runOnUiThread(() -> onPlayCallBack.onStatusCallback(var1));
            }

            @Override
            public void onSuccess(Object var1) {
                ((Activity) getContext()).runOnUiThread(() -> {
                    onPlayCallBack.onSuccess(var1);
                    show();
                });
            }

            @Override
            public void onStart() {
                rl_pre.setVisibility(View.GONE);
                ((Activity) getContext()).runOnUiThread(() -> onPlayCallBack.onStart());
            }
        });
        //视图控制
        if (mMediaPlayer.getPlayMode() == MODE_PLAY) {
            llTimeBarView.setVisibility(INVISIBLE);
            tvSourceStream.setOnClickListener(v -> {
                llStreamType.setVisibility(View.GONE - llStreamType.getVisibility());
            });
        } else if (mMediaPlayer.getPlayMode() == MODE_LOCAL_PLAY) {
            llTimeBarView.setVisibility(VISIBLE);
            findViewById(R.id.llLandView).setVisibility(VISIBLE);
            ivRecord.setVisibility(GONE);
            ivScreenShot.setVisibility(VISIBLE);
            mVideoFullScreen.setVisibility(GONE);
            findViewById(R.id.llVoiceView).setVisibility(GONE);
            tvSourceStream.setVisibility(GONE);
            llStreamType.setVisibility(GONE);
            tvSourceStream.setOnClickListener(v -> {
                llStreamType.setVisibility(View.GONE - llStreamType.getVisibility());
            });
        }

        if (mMediaPlayer.getPlayMode() == BaseMedia.MODE_PLAY_BACK || mediaPlayer.getPlayMode() == BaseMedia.MODE_LOCAL_PLAY) {
            mPlayerSeekBar.setOnSeekBarChangeListener(mSeekListener);
        }
        mVideoTitle.setText(mediaPlayer.getTitle());
    }

    /**
     * 控制隐藏显示
     */
    public void toggleDisplay() {
        if (rl_pre.getVisibility() == VISIBLE) {
            return;
        }
        if (mShowing) {
            hide();
        } else {
            show();
        }
    }

    /**
     * 显示控制器
     */
    public void show() {
        setProgress();
        show(DEFAULT_SHOW_TIME);
    }

    /**
     * 显示控制器
     *
     * @param timeout 显示时长
     */
    public void show(int timeout) {
        if (!isScreenLock) {
            if (!isPortrait) {
                mControllerBack.setVisibility(VISIBLE);
                mControllerTitle.setVisibility(VISIBLE);
            }
            mControllerBottom.setVisibility(VISIBLE);
        } else {
            mControllerBack.setVisibility(GONE);
            mControllerTitle.setVisibility(GONE);
            mControllerBottom.setVisibility(GONE);
        }

        if (!DisplayUtils.isPortrait(getContext())) {
            mScreenLock.setVisibility(VISIBLE);
        }

        mShowing = true;

        updatePausePlay();

        // 开始显示
        post(mShowProgress);

        if (timeout > 0) {
            // 先移除之前的隐藏异步操作
            removeCallbacks(mFadeOut);
            //timeout后隐藏
            postDelayed(mFadeOut, timeout);
        }
    }


    /**
     * 隐藏控制器
     */
    private void hide() {
        if (!mShowing) {
            return;
        }

        // 横屏才消失
        mControllerBack.setVisibility(GONE);
        mControllerTitle.setVisibility(GONE);
        mControllerBottom.setVisibility(GONE);
        llStreamType.setVisibility(GONE);
        mScreenLock.setVisibility(GONE);
        removeCallbacks(mShowProgress);
        mShowing = false;
    }

    /**
     * 异步操作隐藏
     */
    private final Runnable mFadeOut = () -> hide();

    /**
     * 异步操作显示
     */
    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            long pos = setProgress();
            if (!mDragging && mShowing && mMediaPlayer.getPlayStatus() == BaseMedia.PLAY_STATUS_PLAYING) {
                // 解决1秒之内的误差，使得发送消息正好卡在整秒
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    /**
     * 设置进度，同时也返回进度
     *
     * @return
     */
    private long setProgress() {
        if (mMediaPlayer.getPlayMode() == BaseMedia.MODE_PLAY) {
            return 0;
        }
        if (mMediaPlayer == null || mDragging) {
            return 0;
        }

        long current = mMediaPlayer.getCurrentPlayTime();
        long start = mMediaPlayer.getStartTime();
        long end = mMediaPlayer.getTotalTime();
        if (current == end) {
            mMediaPlayer.stop();
        }
        if (mPlayerSeekBar != null) {
            long pos = (current - start) * 1000 / (end - start);
            mPlayerSeekBar.setProgress((int) pos);
        }
        mVideoProgress.setText(getRealTime(current));
        mVideoDuration.setText(getRealTime(end));


        return current;
    }


    /**
     * 判断显示错误类型
     */
    public void checkShowError(boolean isNetChanged) {
        boolean isConnect = NetworkUtils.isNetworkConnected(getContext());
        boolean isMobileNet = NetworkUtils.isMobileConnected(getContext());
        boolean isWifiNet = NetworkUtils.isWifiConnected(getContext());

        if (isConnect) {
            // 如果已经联网
            if (mErrorView.getCurStatus() == LuckyVideoErrorView.STATUS_NO_NETWORK_ERROR && !(isMobileNet && !isWifiNet)) {
                // 如果之前是无网络
                mErrorView.setVisibility(View.GONE);
                reload();
            } else if (mMediaPlayer == null) {
                // 优先判断是否有video数据
                showError(LuckyVideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
            } else if (isMobileNet && !isWifiNet && !mAllowUnWifiPlay) {
                // 如果是手机流量，且未同意过播放，且非本地视频，则提示错误
                if (mMediaPlayer.getPlayMode() != BaseMedia.MODE_LOCAL_PLAY) {
                    mErrorView.showError(LuckyVideoErrorView.STATUS_UN_WIFI_ERROR);
                    mMediaPlayer.setLockPlay(true);
                    mMediaPlayer.stop();
                }
            } else if (isWifiNet && isNetChanged && mErrorView.getCurStatus() == LuckyVideoErrorView.STATUS_UN_WIFI_ERROR) {
                // 如果是wifi流量，且之前是非wifi错误，则恢复播放
                reload();
            } else if (!isNetChanged) {
                showError(LuckyVideoErrorView.STATUS_VIDEO_SRC_ERROR);
            }
        } else {
            // 无网，暂停播放并提示
            mMediaPlayer.stop();
            showError(LuckyVideoErrorView.STATUS_NO_NETWORK_ERROR);
        }
    }

    /**
     * 隐藏错误提示
     */
    public void hideErrorView() {
        mErrorView.hideError();
    }

    /**
     * 重新播放
     */
    private void reload() {
        stop();
        play();
    }

    /**
     * 重置
     */
    public void release() {
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    /**
     * 出错重试
     *
     * @param status
     */
    private void retry(int status) {
        switch (status) {
            case LuckyVideoErrorView.STATUS_VIDEO_DETAIL_ERROR:
                // 传递给activity
                if (onVideoControlListener != null) {
                    onVideoControlListener.onErrorClick(status);
                }
                break;
            case LuckyVideoErrorView.STATUS_VIDEO_SRC_ERROR:
                reload();
                break;
            case LuckyVideoErrorView.STATUS_UN_WIFI_ERROR:
                allowUnWifiPlay();
                break;
            case LuckyVideoErrorView.STATUS_NO_NETWORK_ERROR:
                // 无网络时
                if (NetworkUtils.isNetworkConnected(getContext())) {
                    reload();
                } else {
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            removeCallbacks(mShowProgress);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }
            long startTime = mMediaPlayer.getStartTime();
            long endTime = mMediaPlayer.getTotalTime();
            long timeRange = endTime - startTime;
            mDraggingProgress = (timeRange * progress) / 1000L + startTime;
            if (mVideoProgress != null) {
                mVideoProgress.setText(getRealTime(mDraggingProgress + startTime));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mMediaPlayer.setCurrentTime(mDraggingProgress);
            mDragging = false;
            mDraggingProgress = 0;
            hide();
            post(mShowProgress);
        }
    };

    /**
     * 提示错误
     *
     * @param status
     */
    private void showError(int status) {
        mErrorView.showError(status);
        hide();

        // 如果提示了错误，则看需要解锁
        if (isScreenLock) {
            unlock();
        }
    }

    /**
     * 是否锁屏
     *
     * @return
     */
    public boolean isLock() {
        return isScreenLock;
    }

    /**
     * 锁屏
     */
    private void lock() {
        isScreenLock = true;
        mScreenLock.setImageResource(R.mipmap.video_locked);
    }

    /**
     * 解锁
     */
    private void unlock() {
        isScreenLock = false;
        mScreenLock.setImageResource(R.mipmap.video_unlock);
    }

    /**
     * 允许非wifi播放
     */
    private void allowUnWifiPlay() {
        mAllowUnWifiPlay = true;
        mMediaPlayer.setLockPlay(false);
        mErrorView.setVisibility(GONE);
        reload();
    }


    private OnClickListener mOnPlayerPauseClick = v -> doPauseResume();

    /**
     * 切换播放按钮图片
     */
    public void updatePausePlay() {
        if (mMediaPlayer.getPlayStatus() == PLAY_STATUS_PLAYING) {
            ivPlayerState.setImageResource(R.mipmap.ic_video_pause);
        } else {
            ivPlayerState.setImageResource(R.mipmap.ic_video_play);
        }
    }

    /**
     * 切换播放暂停
     */
    private void doPauseResume() {
        if (mMediaPlayer.getPlayStatus() == PLAY_STATUS_PLAYING) {
            pause();
        } else {
            if (mMediaPlayer.getPlayStatus() == PLAY_STATUS_PAUSE) {
                resume();
            } else {
                play();
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        mMediaPlayer.stop();
        show();
    }

    /**
     * 暂停
     */
    public void pause() {
        mMediaPlayer.pause();
        show();
    }

    /**
     * 播放
     */
    public void play() {
        if (mMediaPlayer.getPlayStatus() > BaseMedia.PLAY_STATUS_PAUSE) {
            mMediaPlayer.stop();
        }
        if (mMediaPlayer.isRecording()) {
            stopRecord();
        }
        mMediaPlayer.play();
        rl_pre.setVisibility(View.GONE);
        hide();

    }

    /**
     * 播放
     */
    public void resume() {
        mMediaPlayer.resume();
        show();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggleVideoLayoutParams();
    }

    /**
     * 横竖屏切换时按钮的隐蔽与显示
     */
    void toggleVideoLayoutParams() {
        isPortrait = DisplayUtils.isPortrait(getContext());
        if (isPortrait) {
            mScreenLock.setVisibility(GONE);
            findViewById(R.id.llLandView).setVisibility(GONE);
            mVideoFullScreen.setSelected(false);
        } else {
            if (mShowing) {
                mScreenLock.setVisibility(VISIBLE);
            }
            findViewById(R.id.llLandView).setVisibility(VISIBLE);
            mVideoFullScreen.setSelected(true);
        }
    }

    /**
     * 控制默认背景隐藏和显示
     *
     * @param flag
     */
    public void showBg(boolean flag) {
        if (flag) {
            rl_pre.setVisibility(View.VISIBLE);
        } else {
            rl_pre.setVisibility(View.GONE);
        }
    }


    public int startRecord(String fileName) {
        int status = mMediaPlayer.record(recordPath, fileName);
        if (status == SDKConstant.LiveSDKConstant.RECORD_SUCCESS || status == SDKConstant.PlayBackSDKConstant.RECORD_SUCCESS) {
            //录像计时器
            rlRecordTime.setVisibility(VISIBLE);
            cnTimer.setBase(SystemClock.elapsedRealtime());//计时器清零
            int hour = (int) ((SystemClock.elapsedRealtime() - cnTimer.getBase()) / 1000 / 60);
            cnTimer.setFormat("0" + String.valueOf(hour) + ":%s");
            cnTimer.start();
            ivRecord.setSelected(true);
        }
        return status;
    }

    public void stopRecord() {
        //录像计时器相关
        rlRecordTime.setVisibility(GONE);
        cnTimer.stop();

        ivRecord.setSelected(false);
        mMediaPlayer.stopRecord();
    }

    public int screenShot(String fileName) {
        int captureStatus = screenShot(screenShotPath, fileName);
        if (captureStatus == SDKConstant.LiveSDKConstant.CAPTURE_SUCCESS || captureStatus == SDKConstant.PlayBackSDKConstant.CAPTURE_SUCCESS) {
            preImage = new File(screenShotPath, fileName);
            if (preImage != null && preImage.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(preImage.getPath());
                ivPreImage.setVisibility(VISIBLE);
                ivPreImage.setImageBitmap(bitmap);
            }
        }
        return captureStatus;
    }

    public String getRealTime(long mill) {
        if (MODE_PLAY_BACK == mMediaPlayer.getPlayMode()) {
            return StringUtils.millToTime(mill);
        } else {
            return StringUtils.stringForTime((int) mill);
        }
    }


    public int screenShot(String filePath, String fileName) {
        if (mMediaPlayer.isRecording()) {
            return 6;
        }
        return mMediaPlayer.capture(filePath, fileName);
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public void setScreenShotPath(String screenShotPath) {
        this.screenShotPath = screenShotPath;
    }

    public void changeStreamType(String s) {
        tvSourceStream.setText(s);
    }
}
