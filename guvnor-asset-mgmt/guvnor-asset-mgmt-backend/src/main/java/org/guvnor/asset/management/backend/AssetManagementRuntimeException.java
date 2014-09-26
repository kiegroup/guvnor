package org.guvnor.asset.management.backend;

public class AssetManagementRuntimeException extends RuntimeException {

    public AssetManagementRuntimeException(String s) {
        super(s);
    }

    public AssetManagementRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AssetManagementRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
