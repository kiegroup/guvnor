package org.drools.brms.client.admin;

import org.drools.brms.client.categorynav.CategoryEditor;
import org.drools.brms.client.categorynav.CategoryExplorerWidget;
import org.drools.brms.client.categorynav.CategorySelectHandler;
import org.drools.brms.client.common.FormStyleLayout;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This controls category administration.
 * @author Michael Neale
 */
public class CategoryManager extends Composite {

    public VerticalPanel layout = new VerticalPanel();
    public String selectedPath;
    private CategoryExplorerWidget explorer;
    
    public CategoryManager() {
        
        FormStyleLayout form = new FormStyleLayout("images/edit_category.gif", "Edit categories");
        form.addAttribute( "", new HTML("<i>Categories aid in managing large numbers of rules/assets. A shallow hierarchy is recommented.</i>") );

        explorer = new CategoryExplorerWidget(new CategorySelectHandler() {
            public void selected(String sel) {
                selectedPath = sel;
            }             
         });

        
        SimplePanel editable = new SimplePanel();
        editable.setStyleName( "metadata-Widget" );
        editable.add( explorer );
        form.addRow( new HTML("<hr/>") );
        form.addAttribute( "Current categories:", editable );
        
        Image refresh = new Image( "images/refresh.gif" );
        refresh.setTitle( "Refresh categories" );
        refresh.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                explorer.refresh();
            }
        } );
        form.addAttribute( "Refresh view:", refresh );
        form.addRow( new HTML("<hr/>") );
        
        Image newCat = new Image( "images/new.gif" );
        newCat.setTitle( "Create a new category" );
        newCat.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                CategoryEditor newCat = new CategoryEditor( selectedPath );
                newCat.setPopupPosition( w.getAbsoluteLeft(),
                                         w.getAbsoluteTop() - 400 );
                newCat.show();
            }
        } );
        
        form.addAttribute( "Create a new category:", newCat );
         
        initWidget( form );
    }
    
    

    
}
