package org.drools.guvnor.client.explorer.navigation.reporting;

import com.google.gwt.place.shared.PlaceController;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReportingNavigationItemBuilderTest {

    private ReportingNavigationItemBuilder builder;
    private ReportingHeaderView reportingHeaderView;

    @Before
    public void setUp() throws Exception {
        reportingHeaderView = mock(ReportingHeaderView.class);
        NavigationViewFactory navigationViewFactory = mock(NavigationViewFactory.class);
        when(
                navigationViewFactory.getReportingHeaderView()
        ).thenReturn(
                reportingHeaderView
        );
        ReportingTreeView reportingTreeView = mock(ReportingTreeView.class);
        when(
                navigationViewFactory.getReportingTreeView()
        ).thenReturn(
                reportingTreeView
        );
        PlaceController placeController = mock(PlaceController.class);
        builder = new ReportingNavigationItemBuilder(navigationViewFactory, placeController);
    }

    @Test
    public void testAlwaysBuilds() throws Exception {
        assertTrue(builder.hasPermissionToBuild());
    }

    @Test
    public void testHeader() throws Exception {
        assertEquals(reportingHeaderView, builder.getHeader());
    }

    @Test
    public void testContent() throws Exception {
        assertTrue(builder.getContent() instanceof ReportingTree);
    }
}
