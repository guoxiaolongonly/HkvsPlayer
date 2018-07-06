package com.standards.libhikvision.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.standards.libhikvision.activity.widget.dialog.ProgressDialog;
import com.standards.libhikvision.view.IBaseView;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/6/20 15:25
 */

public abstract class BaseActivity extends AutoLayoutActivity implements IBaseView {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
        setListener();
        StatuBarCompat.translucentStatusBar(this, false);
//        initTitle(getTitle());
    }

    protected abstract int getLayoutId();

    protected abstract void setListener();

    protected abstract void initData();

    protected abstract void initView();

    @Override
    public void showHint(String hint) {
        Toast.makeText(this, hint, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingDialog(String text) {
        showLoadingDialog(text, false);
    }

    public void showLoadingDialog(String loadText, boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, loadText, cancelable);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        progressDialog.setLoadText(loadText);
    }

    public void closeLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
