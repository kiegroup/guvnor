package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;

import org.drools.guvnor.client.asseteditor.MultiViewRow;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MultiAssetActivityTest {

    private MultiAssetView view;
    private MultiAssetActivity activity;
    private ClientFactory clientFactory;
    private MultiAssetPlace place;

    @Before
    public void setUp() throws Exception {
        clientFactory = mock( ClientFactory.class );

        view = mock( MultiAssetView.class );
        NavigationViewFactory navigationViewFactory = mock( NavigationViewFactory.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        when(
                navigationViewFactory.getMultiAssetView()
        ).thenReturn(
                view
        );

        place = new MultiAssetPlace( createMultiViewRows() );
        activity = new MultiAssetActivity( place, clientFactory );
    }

    private List<MultiViewRow> createMultiViewRows() {
        List<MultiViewRow> multiViewRows = new ArrayList<MultiViewRow>();
        multiViewRows.add( new MultiViewRow( "firstUuid", "firstName", "firstFormat" ) );
        multiViewRows.add( new MultiViewRow( "secondUuid", "secondName", "secondFormat" ) );
        multiViewRows.add( new MultiViewRow( "thirdUuid", "thirdName", "thirdFormat" ) );
        return multiViewRows;
    }

    @Test
    public void testStart() throws Exception {

        AcceptItem acceptTabItem = mock( AcceptItem.class );
        EventBus eventBus = mock( EventBus.class );       
        startActivity( acceptTabItem, eventBus );

        verify( view ).init(
                place.getMultiViewRows().toArray( new MultiViewRow[place.getMultiViewRows().size()] ),
                clientFactory,
                eventBus);

        verify( acceptTabItem ).add( "[ firstName, secondName, thirdName ]", view );
    }

    private void startActivity(AcceptItem acceptTabItem, EventBus eventBus) {
        activity.start( acceptTabItem, eventBus );
    }

}
