package org.drools.brms.client.ruleeditor;

import java.util.Date;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This displays the metadata for a versionable asset.
 * It also captures edits, but it does not load or save anything itself.
 * @author Michael Neale
 */
public class MetaDataWidget extends FormStyleLayout {


    private MetaData data;
    private boolean readOnly;
    private String uuid;
    private Command refreshView;
	
	public MetaDataWidget(MetaData d, boolean readOnly, String uuid, Command refreshView) {
        
        setStyleName( "editable-Surface" );
        
        if (!readOnly) {
            addHeader( "images/meta_data.png", d.name );
        } else {
            addHeader( "images/asset_version.png", d.name );
        }
        this.uuid = uuid;
        this.data = d;
        this.readOnly = readOnly;
        this.refreshView = refreshView;
        loadData(d);
	}


    private void loadData(MetaData d) {
        this.data = d;
        addAttribute("Categories:", categories());
           
        
        addAttribute("Last modified on:", readOnlyDate(data.lastModifiedDate));
        addAttribute("Last modified by:", readOnlyText(data.lastContributor));
        addAttribute("Checkin note:", readOnlyText( data.checkinComment ));
        addAttribute("Created by:", readOnlyText(data.creator));
        addAttribute("Version number:", getVersionNumberLabel());
        addAttribute("Package:", readOnlyText(data.packageName));
        addAttribute("Format:", readOnlyText( data.format ));
        
        if (!readOnly) {
            addAttribute("Created on:", readOnlyDate( data.createdDate ));
        }

        
        addRow(new HTML("<hr/>"));
        
        addAttribute("Subject:", editableText(new FieldBinding() {
            public String getValue() {
                return data.subject;
            }

            public void setValue(String val) {
                data.subject = val;                
            }            
        }, "A short description of the subject matter."));         
        
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
        
        if (!readOnly) {
            addRow( new VersionBrowser(this.uuid, this.data, refreshView) );
        }
    }


    private Widget getVersionNumberLabel() {
        if (data.versionNumber == null || "".equals(data.versionNumber )) {
            return new HTML("<i>Not checked in yet</i>");
        } else {
            return readOnlyText(data.versionNumber);    
        }
        
    }



    private Widget readOnlyDate(Date lastModifiedDate) {
        if (lastModifiedDate == null) {
            return null;
        } else {
            return new Label(lastModifiedDate.toLocaleString());
        }
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



    /** used to bind fields in the meta data DTO to the form */
    static interface FieldBinding {
        void setValue(String val);
        String getValue();
    }



    
    /**
     * Return the data if it is to be saved.
     */
    public MetaData getData() {
        return data;
    }

	
	
}
