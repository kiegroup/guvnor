package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.DirtyableComposite;

/**
 * A superclass for the widgets present in RuleModeller. 
 * @author esteban.aliverti@gmail.com
 */
public abstract class RuleModellerWidget extends DirtyableComposite {

    /**
     * Dictates if the widget's state is RO or not. Sometimes RuleModeller will
     * force this state (i.e. when lockLHS() or lockRHS()), but some other times,
     * the widget itself is responsible to autodetect its state.
     * @return
     */
    public abstract boolean isReadOnly();
}
