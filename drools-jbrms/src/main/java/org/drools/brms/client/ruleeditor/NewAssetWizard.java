package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.categorynav.CategoryExplorerWidget;
import org.drools.brms.client.categorynav.CategorySelectHandler;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.common.RulePackageSelector;
import org.drools.brms.client.common.WarningPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides a popup for creating a new rule/asset from scratch.
 * reuses a few other widgets.
 */
public class NewAssetWizard extends FormStylePopup {

    private TextBox                name        = new TextBox();
    private TextArea               description = new TextArea();
    private String                 initialCategory;

    private ListBox                 formatChooser = getFormatChooser();
    
    private RulePackageSelector packageSelector = new RulePackageSelector();
    private EditItemEvent afterCreate;
    private boolean showCats;
    private String format;

   

    /** This is used when creating a new rule. */
    public NewAssetWizard(EditItemEvent afterCreate, boolean showCats, String format, String title) {
        super("images/new_wiz.gif", title);
        this.showCats = showCats;
        this.format = format;
        
        this.afterCreate = afterCreate;

        addAttribute( "Name:", name );
        
        if (showCats) {
            addAttribute("Initial category:", getCatChooser());
        }
        
        if (format == null) {
            addAttribute( "Type (format) of rule:", this.formatChooser );
        } 
        
        addAttribute("Package:", packageSelector);

        description.setVisibleLines( 4 );
        description.setWidth( "100%" );
        addAttribute("Initial description:", description);

        Button ok = new Button( "OK" );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                ok();
            }

        } );

        addAttribute( "", ok );

        setStyleName( "ks-popups-Popup" );
    }

    private Widget getCatChooser() {
       return new CategoryExplorerWidget( new CategorySelectHandler() {
            public void selected(String selectedPath) {
                initialCategory = selectedPath;
            }
        });
    }

    private ListBox getFormatChooser() {
        
        ListBox box = new ListBox();
        
        box.addItem( "Business rule", AssetFormats.BUSINESS_RULE );
        box.addItem( "DRL rule (technical rule)", AssetFormats.DRL );        
        box.addItem( "Business rule using a DSL template", AssetFormats.DSL_TEMPLATE_RULE );
        box.addItem( "Decision table (spreadsheet)", AssetFormats.DECISION_SPREADSHEET_XLS );
        
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


}
