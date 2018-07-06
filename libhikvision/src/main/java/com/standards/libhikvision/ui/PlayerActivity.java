package com.standards.libhikvision.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.BaseActivity;
import com.standards.libhikvision.activity.widget.player.LivePlayer;
import com.standards.libhikvision.activity.widget.player.listener.OnPlayCallBack;
import com.standards.libhikvision.activity.widget.player.listener.OnVideoControlListener;
import com.standards.libhikvision.adapter.BrowsePhotoAdapter;
import com.standards.libhikvision.browse.BrowsePhotoActivity;
import com.standards.libhikvision.browse.BrowseVideoActivity;
import com.standards.libhikvision.presenter.FileVisitorPresenter;
import com.standards.libhikvision.util.Constant;
import com.standards.libhikvision.util.DisplayUtils;
import com.standards.libhikvision.util.FileUtil;
import com.standards.libhikvision.util.HintUtil;
import com.standards.libhikvision.view.IFileVisitorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lincipin
 */
public class PlayerActivity extends BaseActivity implements IFileVisitorView {
    // 播放器
    private LivePlayer player;
    private SubResourceNodeBean subResourceNodeBean;
    private LinearLayout llScreenShotHistory;
    private RelativeLayout rlRecordHistory;
    private LinearLayout llBackPlay;
    private LinearLayout llRecord;
    private TextView tvRecord;
    private LinearLayout llScreenShot;
    private RecyclerView rvScreenShot;
    private FileVisitorPresenter fileVisitorPresenter;
    private BrowsePhotoAdapter browsePhotoAdapter;
    private TextView tvRecordCount;
    private TextView tvScreenShotCount;
    private File screenShotPath;
    private File recordPath;
    private ImageView ivBack;
    private TextView tvTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_player;
    }

    @Override
    protected void initView() {
        player = findViewById(R.id.player);
        llBackPlay = findViewById(R.id.llBackPlay);
        llRecord = findViewById(R.id.llRecord);
        tvRecord = findViewById(R.id.tvRecord);
        llScreenShot = findViewById(R.id.llScreenShot);
        llScreenShotHistory = findViewById(R.id.llScreenShotHistory);
        rlRecordHistory = findViewById(R.id.rlRecordHistory);
        rvScreenShot = findViewById(R.id.rvScreenShot);
        tvRecordCount = findViewById(R.id.tvRecordCount);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvScreenShotCount = findViewById(R.id.tvScreenShotCount);
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
                DisplayUtils.toggleScreenOrientation(PlayerActivity.this);
            }

            @Override
            public void onScreenShotClick() {
                int opt = player.screenShot();
                showHint(HintUtil.getLiveScreenShotHint(opt));
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
                player.setStreamType(0);
                player.startPlay();
            }

            @Override
            public void onFluencyClick() {
                player.setStreamType(1);
                player.startPlay();
            }
        });
        player.setOnPlayCallBack(new OnPlayCallBack() {
            @Override
            public void onFailure() {
                //截取第一帧保存
            }

            @Override
            public void onStatusCallback(int var1) {

            }

            @Override
            public void onSuccess(Object var1) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    File filePath = FileUtil.getVideoDirPath(subResourceNodeBean.getName() + "/index/");
                    File file = new File(subResourceNodeBean.getName() + "/index/" + Constant.SCREEN_DIR);
                    if (file.exists()) {
                        file.delete();
                    }
                    player.screenShot(filePath.getPath(), file.getName());
                });
                thread.start();
            }

            @Override
            public void onStart() {

            }
        });
    }

    @Override
    protected void initData() {
        subResourceNodeBean = (SubResourceNodeBean) getIntent().getSerializableExtra(Constant.IntentKey.CAMERA);
        tvTitle.setText(subResourceNodeBean.getName() + "预览");
        player.initPlayer(subResourceNodeBean, subResourceNodeBean.getName() + "预览", 1);

        recordPath = FileUtil.getVideoDirPath(subResourceNodeBean.getName() + "/video");
        player.setRecordPath(recordPath.getPath());
        screenShotPath = FileUtil.getVideoDirPath(subResourceNodeBean.getName() + "/screenShot");
        player.setScreenShotPath(screenShotPath.getPath());

        browsePhotoAdapter = new BrowsePhotoAdapter(this, new ArrayList<>(), 3);
        rvScreenShot.setAdapter(browsePhotoAdapter);
        rvScreenShot.setLayoutManager(new GridLayoutManager(this, 3));
        fileVisitorPresenter = new FileVisitorPresenter(this, screenShotPath, recordPath);
        fileVisitorPresenter.getScreeShotList();
        fileVisitorPresenter.getRecordList();
    }

    @Override
    protected void setListener() {
        llBackPlay.setOnClickListener(v -> {
            Intent intent = new Intent(this, BackPlayActivity.class);
            intent.putExtra(Constant.IntentKey.CAMERA, subResourceNodeBean);
            startActivity(intent);
        });
        llRecord.setOnClickListener(v -> {
            recordOpt();
        });
        llScreenShot.setOnClickListener(v -> {
            int opt = player.screenShot();
            showHint(HintUtil.getLiveScreenShotHint(opt));
            if (opt == SDKConstant.LiveSDKConstant.CAPTURE_SUCCESS) {
                fileVisitorPresenter.getScreeShotList();
            }
        });
        llScreenShotHistory.setOnClickListener(v -> {
            //Launch the ScreenShotHistoryViews
            Intent intent = new Intent(this, BrowsePhotoActivity.class);
            intent.putExtra("filePath", screenShotPath);
            startActivity(intent);
        });
        rlRecordHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, BrowseVideoActivity.class);
            intent.putExtra("filePath", recordPath);
            startActivity(intent);
        });

        browsePhotoAdapter.setOnItemClickListener(v -> {
            File file = (File) v.getTag();
            Uri uri = FileProvider.getUriForFile(this, "com.standards.libhikvision.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });
        ivBack.setOnClickListener(v -> finish());
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

    @Override
    protected void onStop() {
        super.onStop();
        player.onStop();
    }

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
            tvRecord.setText("录像");
            fileVisitorPresenter.getRecordList();
            showHint("停止录像");
        } else {
            int recordOpt = player.startRecord();
            showHint(HintUtil.getLiveRecordHint(recordOpt));
            if (recordOpt == SDKConstant.LiveSDKConstant.RECORD_SUCCESS) {
                tvRecord.setText("录像中");
                llRecord.setSelected(true);
            }
        }
    }

    @Override
    public void onGetScreenShotListSuccess(List<File> screenShotList) {
        browsePhotoAdapter.refreshData(screenShotList);
        tvScreenShotCount.setText(screenShotList.size() + "");
    }

    @Override
    public void onGetRecordListSuccess(List<File> backVideoList) {
        tvRecordCount.setText(backVideoList.size() + "");
    }
}
