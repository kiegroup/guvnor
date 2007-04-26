package org.drools.brms.client.common;

public interface IDirtyable {

    public abstract boolean isDirty();

    public abstract void resetDirty();

    public abstract void makeDirty();

}