package org.drools.guvnor.client.decisiontable;

import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;

/**
 * Interface defining commands relating to Column operations
 * 
 * @author manstis
 * 
 */
public interface ColumnCentricCommand {

    /**
     * Causes the Command to perform its encapsulated behaviour.
     * 
     * @param column
     *            The column on which the command should operate
     */
    public void execute(DTColumnConfig column);
}
