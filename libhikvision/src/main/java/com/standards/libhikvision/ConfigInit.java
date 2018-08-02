package com.standards.libhikvision;

import android.app.Application;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hikvision.sdk.VMSNetSDK;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 */

public class ConfigInit {
    public static void init(Application application) {
        //海康威视监控
        MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        TalkClientSDK.initLib();
        VMSNetSDK.init(application);
    }
}
