package org.drools.guvnor.client.asseteditor.drools;

import org.drools.guvnor.client.asseteditor.AfterAssetEditorCheckInEvent;
import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetService;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.BuilderResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

public class OryxMultiViewEditorMenuBarCreator extends DefaultOryxMultiViewEditorMenuBarCreator {
    
    private Command closeCommand;
    private final Command beforeSaveCommand;
    private final Command afterSaveCommand;
    private AssetServiceAsync assetService = GWT.create(AssetService.class);
    private Asset[] assets; 
    /**
     * Constructor that takes 1 command as parameters for "Close" Button. 
     * @param closeCommand
     */
    public OryxMultiViewEditorMenuBarCreator(Command closeCommand, Command beforeSaveCommand, Command afterSaveCommand,Asset[] assets) {
        this.closeCommand = closeCommand;
        this.beforeSaveCommand = beforeSaveCommand;
        this.afterSaveCommand = afterSaveCommand;
        this.assets = assets;
    }

    @Override
    public MenuBar createMenuBar(final MultiViewEditor editor, final EventBus eventBus) {
    	MenuBar toolbar = new MenuBar();
        toolbar.addItem(Constants.INSTANCE.SaveAllChanges(),
                new Command() {
                    public void execute() {
                    	GenericCallback callback = new GenericCallback<BuilderResult>() {
        					public void onSuccess(BuilderResult results) {
        						
        						//RuleValidatorWrapper.showBuilderErrors(results);
        						if(results ==null ||!results.hasLines() ){
        							//before save command
        	                        Command cmd = getBeforeSaveCommand();
        	                        if (cmd != null){
        	                            cmd.execute();
        	                        }
        	                        //persist the asset
        	                        editor.checkin(false);
        	                        
        	                        //after save command
        	                        eventBus.addHandler(
        	                            AfterAssetEditorCheckInEvent.TYPE,
        	                            new AfterAssetEditorCheckInEvent.Handler() {
        	                                public void onRefreshAsset(AfterAssetEditorCheckInEvent afterAssetEditorCheckInEvent) {
        	                                    if (editor == afterAssetEditorCheckInEvent.getEditor()) {
        	                                        Command cmd = getAfterSaveCommand();
        	                                        if (cmd != null){
        	                                            cmd.execute();
        	                                        }
        	                                    }
        	                                }
        	                        });
        						} else {
                            		RuleValidatorWrapper.showBuilderErrors( results );
        						}
        						
        					}
                    	};
                    	//LoadingPopup.showMessage( Constants.INSTANCE.ValidatingItemPleaseWait() );
                    	for (Asset asset : assets) {
        					assetService.validateAsset(asset, callback);
                    	}
                    }
                });
        //toolbar.addItem(Constants.INSTANCE.Close(),this.closeCommand);
        /*
        toolbar.addItem(Constants.INSTANCE.Validate(), new Command() {
            public void execute() {
                //onSave();
                LoadingPopup.showMessage( Constants.INSTANCE.ValidatingItemPleaseWait() );
               
            }
        });
        */
        return toolbar;
    }

    @Override
    protected Command getAfterSaveCommand() {
        return this.afterSaveCommand;
    }

    @Override
    protected Command getBeforeSaveCommand() {
        return this.beforeSaveCommand;
    }
}
