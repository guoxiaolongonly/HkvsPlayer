package com.standards.libhikvision.browse;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.BaseActivity;
import com.standards.libhikvision.adapter.BrowsePhotoAdapter;
import com.standards.libhikvision.adapter.BrowseVideoAdapter;
import com.standards.libhikvision.presenter.FileVisitorPresenter;
import com.standards.libhikvision.ui.H246MediaDecoderActivity;
import com.standards.libhikvision.view.IFileVisitorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linciping
 * @time 2018/5/29
 * @note 浏览视频
 */
public class BrowseVideoActivity extends BaseActivity implements IFileVisitorView {

    private RecyclerView rvContent;

    private TextView tvTitle;

    private BrowseVideoAdapter browseVideoAdapter;
    private ImageView ivBack;

    private FileVisitorPresenter fileVisitorPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browse_video;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        ivBack = findViewById(R.id.ivBack);
        rvContent = findViewById(R.id.rvContent);
    }

    @Override
    protected void initData() {
        File file = (File) getIntent().getSerializableExtra("filePath");
        tvTitle.setText("浏览录像");
        browseVideoAdapter = new BrowseVideoAdapter(this, new ArrayList<>());
        rvContent.setAdapter(browseVideoAdapter);
        rvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContent.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        fileVisitorPresenter = new FileVisitorPresenter(this, null, file);
        fileVisitorPresenter.getRecordList();
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(v -> finish());
        browseVideoAdapter.setOnItemClickListener(v -> {
//            File file = (File) v.getTag();
//            Uri uri = Uri.parse("content://"+file.getPath());
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "video/mp4");
//            startActivity(intent);
            File file = (File) v.getTag();
            Uri uri = FileProvider.getUriForFile(this, "com.standards.libhikvision.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
//            Intent intent = new Intent(this, H246MediaDecoderActivity.class);
//            intent.putExtra("filePath", (File) v.getTag());
//            startActivity(intent);
        });
    }

    @Override
    public void onGetScreenShotListSuccess(List<File> screenShotList) {

    }

    @Override
    public void onGetRecordListSuccess(List<File> backVideoList) {
        browseVideoAdapter.refreshData(backVideoList);
    }
}
