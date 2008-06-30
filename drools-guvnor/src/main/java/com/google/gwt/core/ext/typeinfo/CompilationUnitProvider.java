package com.google.gwt.core.ext.typeinfo;

import com.google.gwt.core.ext.UnableToCompleteException;

import java.util.Comparator;

/**
 *
 */
public interface CompilationUnitProvider {

  Comparator LOCATION_COMPARATOR = new Comparator() {
    public int compare(Object o1, Object o2) {
      String loc1 = ((CompilationUnitProvider) o1).getLocation();
      String loc2 = ((CompilationUnitProvider) o2).getLocation();
      return loc1.compareTo(loc2);
    }
  };

  long getLastModified() throws UnableToCompleteException;

  String getLocation();

  String getPackageName();

  char[] getSource() throws UnableToCompleteException;

  boolean isTransient();
}
