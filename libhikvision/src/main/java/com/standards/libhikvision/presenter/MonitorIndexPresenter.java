package com.standards.libhikvision.presenter;


import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.bean.SubResourceParam;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.standards.libhikvision.bean.HkCameraInfo;
import com.standards.libhikvision.bean.MonitorInfo;
import com.standards.libhikvision.util.Constant;
import com.standards.libhikvision.view.IMonitorIndexView;

import java.util.ArrayList;
import java.util.List;

/**
 *  <描述功能>
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @since: 2018/6/20 15:25
 */

public class MonitorIndexPresenter {
    private IMonitorIndexView monitorIndexView;

    public MonitorIndexPresenter(IMonitorIndexView monitorIndexView) {
        this.monitorIndexView = monitorIndexView;
    }


    public void login(String ip, String username, String password, String macAddress) {
        VMSNetSDK.getInstance().Login(ip, username, password, macAddress, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                monitorIndexView.showHint("登录失败！");
            }

            @Override
            public void onStatusCallback(int i) {
                switch (i) {
                    case Constant.SDK_NO_INIT:
                        monitorIndexView.showHint("登录失败，SDK未初始化！");
                        break;
                    case Constant.ACCOUNT_NO_ALLOW:
                        monitorIndexView.showHint("登录失败，帐号或密码不匹配！");
                        break;
                    case Constant.LOGIN_OUT_TIME:
                        monitorIndexView.showHint("登录超时！");
                        break;
                }
            }

            @Override
            public void onSuccess(Object o) {
                if (o instanceof LoginData) {
                    monitorIndexView.loginSuccess((LoginData) o);
                }
            }
        });
    }

    public void getSubResourceList(int parentNodeType, String pId) {
        VMSNetSDK.getInstance().getSubResourceList(1, 999, SDKConstant.SysType.TYPE_VIDEO, parentNodeType, pId, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                monitorIndexView.showHint("获取设备信息失败！");
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof SubResourceParam) {
                    List<SubResourceNodeBean> list = ((SubResourceParam) obj).getNodeList();
                    monitorIndexView.getAreaNodeDetailSuccess(list);
                }
            }
        });
    }
    public void getSubResourceList(MonitorInfo monitorInfo) {

        List<SubResourceNodeBean> list = new ArrayList<>();
        for(HkCameraInfo hkCameraInfo :monitorInfo.getResourceNode().cameras)
        {
            SubResourceNodeBean subResourceNodeBean = new SubResourceNodeBean();
            subResourceNodeBean.setName(hkCameraInfo.name);
            subResourceNodeBean.setSysCode(hkCameraInfo.id);
            list.add(subResourceNodeBean);
        }
        monitorIndexView.getAreaNodeDetailSuccess(list);
    }
}
