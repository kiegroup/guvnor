package org.drools.guvnor.client.ruleeditor;

import java.util.Collection;
import java.util.HashSet;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.menu.CheckItem;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.CheckItemListener;
import com.gwtext.client.widgets.menu.event.CheckItemListenerAdapter;

/**
 * 
 * @author toni rikkola
 *
 */
public class MultiViewEditor extends GuvnorEditor {

    private Constants                   constants       = GWT.create( Constants.class );

    private VerticalPanel               viewsPanel      = new VerticalPanel();

    private CheckItem                   showMetadata    = new CheckItem( constants.Metadata(),
                                                                         false );
    private CheckItem                   showDescription = new CheckItem( constants.DescriptionAndDiscussion(),
                                                                         false );

    private Command                     closeCommand;

    private final Collection<RuleAsset> assets;
    private Collection<RuleViewer>      ruleViews       = new HashSet<RuleViewer>();

    private final EditItemEvent         editItemEvent;

    public MultiViewEditor(RuleAsset[] assets,
                           EditItemEvent editItemEvent) {
        this.editItemEvent = editItemEvent;
        this.assets = new HashSet<RuleAsset>();
        for ( RuleAsset ruleAsset : assets ) {
            this.assets.add( ruleAsset );
        }

        VerticalPanel rootPanel = new VerticalPanel();

        rootPanel.setWidth( "100%" );

        rootPanel.add( createToolbar() );

        viewsPanel.setWidth( "100%" );
        rootPanel.add( viewsPanel );

        doViews();

        initWidget( rootPanel );
    }

    private Toolbar createToolbar() {
        Toolbar toolbar = new Toolbar();

        ToolbarButton checkinAll = new ToolbarButton( constants.SaveAllChanges() );
        checkinAll.addListener( new ButtonListenerAdapter() {
            public void onClick(com.gwtext.client.widgets.Button button,
                                EventObject e) {
                checkin( false );
            }
        } );
        toolbar.addButton( checkinAll );
        ToolbarButton checkinAndCloseAll = new ToolbarButton( constants.SaveAndCloseAll() );
        toolbar.addButton( checkinAndCloseAll );
        checkinAndCloseAll.addListener( new ButtonListenerAdapter() {
            public void onClick(com.gwtext.client.widgets.Button button,
                                EventObject e) {
                checkin( true );
            }
        } );

        CheckItemListener refresh = new CheckItemListenerAdapter() {
            @Override
            public void onCheckChange(CheckItem item,
                                      boolean checked) {
                doViews();
            }
        };

        Menu layoutMenu = new Menu();
        showMetadata.addListener( refresh );
        layoutMenu.addItem( showMetadata );
        showDescription.addListener( refresh );
        layoutMenu.addItem( showDescription );
        ToolbarMenuButton layout = new ToolbarMenuButton( constants.Show(),
                                                          layoutMenu );
        toolbar.addButton( layout );

        return toolbar;
    }

    private void doViews() {

        viewsPanel.clear();
        ruleViews.clear();

        for ( final RuleAsset asset : assets ) {
            Panel panel = new Panel( asset.metaData.name );
            panel.setIconCls( EditorLauncher.getAssetFormatBGStyle( asset.metaData.format ) ); //NON-NLS
            panel.setCollapsible( true );
            panel.setTitleCollapse( true );
            panel.setCollapsed( true );
            panel.setWidth( "100%" );

            final RuleViewer ruleViewer = new RuleViewer( asset,
                                                          this.editItemEvent );
            ruleViewer.setDocoVisible( showDescription.isChecked() );
            ruleViewer.setMetaVisible( showMetadata.isChecked() );

            ruleViewer.setWidth( "100%" );

            panel.add( ruleViewer );
            ruleViewer.setCloseCommand( new Command() {

                public void execute() {
                    ruleViews.remove( ruleViewer );
                    assets.remove( asset );
                    doViews();
                }
            } );

            ruleViews.add( ruleViewer );

            viewsPanel.add( panel );
        }

    }

    private void checkin(final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup( this.getAbsoluteLeft(),
                                                   this.getAbsoluteTop(),
                                                   constants.CheckInChanges() );
        pop.setCommand( new Command() {
            public void execute() {
                String comment = pop.getCheckinComment();
                for ( RuleViewer ruleViewer : ruleViews ) {
                    ruleViewer.checkInCommand.doCheckin( comment );
                    if ( closeAfter ) close();
                }
            }
        } );
        pop.show();

    }

    public void close() {
        closeCommand.execute();
    }

    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    public void makeDirty() {
        // TODO Auto-generated method stub

    }

    public void resetDirty() {
        // TODO Auto-generated method stub

    }

    public void setCloseCommand(Command command) {
        closeCommand = command;
    }

}
