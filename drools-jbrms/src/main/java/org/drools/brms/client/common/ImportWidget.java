package org.drools.brms.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This builds an ImportWidget for providing common import file features in a 
 * form layout
 * 
 * @author Fernando Meyer
 */

public class ImportWidget extends FormPanel {
    
    private HorizontalPanel panel;
    private FileUpload upload; 
    
    
    /**
     * 
     * @param field
     * @param uuid
     */
    public ImportWidget(String field, String uuid) {
        initWidgets( field, uuid );
    }
    
    /**
     * 
     * @param field
     */
    public ImportWidget (String field) {
        initWidgets( field, null );
    }
    
    /**
     * 
     * @param field
     * @param uuid
     */
    public void initWidgets(String field, String uuid) {
        panel = new HorizontalPanel();

        setAction( GWT.getModuleBaseURL() + "fileManager" );
        setEncoding( FormPanel.ENCODING_MULTIPART );
        setMethod( FormPanel.METHOD_POST );

        setWidget( panel );

        upload = new FileUpload();
        
        if ( uuid != null ) { 
            panel.add( getHiddenField(HTMLFileManagerFields.FORM_FIELD_UUID, uuid) );
        }
        upload.setName( field );

        panel.add( upload );
        
    }

    /**
     * Override the add method to insert widget on internal panel  
     */
    public void add (Widget a) {
        panel.add( a );
    }
    
    public String getFilename() {
        return upload.getFilename();
    }
    
    private TextBox getHiddenField(String name, String value) {
        TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }

    
}
