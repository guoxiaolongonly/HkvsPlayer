package com.standards.libhikvision.ui;

import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.BaseActivity;
import com.standards.libhikvision.activity.widget.ImagePreViewDialog;
import com.standards.libhikvision.activity.widget.player.LocalPlayer;
import com.standards.libhikvision.activity.widget.player.listener.OnPlayCallBack;
import com.standards.libhikvision.activity.widget.player.listener.OnVideoControlListener;
import com.standards.libhikvision.util.FileUtil;
import com.standards.libhikvision.util.HintUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalVideoActivity extends BaseActivity {
    public static final String KEY_FILE = "filePath";
    // 播放器
    private LocalPlayer player;
    private File mFile;
    private List<String> previewFileUri=new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_local;
    }

    @Override
    protected void setListener() {
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
            }

            @Override
            public void onScreenShotClick() {
                screenShot();
            }

            @Override
            public void onRecordClick() {
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

            @Override
            public void onPreViewClick(File preImage) {
                previewFileUri.add(preImage.getAbsolutePath());
                previewImage(previewFileUri,0);
            }
        });
        player.setOnPlayCallBack(new OnPlayCallBack() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onStatusCallback(int var1) {
//                if (var1 == RtspClient.RTSPCLIENT_MSG_PLAYBACK_FINISH) {
//                   finish();
//                }
            }

            @Override
            public void onSuccess(Object var1) {

            }

            @Override
            public void onStart() {

            }
        });
    }

    private void previewImage(List<String> previewFileUri, int i) {
        ImagePreViewDialog imagePreViewDialog =new ImagePreViewDialog(this,previewFileUri,i);
        imagePreViewDialog.show();
    }


    public void screenShot()
    {
        int opt = player.screenShot();
        showHint(HintUtil.getBackScreenShotHint(opt));
    }
    @Override
    protected void initData() {
        mFile = (File) getIntent().getSerializableExtra(KEY_FILE);
        String fileName = mFile.getName();
        fileName = fileName.substring(0, fileName.length() - 4);
        player.initPlayer(fileName + "录像", mFile.getPath());

        File screenShotPath = FileUtil.getVideoDirPath("record"+File.separator+"screenShot");
        player.setScreenShotPath(screenShotPath.getPath());
    }

    @Override
    protected void initView() {
        player = findViewById(R.id.player);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.onDestroy();
    }
}
