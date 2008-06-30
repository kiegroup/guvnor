package com.google.gwt.core.ext.typeinfo;

/**
 *
 */
public abstract class JType {
    public JClassType isClassOrInterface() {
       throw new RuntimeException("not to be used for JavaScript compilation");
    }
}
