package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.messages.ConstantsCore;

public class TasksHeaderViewImpl extends StackItemHeaderViewImpl implements TasksHeaderView {

    private static ConstantsCore constants = GWT.create(ConstantsCore.class);

    public TasksHeaderViewImpl() {
        setText(constants.Tasks());
    }

}
