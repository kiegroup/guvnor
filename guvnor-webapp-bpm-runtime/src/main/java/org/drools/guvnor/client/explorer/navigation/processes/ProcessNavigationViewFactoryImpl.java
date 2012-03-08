package org.drools.guvnor.client.explorer.navigation.processes;

import org.drools.guvnor.client.explorer.navigation.reporting.ReportingHeaderView;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingHeaderViewImpl;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingTreeView;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingTreeViewImpl;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsHeaderView;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsHeaderViewImpl;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsTreeView;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsTreeViewImpl;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksHeaderView;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksHeaderViewImpl;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksTreeView;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksTreeViewImpl;

public class ProcessNavigationViewFactoryImpl implements ProcessNavigationViewFactory {

    public ProcessesHeaderView getProcessesHeaderView() {
        return new ProcessesHeaderViewImpl();
    }

    public ProcessesTreeView getProcessesTreeView() {
        return new ProcessesTreeViewImpl();
    }

    public ReportingHeaderView getReportingHeaderView() {
        return new ReportingHeaderViewImpl();
    }

    public ReportingTreeView getReportingTreeView() {
        return new ReportingTreeViewImpl();
    }

    public TasksHeaderView getTasksHeaderView() {
        return new TasksHeaderViewImpl();
    }

    public TasksTreeView getTasksTreeView() {
        return new TasksTreeViewImpl();
    }

    public SettingsHeaderView getSettingsHeaderView() {
        return new SettingsHeaderViewImpl();
    }

    public SettingsTreeView getSettingsTreeView() {
        return new SettingsTreeViewImpl();
    }
}
