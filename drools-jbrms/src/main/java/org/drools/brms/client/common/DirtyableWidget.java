package org.drools.brms.client.common;

public interface DirtyableWidget extends IDirtyable {

    public abstract boolean isDirty();

    public abstract void resetDirty();

    public abstract void makeDirty();

}