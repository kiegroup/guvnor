package org.drools.guvnor.client.util;

public interface SaveCommand<T> {
    public void save(T t);
}
