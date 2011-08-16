package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.place.shared.Place;
import com.google.web.bindery.event.shared.Event;
import org.drools.guvnor.client.packages.ClosePlaceEvent;
import org.drools.guvnor.client.util.Activity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MultiActivityManagerCloseTabsTest extends MultiActivityManagerTestBase {

    private Place place;
    private HandlerRegistration handlerRegistration;

    @Override @Before
    public void setUp() throws Exception {
        super.setUp();
        place = mock(Place.class);
        multiActivityManager.setTabbedPanel(tabbedPanel);
        handlerRegistration = mock(HandlerRegistration.class);
    }

    protected EventBus setUpEventBus() {
        return new EventBusMock();
    }

    @Test
    public void testItIsSafeToCloseATabThatDoesNotExist() throws Exception {
        multiActivityManager.onCloseTab(new ClosePlaceEvent(new AssetEditorPlace("I-do-Not-Exist")));
        // Does nothing
    }

    @Test
    public void testClosingATabIsBlockedByTheActivity() throws Exception {
        Activity activity = goTo(place);
        when(activity.mayStop()).thenReturn(false);

        multiActivityManager.onCloseTab(new ClosePlaceEvent(place));
        verify(activity, never()).onStop();
    }

    @Test
    public void testCloseTabCallsOnStopAndRemovesWrappersHandlers() throws Exception {
        ArgumentCaptor<ResettableEventBus> resettableEventBusArgumentCaptor = ArgumentCaptor.forClass(ResettableEventBus.class);

        ClosePlaceEvent.Handler handler = mock(ClosePlaceEvent.Handler.class);

        Activity activity = goTo(place);

        when(activity.mayStop()).thenReturn(true);

        verify(activity).start(any(AcceptTabItem.class), resettableEventBusArgumentCaptor.capture());

        resettableEventBusArgumentCaptor.getValue().addHandler(ClosePlaceEvent.TYPE, handler);

        multiActivityManager.onCloseTab(new ClosePlaceEvent(place));
        verify(activity).onStop();
        verify(handlerRegistration).removeHandler();
    }

    class EventBusMock extends EventBus {

        @Override
        public <H> com.google.web.bindery.event.shared.HandlerRegistration addHandler(Event.Type<H> type, H handler) {
            return handlerRegistration;
        }

        @Override
        public <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
            return handlerRegistration;
        }

        @Override
        public <H extends EventHandler> HandlerRegistration addHandlerToSource(GwtEvent.Type<H> type, Object source, H handler) {
            return null;
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
        }

        @Override
        public void fireEventFromSource(GwtEvent<?> event, Object source) {
        }
    }

}
