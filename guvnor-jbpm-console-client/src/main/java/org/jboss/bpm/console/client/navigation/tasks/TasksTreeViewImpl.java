package org.jboss.bpm.console.client.navigation.tasks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.TreeItem;
import org.jboss.bpm.console.client.navigation.RuntimeBaseTree;
import org.drools.guvnor.client.messages.ConstantsCore;

public class TasksTreeViewImpl extends RuntimeBaseTree implements TasksTreeView {

    private static ConstantsCore constants = GWT.create(ConstantsCore.class);

    private Presenter presenter;
    private TreeItem personalTasksTreeItem;
    private TreeItem groupTasksTreeItem;

    public TasksTreeViewImpl() {
        super();
        personalTasksTreeItem = addItem(constants.PersonalTasks());
        groupTasksTreeItem = addItem(constants.GroupTasks());
    }

    @Override
    protected void onSelection(TreeItem selectedItem) {
        if (selectedItem.equals(personalTasksTreeItem)) {
            presenter.onPersonalTasksSelected();
        } else if (selectedItem.equals(groupTasksTreeItem)) {
            presenter.onGroupTasksSelected();
        }
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
