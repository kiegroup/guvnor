package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.junit.Before;
import org.drools.guvnor.client.explorer.AuthorPerspectiveView.Presenter;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AuthorPerspectiveActivityTest {

    private AuthorPerspectiveView view;
    private AuthorPerspectiveActivity authorPerspective;
    private Presenter presenter;
    private AcceptsOneWidget rootPanel;

    @Before
    public void setUp() throws Exception {
        rootPanel = mock(AcceptsOneWidget.class);
        view = mock(AuthorPerspectiveView.class);

        ClientFactory clientFactory = mock(ClientFactory.class);
        when(clientFactory.getAuthorPerspectiveView()).thenReturn(view);

        authorPerspective = new AuthorPerspectiveActivity(clientFactory);
        presenter = getPresenter();

        authorPerspective.start(rootPanel, mock(EventBus.class));
    }

    public Presenter getPresenter() {
        return authorPerspective;
    }

    @Test
    public void testPresenterAndViewAreSet() throws Exception {
        verify(rootPanel).setWidget(view);
        verify(view).setPresenter(presenter);
    }
}
