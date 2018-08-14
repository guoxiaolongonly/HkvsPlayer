package com.standards.libhikvision.ui;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.standards.libhikvision.R;
import com.standards.libhikvision.activity.BaseActivity;
import com.standards.libhikvision.adapter.IndexAdapter;
import com.standards.libhikvision.bean.AccountInfo;
import com.standards.libhikvision.bean.MonitorInfo;
import com.standards.libhikvision.presenter.MonitorIndexPresenter;
import com.standards.libhikvision.util.Constant;
import com.standards.libhikvision.util.HikvisionUtil;
import com.standards.libhikvision.util.PermissionUtil;
import com.standards.libhikvision.view.IMonitorIndexView;

import java.util.ArrayList;
import java.util.List;

/**
 * <描述功能>
 *
 * @author linciping
 * @version v1.0
 * @since: 2018/6/28
 */

public class IndexActivity extends BaseActivity implements IMonitorIndexView {

    private RecyclerView rvIndex;

    private IndexAdapter indexAdapter;

    private TextView tvName;

    private ImageView ivBack;
    private MonitorIndexPresenter mMonitorIndexPresenter;
    private MonitorInfo mMonitorInfo;
    private TextView tvTitle;
    /**
     * 资源源数据
     */
    private List<SubResourceNodeBean> mSource = new ArrayList<>();

    private GridLayoutManager gridLayoutManager;
    private PermissionUtil.PermissionTool permissionTool;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_index;
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void initData() {
        mMonitorInfo = (MonitorInfo) getIntent().getSerializableExtra("monitorInfo");
        tvName.setText(mMonitorInfo.getResourceNode().name);
        AccountInfo accountInfo = mMonitorInfo.getAccountInfo();
        mMonitorIndexPresenter = new MonitorIndexPresenter(this);
        String macAddress = HikvisionUtil.getMacAddress();
        if (TextUtils.isEmpty(macAddress)) {
            Toast.makeText(this, "未取到设备地址!", Toast.LENGTH_LONG).show();
        }
        mMonitorIndexPresenter.login(accountInfo.url, accountInfo.userName, accountInfo.password, macAddress);
    }

    @Override
    protected void initView() {
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        rvIndex = findViewById(R.id.rvIndex);
        tvName = findViewById(R.id.tvName);
        tvTitle.setText("视频监控");
        gridLayoutManager = new GridLayoutManager(this, 2);
        if (Build.VERSION.SDK_INT >= 23) { //针对6.0以后的版本加权限判断
            permissionTool = new PermissionUtil.PermissionTool(new PermissionUtil.PermissionListener() {
                @Override
                public void allGranted() {
                }

                @Override
                public void grantFaild() {
                    showHint("授权失败，部分功能将无法使用！可前往设置页手动授权！！");
                    permissionTool.startAppSettings(IndexActivity.this);
                }
            });
            permissionTool.checkAndRequestPermission(this, permissionTool.requestPermissions);
        }
    }

    @Override
    public void showLoadingDialog(String text) {
        //loadText
    }

    @Override
    public void loginSuccess(LoginData loginData) {
//        mMonitorIndexPresenter.getSubResourceList(mMonitorInfo.getResourceNode().node_type, mMonitorInfo.getResourceNode().id);
        mMonitorIndexPresenter.getSubResourceList(mMonitorInfo);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionTool.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void getAreaNodeDetailSuccess(List<SubResourceNodeBean> list) {
        indexAdapter = new IndexAdapter(this, mSource = list);
        indexAdapter.setOnItemCliclListener(v -> {
            SubResourceNodeBean subResourceNodeBean = (SubResourceNodeBean) v.getTag();
            Intent intent = new Intent(IndexActivity.this, PlayerActivity.class);
            intent.putExtra(Constant.IntentKey.CAMERA, subResourceNodeBean);
            startActivity(intent);
        });
        rvIndex.setLayoutManager(gridLayoutManager);
        rvIndex.setAdapter(indexAdapter);
    }
}
