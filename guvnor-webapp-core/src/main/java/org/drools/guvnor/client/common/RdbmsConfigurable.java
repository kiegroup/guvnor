package org.drools.guvnor.client.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RdbmsConfigurable implements Serializable, IsSerializable {
    private String dbType;
    private boolean jndi;
    private String dbDriver;
    private String dbUrl;
    private String dbUser;
    private String dbPass;
    private String jndiDsName;

    public String getDbType() {
        return dbType;
    }
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
    public boolean isJndi() {
        return jndi;
    }
    public void setJndi(boolean jndi) {
        this.jndi = jndi;
    }
    public String getDbDriver() {
        return dbDriver;
    }
    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }
    public String getDbUrl() {
        return dbUrl;
    }
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    public String getDbUser() {
        return dbUser;
    }
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }
    public String getDbPass() {
        return dbPass;
    }
    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }
    public String getJndiDsName() {
        return jndiDsName;
    }
    public void setJndiDsName(String jndiDsName) {
        this.jndiDsName = jndiDsName;
    }

    public Map<String, Object> getMapRep() {
        Map<String, Object> maprep = new HashMap<String, Object>();
        maprep.put("dbType", getDbType());
        maprep.put("dbDriver", getDbDriver());
        maprep.put("dbUrl", getDbUrl());
        maprep.put("dbUser", getDbUser());
        maprep.put("dbPass", getDbPass());
        maprep.put("jndiDsName", getJndiDsName());

        return maprep;
    }

}
