package com.standards.libhikvision.ui;

import android.content.res.Configuration;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.BaseActivity;
import com.standards.libhikvision.activity.widget.dialog.DatePickerDialog;
import com.standards.libhikvision.activity.widget.player.BackPlayer;
import com.standards.libhikvision.activity.widget.player.listener.OnPlayCallBack;
import com.standards.libhikvision.activity.widget.player.listener.OnVideoControlListener;
import com.standards.libhikvision.activity.widget.slice.ScalableTimebarView;
import com.standards.libhikvision.util.Constant;
import com.standards.libhikvision.util.DisplayUtils;
import com.standards.libhikvision.util.FileUtil;
import com.standards.libhikvision.util.HintUtil;
import com.standards.libhikvision.util.StringUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 */

public class BackPlayActivity extends BaseActivity {

    public static final long ONEDAY_MILL = 24 * 60 * 60 * 1000;
    // 播放器
    private BackPlayer player;
    private ImageView ivBack;
    private TextView tvTitle;
    private SubResourceNodeBean subResourceNodeBean;
    private LinearLayout llRecord;
    private LinearLayout llScreenShot;
    private TextView tvRecord;
    private ScalableTimebarView sctvTimeLine;
    private FrameLayout flTimePre;
    private FrameLayout flTimeNext;
    private TextView tvTime;

