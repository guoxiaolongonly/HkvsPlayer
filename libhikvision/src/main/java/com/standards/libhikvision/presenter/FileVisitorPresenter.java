package com.standards.libhikvision.presenter;

import android.app.Activity;

import com.standards.libhikvision.util.FileUtil;
import com.standards.libhikvision.view.IFileVisitorView;

import java.io.File;


/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/6/29 15:30
 */

public class FileVisitorPresenter {
    private IFileVisitorView iFileVisitorView;
    private File mScreenShotPath;
    private File mRecordPath;

    public FileVisitorPresenter(IFileVisitorView iFileVisitorView, File screenShotPath, File recordPath) {
        this.iFileVisitorView = iFileVisitorView;
        this.mRecordPath = recordPath;
        this.mScreenShotPath = screenShotPath;
    }

    /**
     * 需要传一个参数，文件夹名称
     */
    public void getRecordList() {
        iFileVisitorView.onGetRecordListSuccess(FileUtil.visitFileInPath(mRecordPath.getPath(), ".mp4"));
    }

    /**
     * 需要传一个参数，文件夹名称
     */
    public void getScreeShotList() {
        iFileVisitorView.onGetScreenShotListSuccess(FileUtil.visitFileInPath(mScreenShotPath.getPath(), ".jpg"));
    }
}
