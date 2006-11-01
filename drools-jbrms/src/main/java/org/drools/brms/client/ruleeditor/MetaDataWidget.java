package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This displays the metadata for a versionable asset.
 * It also captures edits, but it does not load or save anything itself.
 * @author Michael Neale
 */
public class MetaDataWidget extends Composite {

	private FlexTable layout = new FlexTable();
    private FlexCellFormatter formatter = layout.getFlexCellFormatter();
	private int numInLayout = 0;
    private MetaData data;
    private boolean readOnly;
	
	public MetaDataWidget(String name, boolean readOnly) {
        this.readOnly = readOnly;
        initWidget( layout );
        addHeader("images/meta_data.gif", name);
	}


    public void loadData(MetaData d) {
        this.data = d;
        addAttribute("Categories:", categories());
        
        addAttribute("Subject:", editableText(new FieldBinding() {
            public String getValue() {
                return data.subject;
            }

            public void setValue(String val) {
                data.subject = val;                
            }            
        }, "A short description of the subject matter."));            
        
        addAttribute("Last modified on:", readOnlyText(data.lastModifiedDate));
        addAttribute("Last modified by:", readOnlyText(data.lastContributor));
        addAttribute("Checkin note:", readOnlyText( data.lastCheckinComment ));
        addAttribute("Created by:", readOnlyText(data.creator));
        addAttribute("Version number:", readOnlyText("" + data.versionNumber));
        addAttribute("Package:", readOnlyText(data.packageName));
        
            
        addAttribute("Type:", editableText(new FieldBinding() {
            public String getValue() {
                return data.type;
            }

            public void setValue(String val) {
                data.type = val;
            }
            
        }, "This is for classification purposes."));
        
        addAttribute("External link:", editableText(new FieldBinding() {
            public String getValue() {
                return data.externalRelation;
            }

            public void setValue(String val) {
                data.externalRelation = val;
            }
            
        }, "This is for relating the asset to an external system."));    
        
        addAttribute("Source:", editableText(new FieldBinding() {
            public String getValue() {
                return data.externalSource;
            }

            public void setValue(String val) {
                data.externalSource = val;
            }
            
        }, "A short description or code indicating the source of the rule."));
    }



    private Label readOnlyText(String text) {
        Label lbl = new Label(text);
        lbl.setWidth( "100%" );
        return lbl;
    }



    private Widget categories() {
        AssetCategoryEditor ed = new AssetCategoryEditor(this.data, this.readOnly);
        return ed;
    }


    /** This binds a field, and returns a text editor for it */
    private Widget editableText(final FieldBinding bind, String toolTip) {
        if (!readOnly) {
            final TextBox box = new TextBox();
            box.setTitle( toolTip );
            box.setText( bind.getValue() );
            ChangeListener listener = new ChangeListener() {    
                public void onChange(Widget w) {   
                    data.dirty = true;     
                    bind.setValue( box.getText() );                
                }                
            };            
            box.addChangeListener( listener );
            return box;
        } else {
            return new Label(bind.getValue());
        }
    }



    private Widget description() {
        final TextArea box = new TextArea();
        box.setText( data.description );
        box.setVisibleLines( 10 );
        box.setWidth( "100%" );
        box.setStyleName( "rule-viewer-Documentation" );
        ChangeListener listener = new ChangeListener() {

            public void onChange(Widget w) {   
                data.dirty = true;     
                data.description = box.getText();                
            }
            
        };
        
        box.addChangeListener( listener );
        return box;        
    }    



    /** used to bind fields in the meta data DTO to the form */
    static interface FieldBinding {
        void setValue(String val);
        String getValue();
    }

    /**
     * Adds a header at the top.
     */
    private void addHeader(String image, String title) {
        layout.setWidget( 0, 0, new Image(image) );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        
        Label name = new Label(title);
        name.setStyleName( "resource-name-Label" );
        
        layout.setWidget( 0, 1, name );
        numInLayout++;
    }


    /**
     * Add a widget to the "form".
     */
    private void addAttribute(String lbl,
                     Widget editor) {
        Label label = new Label(lbl);
        layout.setWidget( numInLayout, 0, label );
        formatter.setAlignment( numInLayout, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP );
        layout.setWidget( numInLayout, 1, editor );
        formatter.setAlignment( numInLayout, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        
        numInLayout++;
        
    }


    /**
     * This is used if the data is dirty, ie change pending save.
     */
    public boolean isDirty() {
        return data.dirty;
    }
    
    /**
     * Return the data if it is to be saved.
     */
    public MetaData getData() {
        return data;
    }

	
	
}