    private Calendar mStartTime;
    private Calendar mEndTime;
    private Timer mUpdateTimer;
    private DatePickerDialog datePickerDialog;
    /**
     * 定时器执行的任务
     */
    private TimerTask mUpdateTimerTask = null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_back_player;
    }

    @Override
    protected void initView() {
        player = findViewById(R.id.player);
        llRecord = findViewById(R.id.llRecord);
        llScreenShot = findViewById(R.id.llScreenShot);
        tvRecord = findViewById(R.id.tvRecord);
        sctvTimeLine = findViewById(R.id.sctvTimeLine);
        flTimePre = findViewById(R.id.flTimePre);
        flTimeNext = findViewById(R.id.flTimeNext);
        tvTime = findViewById(R.id.tvTime);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
    }

    @Override
    protected void initData() {
        subResourceNodeBean = (SubResourceNodeBean) getIntent().getSerializableExtra(Constant.IntentKey.CAMERA);
        tvTitle.setText(subResourceNodeBean.getName() + "回放");
        player.initPlayer(subResourceNodeBean, subResourceNodeBean.getName() + "回放", 1);

        File recordPath = FileUtil.getVideoDirPath(subResourceNodeBean.getName() + "/backVideo");
        player.setRecordPath(recordPath.getPath());
        File screenShotPath = FileUtil.getVideoDirPath(
                subResourceNodeBean.getName() + "/backScreenShot");
        player.setScreenShotPath(screenShotPath.getPath());
    }

    @Override
    protected void setListener() {
        llRecord.setOnClickListener(v -> {
            recordOpt();
        });
        llScreenShot.setOnClickListener(v -> {
            int opt = player.screenShot();
            showHint(HintUtil.getBackScreenShotHint(opt));
        });
        player.setOnVideoControlListener(new OnVideoControlListener() {
            @Override
            public void onStartPlayClick() {
                player.startPlay();
            }

            @Override
            public void onBackClick() {
                onBackPressed();
            }

            @Override
            public void onFullScreenClick() {
                DisplayUtils.toggleScreenOrientation(BackPlayActivity.this);
            }

            @Override
            public void onScreenShotClick() {
                int opt = player.screenShot();
                showHint(HintUtil.getBackScreenShotHint(opt));
            }

            @Override
            public void onRecordClick() {
                recordOpt();
            }

            @Override
            public void onErrorClick(int errorStatus) {
                player.startPlay();
            }

            @Override
            public void onHiQualityClick() {

            }

            @Override
            public void onFluencyClick() {

            }
        });
        player.setOnPlayCallBack(new OnPlayCallBack() {
            @Override
            public void onFailure() {
                closeLoadingDialog();
                stopUpdateTimer();
            }

            @Override
            public void onStatusCallback(int var1) {
                closeLoadingDialog();
            }

            @Override
            public void onSuccess(Object var1) {
                closeLoadingDialog();
                startUpdateTimer();
            }

            @Override
            public void onStart() {
                showLoadingDialog("视频加载中", false);
            }
        });

        flTimePre.setOnClickListener(v -> {
            preClick();
        });
        flTimeNext.setOnClickListener(v -> {
            nextClick();
        });

        tvTime.setOnClickListener(v -> {
            datePickerDialog.show();

        });
        ivBack.setOnClickListener(v -> finish());
        initTimeView();
    }

    private void preClick() {
        mStartTime = Calendar.getInstance();
        mStartTime.setTimeInMillis(sctvTimeLine.getCurrentTimeInMillisecond());
        mStartTime.set(Calendar.HOUR_OF_DAY, 0);
        mStartTime.set(Calendar.MINUTE, 0);
        mStartTime.set(Calendar.SECOND, 0);
        mStartTime.set(Calendar.DAY_OF_MONTH, mStartTime.get(Calendar.DAY_OF_MONTH) - 1);
        mEndTime = Calendar.getInstance();
        mEndTime.setTimeInMillis(mStartTime.getTimeInMillis());
        mEndTime.set(Calendar.DAY_OF_MONTH, mEndTime.get(Calendar.DAY_OF_MONTH) + 1);
        tvTime.setText(StringUtils.formatDateYYMMDD(mStartTime.getTimeInMillis()));
        sctvTimeLine.initTimebarLengthAndPosition(mStartTime.getTimeInMillis(), mEndTime.getTimeInMillis(),
                mStartTime.getTimeInMillis());
        player.playTime(mStartTime, mEndTime, mStartTime);
    }

    private void nextClick() {
        mStartTime = Calendar.getInstance();
        mStartTime.setTimeInMillis(sctvTimeLine.getCurrentTimeInMillisecond());
        mStartTime.set(Calendar.HOUR_OF_DAY, 0);
        mStartTime.set(Calendar.MINUTE, 0);
        mStartTime.set(Calendar.SECOND, 0);
        mStartTime.set(Calendar.DAY_OF_MONTH, mStartTime.get(Calendar.DAY_OF_MONTH) + 1);
        if (mStartTime.getTimeInMillis() > System.currentTimeMillis()) {
            showHint("回放时间不可超过当前时间");
            return;
        }
        mEndTime = Calendar.getInstance();
        if ((System.currentTimeMillis() - mStartTime.getTimeInMillis()) > ONEDAY_MILL) {
            mEndTime.setTimeInMillis(mStartTime.getTimeInMillis());
            mEndTime.set(Calendar.DAY_OF_MONTH, mEndTime.get(Calendar.DAY_OF_MONTH) + 1);
        }
        tvTime.setText(StringUtils.formatDateYYMMDD(mStartTime.getTimeInMillis()));
        sctvTimeLine.initTimebarLengthAndPosition(mStartTime.getTimeInMillis(), mEndTime.getTimeInMillis(),
                mStartTime.getTimeInMillis());
        player.playTime(mStartTime, mEndTime, mStartTime);
    }

    private void initTimeView() {
        mStartTime = Calendar.getInstance();
        int year = mStartTime.get(Calendar.YEAR);
        int month = mStartTime.get(Calendar.MONTH);
        int day = mStartTime.get(Calendar.DAY_OF_MONTH);
        mEndTime = Calendar.getInstance();
        mStartTime.set(year, month, day, 0, 0, 0);
        tvTime.setText(StringUtils.formatDateYYMMDD(System.currentTimeMillis()));
        sctvTimeLine.initTimebarLengthAndPosition(mStartTime.getTimeInMillis(), mEndTime.getTimeInMillis(),
                mStartTime.getTimeInMillis());
        sctvTimeLine.setOnBarMoveListener(new ScalableTimebarView.OnBarMoveListener() {
            @Override
            public void onBarMove(long screenLeftTime, long screenRightTime, long currentTime) {
                stopUpdateTimer();
            }

            @Override
            public void OnBarMoveFinish(long screenLeftTime, long screenRightTime, long currentTime) {
                player.playTime(currentTime);
            }
        });

        datePickerDialog = new DatePickerDialog.Builder(this)
                .setMinYear(2018)
                .setMaxYear(2018)
                .setInitialDate(mStartTime.getTimeInMillis())
                .setShowType(DatePickerDialog.TAG_SHOW_DATE_ONLY)
                .build();
        datePickerDialog.setListener(mills -> {
            mStartTime = Calendar.getInstance();
            mStartTime.setTimeInMillis(mills);
            mStartTime.set(Calendar.HOUR_OF_DAY, 0);
            mStartTime.set(Calendar.MINUTE, 0);
            mStartTime.set(Calendar.SECOND, 0);
            if (mStartTime.getTimeInMillis() > System.currentTimeMillis()) {
                showHint("回放时间不可超过当前时间");
                return;
            }
            mEndTime = Calendar.getInstance();
            if ((System.currentTimeMillis() - mStartTime.getTimeInMillis()) > ONEDAY_MILL) {
                mEndTime.setTimeInMillis(mStartTime.getTimeInMillis());
                mEndTime.set(Calendar.DAY_OF_MONTH, mEndTime.get(Calendar.DAY_OF_MONTH) + 1);
            }
            tvTime.setText(StringUtils.formatDateYYMMDD(mStartTime.getTimeInMillis()));
            sctvTimeLine.initTimebarLengthAndPosition(mStartTime.getTimeInMillis(), mEndTime.getTimeInMillis(), mStartTime.getTimeInMillis());
            player.playTime(mStartTime, mEndTime, mStartTime);
        });
    }

    @Override
    public void onBackPressed() {
        if (!DisplayUtils.isPortrait(this)) {
            if (!player.isLock()) {
                DisplayUtils.toggleScreenOrientation(this);
            }
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        player.onStop();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.toolBar).setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void recordOpt() {
        if (llRecord.isSelected()) {
            llRecord.setSelected(false);
            player.stopRecord();
            showHint("停止录像");
            tvRecord.setText("录像");
            sctvTimeLine.setVisibility(View.VISIBLE);
        } else {
            int recordOpt = player.startRecord();
            showHint(HintUtil.getBackPlayRecordHint(recordOpt));
            if (recordOpt == SDKConstant.PlayBackSDKConstant.RECORD_SUCCESS) {
                llRecord.setSelected(true);
                tvRecord.setText("录像中");
                sctvTimeLine.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 启动定时器
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        mUpdateTimer = new Timer();
        mUpdateTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    //updateProgress
                    updateRemotePlayUI();
                });
            }
        };
        // 延时1000ms后执行，1000ms执行一次
        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
    }

    private void updateRemotePlayUI() {
        //获取播放进度
        long osd = player.getCurrentPlayerTime();
        if (osd != -1 && sctvTimeLine != null) {
            sctvTimeLine.setCurrentTimeInMillisecond(osd);
        }
    }


    /**
     * 停止定时器
     */
    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }
}

