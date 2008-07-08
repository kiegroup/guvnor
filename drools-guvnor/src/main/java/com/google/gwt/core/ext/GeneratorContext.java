package com.google.gwt.core.ext;

import com.google.gwt.core.ext.typeinfo.TypeOracle;

import java.io.PrintWriter;
import java.io.OutputStream;

public interface GeneratorContext {
    void commit(TreeLogger logger, PrintWriter pw);

  void commitResource(TreeLogger logger, OutputStream os)
      throws UnableToCompleteException;

  PropertyOracle getPropertyOracle();

  TypeOracle getTypeOracle();

  PrintWriter tryCreate(TreeLogger logger, String packageName, String simpleName);

  OutputStream tryCreateResource(TreeLogger logger, String partialPath)
      throws UnableToCompleteException;
}
