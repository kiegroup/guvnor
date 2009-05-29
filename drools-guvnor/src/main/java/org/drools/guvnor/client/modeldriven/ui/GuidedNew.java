package org.drools.guvnor.client.modeldriven.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TableListener;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.CompositeFactPattern;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.common.SmallLabel;

/**
 * @author Michael Neale
 */
public class GuidedNew extends Composite {
    private RuleModel model;
    private FlexTable layout;

    public GuidedNew(RuleModel model, RuleViewer view) {
        this.model = model;
        this.layout = new FlexTable();
        layout.setWidget(0, 0, new SmallLabel("When"));
        for(IPattern p : model.lhs) {
            if (p instanceof FactPattern) {
                doLayout((FactPattern)p, 1);            
            } else if (p instanceof CompositeFactPattern) {
                doLayout((CompositeFactPattern)p);
            }
        }

        initWidget(layout);

    }

    private void doLayout(CompositeFactPattern factPattern) {
        layout.setWidget(layout.getRowCount() + 1, 1, new SmallLabel(factPattern.type + ":"));
        for(FactPattern fp: factPattern.patterns) {
            doLayout(fp, 2);
        }
    }

    private void doLayout(FactPattern factPattern, int col) {
        int row = layout.getRowCount() + 1;
        if (factPattern.constraintList == null || factPattern.constraintList.constraints.length == 0) {
            //single one
            layout.setWidget(layout.getRowCount() + 1, col, new SmallLabel("There is a " + factPattern.factType + " [" + factPattern.boundName + "]"));
        } else {
            layout.setWidget(layout.getRowCount() + 1, col, new SmallLabel("There is a " + factPattern.factType + " [" + factPattern.boundName + "] with:"));


        }
    }
}
