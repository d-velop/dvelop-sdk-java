package com.dvelop.sdk.tenant;

public class Tenant {
    String baseuri;
    String tenantId;

    public Tenant(String baseuri, String tenantId) {
        this.baseuri = baseuri;
        this.tenantId = tenantId;
    }

    public String getBaseuri() {
        return baseuri;
    }

    public void setBaseuri(String baseuri) {
        this.baseuri = baseuri;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
