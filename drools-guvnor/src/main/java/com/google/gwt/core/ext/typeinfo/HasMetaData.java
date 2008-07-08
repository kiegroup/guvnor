package com.google.gwt.core.ext.typeinfo;

/**
 *
 */
public interface HasMetaData {
    void addMetaData(String tagName, String[] values);

    String[][] getMetaData(String tagName);

    String[] getMetaDataTags();
}
