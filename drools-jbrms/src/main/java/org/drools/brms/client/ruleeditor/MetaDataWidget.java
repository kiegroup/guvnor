package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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
	
	
	public MetaDataWidget(MetaData d, boolean readOnly) {
        this.data = d;
        initWidget( layout );
        
        addHeader("images/meta_data.gif", data.name);

        addAttribute("Subject", editableText(new FieldBinding() {
            public String getValue() {
                return data.subject;
            }

            public void setValue(String val) {
                data.subject = val;                
            }            
        }));
        
        
        //addAttribute("Description", description());
        
        addAttribute("Categories", categories());
        
        addAttribute("Package", new Label(data.packageName));
        addAttribute("Last modified on:", new Label(data.lastModifiedDate));
        addAttribute("Last modified by:", new Label(data.lastContributor));
        addAttribute("Created by:", new Label(data.creator));
        addAttribute("Version number:", new Label("" + data.versionNumber));
        
        //addAttribute("Status", status());
        
        addAttribute("Type", editableText(new FieldBinding() {

            public String getValue() {
                return data.type;
            }

            public void setValue(String val) {
                data.type = val;
            }
            
        }));
        
        
        
	}

    





    private Widget categories() {
        
        ListBox box = new ListBox();
        for ( int i = 0; i < data.categories.length; i++ ) {
            String cat = data.categories[i];
            box.addItem( cat );
        }
        
        return box;
        
    }







    /** This binds a field, and returns a text editor for it */
    private Widget editableText(final FieldBinding bind) {
        final TextBox box = new TextBox();
        box.setText( bind.getValue() );
        ChangeListener listener = new ChangeListener() {

            public void onChange(Widget w) {   
                data.dirty = true;     
                bind.setValue( box.getText() );                
            }
            
        };
        
        box.addChangeListener( listener );
        return box;        
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
