package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RuntimePerspectiveActivityTest {

    private RuntimePerspectiveView view;
    private RuntimePerspectiveActivity authorPerspective;
    private RuntimePerspectiveView.Presenter presenter;
    private AcceptsOneWidget rootPanel;

    @Before
    public void setUp() throws Exception {
        rootPanel = mock(AcceptsOneWidget.class);
        view = mock(RuntimePerspectiveView.class);

        ClientFactory clientFactory = mock(ClientFactory.class);
        when(clientFactory.getRuntimePerspectiveView()).thenReturn(view);

        authorPerspective = new RuntimePerspectiveActivity(clientFactory);
        presenter = getPresenter();

        authorPerspective.start(rootPanel, mock(EventBus.class));
    }

    public RuntimePerspectiveView.Presenter getPresenter() {
        return authorPerspective;
    }

    @Test
    public void testPresenterAndViewAreSet() throws Exception {
        verify(rootPanel).setWidget(view);
        verify(view).setPresenter(presenter);
    }
}
