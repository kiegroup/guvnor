package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IFramePerspectiveConfiguration implements IsSerializable {

    private String uuid = null;
    private String name;
    private String url;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
