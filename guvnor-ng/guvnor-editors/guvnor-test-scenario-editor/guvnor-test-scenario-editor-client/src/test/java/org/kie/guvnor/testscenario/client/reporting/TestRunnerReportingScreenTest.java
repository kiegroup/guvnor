package org.kie.guvnor.testscenario.client.reporting;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestRunnerReportingScreenTest {

    private TestRunnerReportingView view;
    private TestRunnerReportingScreen screen;

    @Before
    public void setUp() throws Exception {
        view = mock(TestRunnerReportingView.class);
        screen = new TestRunnerReportingScreen(view);
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify(view).setPresenter(screen);
    }
}
