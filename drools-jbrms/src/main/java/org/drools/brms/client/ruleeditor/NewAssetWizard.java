package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.categorynav.CategoryExplorerWidget;
import org.drools.brms.client.categorynav.CategorySelectHandler;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.common.RulePackageSelector;
import org.drools.brms.client.common.WarningPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides a popup for creating a new rule/asset from scratch.
 * reuses a few other widgets.
 */
public class NewAssetWizard extends PopupPanel {

    private FlexTable              table       = new FlexTable(); //Using this table for the form layout
    private TextBox                name        = new TextBox();
    private TextArea               description = new TextArea();
    private String                 initialCategory;
    private CategoryExplorerWidget catChooser  = new CategoryExplorerWidget( new CategorySelectHandler() {
                                                   public void selected(String selectedPath) {
                                                       initialCategory = selectedPath;
                                                   }
                                               });
    private ListBox                 formatChooser = getFormatChooser();
    
    private RulePackageSelector packageSelector = new RulePackageSelector();
    private EditItemEvent afterCreate;
    private boolean showCats;
    private String format;

   

    /** This is used when creating a new rule. */
    public NewAssetWizard(EditItemEvent afterCreate, boolean showCats, String format, String title) {
        super( true );
        this.showCats = showCats;
        this.format = format;
        
        this.afterCreate = afterCreate;
        super.setWidth( "40%" );
        table.setWidth( "100%" );
        name.setWidth( "100%" );
        
        table.setWidget( 0,
                         0,
                         new Image( "images/new_wiz.gif" ) );
        table.setWidget( 0,
                         1,
                         new HTML( "<b>" + title + "</b>" ) );        

        table.setWidget( 1,
                         0,
                         new Label("Name:") );
        table.setWidget( 1,
                         1,
                         name );
        if (showCats) {
            table.setWidget( 2, 0, new Label("Initial category:") );
            table.setWidget( 2, 1, catChooser );
        }
        
        if (format == null) {
            table.setWidget( 3, 0, new Label("Type (format) of rule:" ));
            table.setWidget( 3, 1, this.formatChooser );
        } 
        
        table.setWidget( 4, 0, new Label("Package") );
        table.setWidget( 4, 1, packageSelector );

        description.setVisibleLines( 4 );
        description.setWidth( "100%" );
        table.setWidget( 5,
                         0,
                         new Label( "Initial Description:" ) );
        table.setWidget( 5,
                         1,
                         description );

        Button ok = new Button( "OK" );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                ok();
            }

        } );

        table.setWidget( 6,
                         0,
                         ok );

        Button cancel = new Button( "Cancel" );
        cancel.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                cancel();
            }

        } );

        table.setWidget( 6,
                         1,
                         cancel );

        add( table );
        setStyleName( "ks-popups-Popup" );
    }

    private ListBox getFormatChooser() {
        
        ListBox box = new ListBox();
        
        box.addItem( "Business rule", AssetFormats.BUSINESS_RULE );
        box.addItem( "DRL file", AssetFormats.DRL );        
        box.addItem( "Technical rule", AssetFormats.TECHNICAL_RULE );
        box.addItem( "Business rule using a DSL template", AssetFormats.DSL_TEMPLATE_RULE );
        
        box.setSelectedIndex( 0 );
        
        return box;
    }

    /**
     * When OK is pressed, it will update the repository with the new rule.
     */
    void ok() {
        
        if (this.showCats && this.initialCategory == null) {            
            WarningPopup.showMessage( "You have to pick an initial category.", this.getAbsoluteLeft(), this.getAbsoluteTop() );
            return;
        } else if (this.name.getText() == null || "".equals( this.name.getText() )) {
            WarningPopup.showMessage( "Rule must have a name", this.getAbsoluteLeft(), this.getAbsoluteTop() );
            return;
        }
        
        GenericCallback cb = new GenericCallback() {
            public void onSuccess(Object result) {
                    openEditor((String) result);
                    hide();
            }
        };


        LoadingPopup.showMessage( "Please wait ..." );
        RepositoryServiceFactory.getService().createNewRule( name.getText(),
                                                          description.getText(),
                                                          initialCategory,
                                                          packageSelector.getSelectedPackage(),
                                                          getFormat(),
                                                          cb );

    }

    private String getFormat() {
        if (format != null) return format;
        return formatChooser.getValue( formatChooser.getSelectedIndex() );
    }

    /**
     * After creating the item we open it in the editor.
     * @param uuid
     */
    protected void openEditor(String uuid) {
        afterCreate.open( uuid );
    }

    void cancel() {
        hide();
    }

}
