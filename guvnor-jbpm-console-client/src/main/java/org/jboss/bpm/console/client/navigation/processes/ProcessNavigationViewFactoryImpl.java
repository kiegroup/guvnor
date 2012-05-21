package org.jboss.bpm.console.client.navigation.processes;

import org.jboss.bpm.console.client.navigation.reporting.ReportingHeaderView;
import org.jboss.bpm.console.client.navigation.reporting.ReportingHeaderViewImpl;
import org.jboss.bpm.console.client.navigation.reporting.ReportingTreeViewImpl;
import org.jboss.bpm.console.client.navigation.reporting.ReportingTreeView;
import org.jboss.bpm.console.client.navigation.settings.SettingsHeaderViewImpl;
import org.jboss.bpm.console.client.navigation.settings.SettingsTreeView;
import org.jboss.bpm.console.client.navigation.tasks.TasksHeaderView;
import org.jboss.bpm.console.client.navigation.tasks.TasksHeaderViewImpl;
import org.jboss.bpm.console.client.navigation.tasks.TasksTreeViewImpl;
import org.jboss.bpm.console.client.navigation.settings.SettingsHeaderView;
import org.jboss.bpm.console.client.navigation.settings.SettingsTreeViewImpl;
import org.jboss.bpm.console.client.navigation.tasks.TasksTreeView;

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
