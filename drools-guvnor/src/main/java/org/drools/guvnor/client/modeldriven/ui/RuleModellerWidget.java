package org.drools.guvnor.client.modeldriven.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import java.util.ArrayList;
import java.util.List;
import org.drools.guvnor.client.common.DirtyableComposite;

/**
 * A superclass for the widgets present in RuleModeller. 
 * @author esteban.aliverti@gmail.com
 */
public abstract class RuleModellerWidget extends DirtyableComposite {

    private RuleModeller modeller;

    private boolean modified;

    private List<Command> onModifiedCommands = new ArrayList<Command>();

    public RuleModellerWidget(RuleModeller modeller) {
        this.modeller = modeller;
    }

    /**
     * Dictates if the widget's state is RO or not. Sometimes RuleModeller will
     * force this state (i.e. when lockLHS() or lockRHS()), but some other times,
     * the widget itself is responsible to autodetect its state.
     * @return
     */
    public abstract boolean isReadOnly();

    public RuleModeller getModeller() {
        return modeller;
    }

    protected void setModified(boolean modified) {
        if (modified){
            executeOnModifiedCommands();
        }
        this.modified = modified;
    }

    public void addOnModifiedCommand(Command command){
        this.onModifiedCommands.add(command);
    }

    private void executeOnModifiedCommands(){
        for (Command command : onModifiedCommands) {
            command.execute();
        }
    }

}
