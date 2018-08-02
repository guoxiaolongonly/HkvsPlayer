package com.standards.libhikvision.util;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * <描述功能>
 * @author linciping
 * @version v1.0
 * @since: 2018/6/11
 */
public class HikvisionUtil {

    private static final String TAG = "HikvisionUtil";

    /**
     * 通过网络接口取
     * @return
     */
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
