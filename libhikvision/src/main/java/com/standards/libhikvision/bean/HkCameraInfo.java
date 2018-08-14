package com.standards.libhikvision.bean;

import java.io.Serializable;

/**
 * author: x
 * create on: 2018/7/13 14:56
 * description:
 */

public class HkCameraInfo implements Serializable{

    public String id;
    public String name;

    public HkCameraInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public HkCameraInfo() {
    }
}
