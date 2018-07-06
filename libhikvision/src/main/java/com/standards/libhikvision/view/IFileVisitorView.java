package com.standards.libhikvision.view;

import java.io.File;
import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/6/29 15:32
 */

public interface IFileVisitorView extends IBaseView {

    void onGetScreenShotListSuccess(List<File> screenShotList);

    void onGetRecordListSuccess(List<File> backVideoList);
}
