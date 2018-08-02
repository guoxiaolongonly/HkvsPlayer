package com.standards.libhikvision.bean;

import java.io.Serializable;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 */

public class MonitorInfo implements Serializable {
    private AccountInfo accountInfo;
    private ResourceNode resourceNode;

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public void setResourceNode(ResourceNode resourceNode) {
        this.resourceNode = resourceNode;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public ResourceNode getResourceNode() {
        return resourceNode;
    }
}
