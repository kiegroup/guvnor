package com.google.gwt.core.ext;

import com.google.gwt.core.ext.typeinfo.TypeOracle;

import java.io.PrintWriter;

/**
 *
 */
public interface GeneratorContext {

    public TypeOracle getTypeOracle();

    public PrintWriter tryCreate(TreeLogger logger, String packageName, String generatedClassName);
}
