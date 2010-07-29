/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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