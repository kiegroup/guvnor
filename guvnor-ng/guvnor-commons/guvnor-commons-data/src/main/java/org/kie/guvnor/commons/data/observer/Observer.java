package org.kie.guvnor.commons.data.observer;

public class Observer<T> {

    private int hashCode;

    public Observer(T observable) {
        hashCode = observable.hashCode();
    }

    public boolean isDirty(T observable) {
        return hashCode == observable.hashCode();
    }
}
