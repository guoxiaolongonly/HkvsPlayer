package com.standards.libhikvision.browse;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.BaseActivity;
import com.standards.libhikvision.adapter.BrowsePhotoAdapter;
import com.standards.libhikvision.presenter.FileVisitorPresenter;
import com.standards.libhikvision.view.IFileVisitorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linciping
 * @time 2018/5/29
 * @note 浏览截图
 */
public class BrowsePhotoActivity extends BaseActivity implements IFileVisitorView {

    private RecyclerView rvContent;

    private TextView tvTitle;

    private BrowsePhotoAdapter browsePhotoAdapter;
    private ImageView ivBack;

    private FileVisitorPresenter fileVisitorPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browse_photo;
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
        tvTitle.setText("浏览截图");
        browsePhotoAdapter = new BrowsePhotoAdapter(this, new ArrayList<>());
        rvContent.setAdapter(browsePhotoAdapter);
        rvContent.setLayoutManager(new GridLayoutManager(this, 2));

        fileVisitorPresenter = new FileVisitorPresenter(this, file, null);
        fileVisitorPresenter.getScreeShotList();
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(v -> finish());
        browsePhotoAdapter.setOnItemClickListener(v -> {
            File file = (File) v.getTag();
            Uri uri = FileProvider.getUriForFile(this, "com.standards.libhikvision.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });
    }


    @Override
    public void onGetScreenShotListSuccess(List<File> screenShotList) {
        browsePhotoAdapter.refreshData(screenShotList);
    }

    @Override
    public void onGetRecordListSuccess(List<File> backVideoList) {

    }
}
