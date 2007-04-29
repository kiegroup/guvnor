package org.drools.brms.client.ruleeditor;

import java.util.Map;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.modeldriven.ui.RuleModeller;
import org.drools.brms.client.packages.ModelAttachmentFileWidget;
import org.drools.brms.client.packages.SuggestionCompletionCache;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This launches the appropriate editor for the asset type.
 * This uses the format attribute to determine the appropriate editor, and
 * ALSO to unpackage the content payload from the generic asset RPC object.
 * 
 * NOTE: when adding new editors for asset types, this will also need to be enhanced to load 
 * it up/unpackage it correctly for the editor.
 * The editors will make changes to the rpc objects in place, and when checking in the whole RPC 
 * objects will be sent back to the server.
 * 
 * @author Michael Neale
 */
public class EditorLauncher {

    /**
     * This will return the appropriate viewer for the asset.
     */
    public static Widget getEditorViewer(RuleAsset asset,
                                         RuleViewer viewer) {
        //depending on the format, load the appropriate editor
        if ( asset.metaData.format.equals( AssetFormats.BUSINESS_RULE ) ) {
            return new RuleModeller( asset  );
        } else if ( asset.metaData.format.equals( AssetFormats.DSL_TEMPLATE_RULE ) ) {
            return new DSLRuleEditor( asset );
        } else if ( asset.metaData.format.equals( AssetFormats.MODEL ) ) {
            return new ModelAttachmentFileWidget( asset );
        } else {

            return new DefaultRuleContentWidget( asset );
        }

    }


    /**
     * This will show the rule viewer. If it was previously opened, it will show that dialog instead
     * of opening it again.
     */
    public static void showLoadEditor(final Map openedViewers,
                                      final TabPanel tab,
                                      final String uuid,
                                      final boolean readonly) {

        if ( openedViewers.containsKey( uuid ) ) {
            tab.selectTab( tab.getWidgetIndex( (Widget) openedViewers.get( uuid ) ) );
            LoadingPopup.close();
            return;
        }

        RepositoryServiceFactory.getService().loadRuleAsset( uuid,
                                                             new GenericCallback() {

                                                                 public void onSuccess(Object o) {
                                                                     final RuleAsset asset = (RuleAsset) o;

                                                                     SuggestionCompletionCache cache = SuggestionCompletionCache.getInstance();
                                                                     cache.doAction( asset.metaData.packageName,
                                                                                     new Command() {
                                                                                         public void execute() {
                                                                                             openRuleViewer( openedViewers,
                                                                                                             tab,
                                                                                                             uuid,
                                                                                                             readonly,
                                                                                                             asset );
                                                                                         }

                                                                                     } );
                                                                 }

                                                             } );

    }

    /**
     * This will actually show the viewer once everything is loaded and ready.
     * @param openedViewers
     * @param tab
     * @param uuid
     * @param readonly
     * @param asset
     */
    private static void openRuleViewer(final Map openedViewers,
                                       final TabPanel tab,
                                       final String uuid,
                                       final boolean readonly,
                                       RuleAsset asset) {
        final RuleViewer view = new RuleViewer( asset,
                                                readonly );

        String displayName = asset.metaData.name;
        if ( displayName.length() > 10 ) {
            displayName = displayName.substring( 0,
                                                 7 ) + "...";
        }
        String icon = "rule_asset.gif";
        if ( asset.metaData.format.equals( AssetFormats.DRL ) ) {
            icon = "technical_rule_assets.gif";
        } else if ( asset.metaData.format.equals( AssetFormats.DSL ) ) {
            icon = "dsl.gif";
        } else if ( asset.metaData.format.equals( AssetFormats.FUNCTION ) ) {
            icon = "function_assets.gif";
        } else if ( asset.metaData.format.equals( AssetFormats.MODEL ) ) {
            icon = "model_asset.gif";
        }
        tab.add( view,
                 "<img src='images/" + icon + "'>" + displayName,
                 true );

        openedViewers.put( uuid,
                           view );

        view.setCloseCommand( new Command() {
            public void execute() {
                tab.remove( tab.getWidgetIndex( view ) );
                tab.selectTab( 0 );
                openedViewers.remove( uuid );

            }
        } );
        tab.selectTab( tab.getWidgetIndex( view ) );
    }

}
