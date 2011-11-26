package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.util.Activity;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class MultiActivityManagerChangePlaceTest extends MultiActivityManagerTestBase {

    @Test
    public void testChangePlace() throws Exception {
        Place oldPlace = mock(Place.class);
        Place newPlace = mock(Place.class);

        multiActivityManager.setTabbedPanel(tabbedPanel);

        verifyGoToNewPlace(oldPlace, "oldMockTabTitle");
        verifyGoToNewPlace(newPlace, "newMockTabTitle");
    }

    @Test
    public void testPlaceGetsShownIfItAlreadyActive() throws Exception {
        Place oldPlace = mock(Place.class);
        Place newPlace = mock(Place.class);

        multiActivityManager.setTabbedPanel(tabbedPanel);

        verifyGoToNewPlace(oldPlace, "oldMockTabTitle");
        verifyGoToExistingPlace(newPlace);
    }

    @Test
    public void testGoingNoWhereGetsYouNoWhere() throws Exception {
        multiActivityManager.setTabbedPanel(tabbedPanel);
        PlaceChangeEvent placeChangeEvent = setUpPlaceChangeEvent(Place.NOWHERE);
        multiActivityManager.onPlaceChange(placeChangeEvent);
        verify(tabbedPanel, never()).show(any(Place.class));
        verify(tabbedPanel, never()).addTab(anyString(), Matchers.<IsWidget>any(), any(Place.class));
    }

    private void verifyGoToExistingPlace(Place newPlace) {
        goTo(newPlace);
        tabbedPanel.show(newPlace);
        verify(tabbedPanel, never()).addTab(anyString(), Matchers.<IsWidget>any(), eq(newPlace));
    }

    private void verifyGoToNewPlace(Place place, String tabTitle) {
        ArgumentCaptor<AcceptItem> acceptTabItemArgumentCaptor = ArgumentCaptor.forClass(AcceptItem.class);
        IsWidget tabContentWidget = mock(IsWidget.class);

        Activity activity = goTo(place);

        verify(activity).start(acceptTabItemArgumentCaptor.capture(), any(ResettableEventBus.class));

        acceptTabItemArgumentCaptor.getValue().add(tabTitle, tabContentWidget);
        verify(tabbedPanel).addTab(tabTitle, tabContentWidget, place);
    }

}
