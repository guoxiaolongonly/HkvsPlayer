package com.standards.libhikvision.view;

import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;

import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/6/20 15:08
 */

public interface IMonitorIndexView extends IBaseView {
    /**
     * 登录成功
     * @param loginData
     */
    void loginSuccess(LoginData loginData);


    void getAreaNodeDetailSuccess(List<SubResourceNodeBean>  list);
}
