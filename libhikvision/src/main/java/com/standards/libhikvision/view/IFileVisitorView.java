package com.standards.libhikvision.view;

import java.io.File;
import java.util.List;

/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */
public interface IFileVisitorView extends IBaseView {

    void onGetScreenShotListSuccess(List<File> screenShotList);

    void onGetRecordListSuccess(List<File> backVideoList);
}
