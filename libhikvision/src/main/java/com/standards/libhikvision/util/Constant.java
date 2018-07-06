package com.standards.libhikvision.util;

/**
 * @author linciping
 * @time 2018/5/29
 * @note
 */
public interface Constant {

    String IMAGE_TYPE = "image_type";
    String IMAGE_URLS = "image_urls";
    String FILE_URLS = "file_urls";
    String TARGET = "target";
    String IMAGE_URL = "image_url";

    int SDK_NO_INIT = 202;
    int ACCOUNT_NO_ALLOW = 203;
    int LOGIN_OUT_TIME = 204;

    /**
     * Intent相关常量
     */
    interface IntentKey {
        /**
         * 获取根节点数据
         */
        String GET_ROOT_NODE = "getRootNode";
        /**
         * 获取子节点列表
         */
        String GET_SUB_NODE = "getChildNode";
        /**
         * 父节点类型
         */
        String PARENT_NODE_TYPE = "parentNodeType";
        /**
         * 父节点ID
         */
        String PARENT_ID = "parentId";
        /**
         * 监控点资源
         */
        String CAMERA = "Camera";

    }

    /**
     * 监控点资源
     */
    String SCREEN_DIR = "index.jpg";
}
