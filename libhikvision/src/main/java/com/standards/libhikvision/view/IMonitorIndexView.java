package com.standards.libhikvision.view;

import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;

import java.util.List;

/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */
public interface IMonitorIndexView extends IBaseView {
    /**
     * 登录成功
     * @param loginData
     */
    void loginSuccess(LoginData loginData);


    void getAreaNodeDetailSuccess(List<SubResourceNodeBean>  list);
}
