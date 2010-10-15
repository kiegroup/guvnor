/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.LoggedInUserInfo;
import org.drools.guvnor.client.security.Capabilities;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * This is the main part of the app that lays everything out. 
 */
public class ExplorerLayoutManager {
    /**
     * These are used to decide what to display or not.
     */
    protected static Capabilities         capabilities;

    private TitlePanel                    titlePanel;
    private NavigationPanel               navigationStackLayoutPanel;
    private DockLayoutPanel               mainPanel;

    private final ExplorerViewCenterPanel centertabbedPanel = new ExplorerViewCenterPanel();

    public ExplorerLayoutManager(LoggedInUserInfo uif,
                                 Capabilities caps) {
        Preferences.INSTANCE.loadPrefs( caps );

        String tok = History.getToken();

        //we use this to decide what to display.
        BookmarkInfo bookmarkInfo = handleHistoryToken( tok );
        ExplorerLayoutManager.capabilities = caps;

        if ( bookmarkInfo.isShowChrome() ) {
            titlePanel = new TitlePanel( uif );
        }

        navigationStackLayoutPanel = new NavigationPanel( centertabbedPanel );
        setupMainPanel( bookmarkInfo );

        //Open default widgets
        if ( bookmarkInfo.isLoadAsset() ) {
            centertabbedPanel.openAsset( bookmarkInfo.getAssetId() );
        }
        centertabbedPanel.openFind();
    }

    /**
     * Create the main panel.
     * 
     */
    private void setupMainPanel(BookmarkInfo bi) {
        mainPanel = new DockLayoutPanel( Unit.EM );

        if ( bi.isShowChrome() ) {
            mainPanel.addNorth( titlePanel,
                                4 );
        }
        SplitLayoutPanel centerPanel = new SplitLayoutPanel();
        centerPanel.addWest( navigationStackLayoutPanel,
                             250 );
        centerPanel.add( centertabbedPanel );
        mainPanel.add( centerPanel );
    }

    public Panel getBaseLayout() {
        return mainPanel;
    }

    public static boolean shouldShow(Integer... capability) {
        for ( Integer cap : capability ) {
            if ( capabilities.list.contains( cap ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parse the bookmark/history token (the bit after the "#" in the URL)
     * to work out what we will display.
     */
    static BookmarkInfo handleHistoryToken(String tok) {
        if ( tok == null ) return new BookmarkInfo();
        BookmarkInfo bi = new BookmarkInfo();
        if ( tok.startsWith( "asset=" ) ) { //NON-NLS
            String uuid = null;
            //URLDecoder is not supported in GWT. We decode  ampersand (&) here by ourself. 
            if ( tok.indexOf( "%26nochrome" ) >= 0 ) {
                uuid = tok.substring( 6 ).split( "%26nochrome" )[0]; //NON-NLS
            } else {
                uuid = tok.substring( 6 ).split( "&nochrome" )[0]; //NON-NLS
            }
            bi.setLoadAsset( true );
            bi.setAssetId( uuid );
        }

        if ( tok.contains( "nochrome" ) || tok.contains( "nochrome==true" ) ) {
            bi.setShowChrome( false );
        }

        return bi;
    }

    public static class BookmarkInfo {
        private String  assetId;
        private boolean showChrome = true;
        private boolean loadAsset  = false;

        void setShowChrome(boolean showChrome) {
            this.showChrome = showChrome;
        }

        boolean isShowChrome() {
            return showChrome;
        }

        void setLoadAsset(boolean loadAsset) {
            this.loadAsset = loadAsset;
        }

        boolean isLoadAsset() {
            return loadAsset;
        }

        void setAssetId(String assetId) {
            this.assetId = assetId;
        }

        String getAssetId() {
            return assetId;
        }
    }
}
