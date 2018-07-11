package com.standards.libhikvision.util;

import com.hikvision.sdk.consts.SDKConstant;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/6/28 10:05
 */

public class HintUtil {

    public static String getLiveScreenShotHint(int status) {
        String hingResult = "";
        switch (status) {
            case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                hingResult = "SD卡不可用";
                break;
            case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                hingResult = "SD卡空间不足";
                break;
            case SDKConstant.LiveSDKConstant.CAPTURE_FAILED:
                hingResult = "抓拍失败";
                break;
            case SDKConstant.LiveSDKConstant.CAPTURE_SUCCESS:
                hingResult = "抓拍成功";
                break;
            case 6:
                hingResult = "录像中不能抓拍";
                break;
            default:
                hingResult = "当前无法抓拍！";
                break;
        }
        return hingResult;
    }

    public static String getBackScreenShotHint(int status) {
        String hingResult = "";
        switch (status) {
            case SDKConstant.PlayBackSDKConstant.SD_CARD_UN_USABLE:
                hingResult = "SD卡不可用";
                break;
            case SDKConstant.PlayBackSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                hingResult = "SD卡空间不足";
                break;
            case SDKConstant.PlayBackSDKConstant.CAPTURE_FAILED:
                hingResult = "抓拍失败";
                break;
            case SDKConstant.PlayBackSDKConstant.CAPTURE_SUCCESS:
                hingResult = "抓拍成功";
                break;
            case 6:
                hingResult = "录像中不能抓拍";
                break;
            default:
                hingResult = "当前无法抓拍！";
                break;
        }
        return hingResult;
    }


    public static String getLiveRecordHint(int status) {
        String hingResult = "";
        switch (status) {
            case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                hingResult = "SD卡不可用";
                break;
            case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                hingResult = "存储空间不足";
                break;
            case SDKConstant.LiveSDKConstant.RECORD_FAILED:
                hingResult = "录像启动失败";
                break;
            case SDKConstant.LiveSDKConstant.RECORD_SUCCESS:
                hingResult = "启动录像成功";
                break;
            default:
                hingResult = "当前无法录像！";
                break;
        }
        return hingResult;
    }

    public static String getBackPlayRecordHint(int status) {
        String hingResult = "";
        switch (status) {
            case SDKConstant.PlayBackSDKConstant.SD_CARD_UN_USABLE:
                hingResult = "SD卡不可用";
                break;
            case SDKConstant.PlayBackSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                hingResult = "存储空间不足";
                break;
            case SDKConstant.PlayBackSDKConstant.RECORD_FAILED:
                hingResult = "录像启动失败";
                break;
            case SDKConstant.PlayBackSDKConstant.RECORD_SUCCESS:
                hingResult = "启动录像成功";
                break;
            default:
                hingResult = "当前无法录像！";
                break;
        }
        return hingResult;
    }
}
