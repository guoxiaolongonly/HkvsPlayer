package com.standards.libhikvision.browse;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.BaseActivity;
import com.standards.libhikvision.activity.widget.ImagePreViewDialog;
import com.standards.libhikvision.adapter.BrowsePhotoAdapter;
import com.standards.libhikvision.presenter.FileVisitorPresenter;
import com.standards.libhikvision.view.IFileVisitorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <浏览截图>
 * @author linciping
 * @version v1.0
 * @since: 2018/6/11
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
            ImagePreViewDialog imagePreViewDialog = new ImagePreViewDialog(this,  browsePhotoAdapter.getUrls(), (Integer) v.getTag());
            imagePreViewDialog.show();
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
