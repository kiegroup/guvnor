/**
 * 
 */
package org.drools.scm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;

public class CompositeScmAction
    implements
    ScmAction {
    private List actions;

    public CompositeScmAction() {
        this.actions = Collections.EMPTY_LIST;
    }

    public void addScmAction(ScmAction action) {
        if ( actions == Collections.EMPTY_LIST ) {
            this.actions = new ArrayList();
        }
        this.actions.add( action );
    }

    public void applyAction(Object context) throws Exception {
        for ( Iterator it = this.actions.iterator(); it.hasNext(); ) {
            ScmAction action = (ScmAction) it.next();
            action.applyAction( context );
        }
    }
}