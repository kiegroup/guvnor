package com.google.gwt.core.ext.typeinfo;

/**
 *
 */
public interface HasMetaData {
  /**
   * Adds additional metadata.
   */
  void addMetaData(String tagName, String[] values);

  /**
   * Gets each list of metadata for the specified tag name.
   */
  String[][] getMetaData(String tagName);

  /**
   * Gets the name of available metadata tags.
   */
  String[] getMetaDataTags();
}