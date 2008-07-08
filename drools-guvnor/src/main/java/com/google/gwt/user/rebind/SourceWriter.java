package com.google.gwt.user.rebind;

import com.google.gwt.core.ext.TreeLogger;

/**
 *
 */
public interface SourceWriter {
    public void println(String s);

    public void commit(TreeLogger logger);

    public void indent() ;

    public void outdent() ;
}
