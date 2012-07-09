package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.testing.CollectionFieldData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;

public class ListEditor extends VerticalPanel {


    public ListEditor(final CollectionFieldData field, FieldConstraintHelper helper, final ScenarioParentWidget parent) {
        if (field.getCollectionFieldList().isEmpty()) {
            add(new ImageButton(DroolsGuvnorImages.INSTANCE.itemImages().newItem(),
                    Constants.INSTANCE.AElementToAddInCollectionList(),
                    new ClickHandler() {

                        public void onClick(ClickEvent w) {
                            FieldData fieldData = new FieldData();
                            fieldData.setName(field.getName());
                            field.getCollectionFieldList().add(fieldData);
                            parent.renderEditor();
                        }
                    }));
        } else {
            int i = 0;
            for (final FieldData fieldData : field.getCollectionFieldList()) {
                add(new ListEditorRow(i, field, fieldData, helper, parent));
                i++;
            }
        }
    }

}
