/**
 * 
 */
package org.drools.scm;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;

public interface ScmAction {
    public void applyAction(Object context) throws Exception;
}