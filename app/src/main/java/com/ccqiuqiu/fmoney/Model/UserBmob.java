package com.ccqiuqiu.fmoney.Model;

import cn.bmob.v3.BmobUser;

/**
 * Created by cc on 2016/2/19.
 */
public class UserBmob extends BmobUser{

    private String installationId;

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }
}
