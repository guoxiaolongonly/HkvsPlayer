package com.standards.libhikvision.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 */

public class ResourceNode implements Serializable{
    public String id;
    public int node_type;
    public String name;
    public List<HkCameraInfo> cameras;
}
