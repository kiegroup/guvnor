package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.explorer.navigation.RuntimeBaseTree;
import org.drools.guvnor.client.messages.Constants;

public class TasksTreeViewImpl extends RuntimeBaseTree implements TasksTreeView {

    private static Constants constants = GWT.create(Constants.class);

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
