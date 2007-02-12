/**
 * 
 */
package org.drools.scm.jcr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.scm.ScmAction;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;

public class CompositeJcrAction
    implements
    ScmAction {
    private List actions;

    public CompositeJcrAction() {
        this.actions = Collections.EMPTY_LIST;
    }

    public void addScmAction(ScmAction action) {
        if ( actions == Collections.EMPTY_LIST ) {
            this.actions = new ArrayList();
        }
        this.actions.add( action );
    }

    public void applyAction(Object context) throws SVNException {
//        ISVNEditor editor = ( ISVNEditor ) context;
        for ( Iterator it = this.actions.iterator(); it.hasNext(); ) {
            ScmAction action = (ScmAction) it.next();
//            action.applyAction( editor );
        }
    }
}