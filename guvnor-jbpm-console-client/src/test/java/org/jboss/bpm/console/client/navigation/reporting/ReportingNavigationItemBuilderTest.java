package org.jboss.bpm.console.client.navigation.reporting;

import com.google.gwt.place.shared.PlaceController;
import org.jboss.bpm.console.client.navigation.processes.ProcessNavigationViewFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class ReportingNavigationItemBuilderTest {

    private ReportingNavigationItemBuilder builder;
    private ReportingHeaderView reportingHeaderView;

    @Before
    public void setUp() throws Exception {
        reportingHeaderView = Mockito.mock(ReportingHeaderView.class);
        ProcessNavigationViewFactory navigationViewFactory = Mockito.mock(ProcessNavigationViewFactory.class);
        Mockito.when(
                navigationViewFactory.getReportingHeaderView()
        ).thenReturn(
                reportingHeaderView
        );
        ReportingTreeView reportingTreeView = Mockito.mock(ReportingTreeView.class);
        Mockito.when(
                navigationViewFactory.getReportingTreeView()
        ).thenReturn(
                reportingTreeView
        );
        PlaceController placeController = Mockito.mock(PlaceController.class);
        builder = new ReportingNavigationItemBuilder(navigationViewFactory, placeController);
    }

    @Test
    public void testAlwaysBuilds() throws Exception {
        Assert.assertTrue(builder.hasPermissionToBuild());
    }

    @Test
    public void testHeader() throws Exception {
        assertEquals(reportingHeaderView, builder.getHeader());
    }

    @Test
    public void testContent() throws Exception {
        Assert.assertTrue(builder.getContent() instanceof ReportingTree);
    }
}
