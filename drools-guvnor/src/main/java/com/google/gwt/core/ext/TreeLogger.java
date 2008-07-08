package com.google.gwt.core.ext;

public interface TreeLogger {
    class Type {
    }

    public TreeLogger branch(Type type, String msg, Throwable caught);

    public boolean isLoggable(Type type);

    public void log(Type type, String msg, Throwable caught);
}
