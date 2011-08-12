package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.PlaceChangeEvent;
import org.drools.guvnor.client.packages.ClosePlaceEvent;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MultiActivityManagerSetUpTest extends MultiActivityManagerTestBase {

    @Test
    public void testPlaceChangeEventHandlerIsSet() throws Exception {
        verify(eventBus).addHandler(
                PlaceChangeEvent.TYPE,
                multiActivityManager);
    }

    @Test
    public void testCloseTabEventHandlerIsSet() throws Exception {
        verify(eventBus).addHandler(
                ClosePlaceEvent.TYPE,
                multiActivityManager);
    }

    @Test(expected = IllegalStateException.class)
    public void testTabbedPanelCanOnlyBeSetOnce() throws Exception {
        multiActivityManager.setTabbedPanel(tabbedPanel);
        multiActivityManager.setTabbedPanel(tabbedPanel);
    }

    @Test(expected = IllegalStateException.class)
    public void testTabbedPanelIsNotSet() throws Exception {
        PlaceChangeEvent placeChangeEvent = mock(PlaceChangeEvent.class);
        multiActivityManager.onPlaceChange(placeChangeEvent);
    }
}
