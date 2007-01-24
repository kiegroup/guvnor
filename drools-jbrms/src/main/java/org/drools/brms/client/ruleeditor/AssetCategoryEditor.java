package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.categorynav.CategoryExplorerWidget;
import org.drools.brms.client.categorynav.CategorySelectHandler;
import org.drools.brms.client.rpc.MetaData;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a viewer/editor for categories.
 * It will show a list of categories currently applicable, and allow you to 
 * remove/add to them.
 * 
 * It is intended to work with the meta data form.
 * 
 * @author Michael Neale
 */
public class AssetCategoryEditor extends Composite {

    private MetaData data;
    private HorizontalPanel panel = new HorizontalPanel();
    private ListBox box;
    
    /**
     * @param d The meta data.
     * @param readOnly If it is to be non editable.
     * @param change This will be called when a change is made (data in MetaData will be changed).
     */
    public AssetCategoryEditor(MetaData d, boolean readOnly) {
        this.data = d;

        box = new ListBox();
        
        
        box.setVisibleItemCount( 3 );
        box.setWidth( "100%" );
        box.setMultipleSelect( false );
        loadData( box );        
        panel.add( box );
        
        if (!readOnly) {
            doActions();
        }
        
        panel.setWidth( "100%" );
        initWidget( panel );        
    }

    private void doActions() {
        VerticalPanel actions = new VerticalPanel();
        Image add = new Image("images/new_item.gif");
        add.setTitle( "Add a new category." );
        
        add.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                doOKClick();
            }            
        });
        
        Image remove = new Image("images/delete_obj.gif");
        remove.setTitle( "Remove the currently selected category." );
        remove.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                 if (box.getSelectedIndex() != -1) {
                     removeCategory(box.getItemText( box.getSelectedIndex()));
                 }
            }
        });
        
        
        actions.add( add );
        actions.add( remove );
        panel.add( actions );
    }

    protected void removeCategory(String category) {
        
        String[] newList = new String[data.categories.length - 1];
        
        for ( int i = 0, j = 0; i < data.categories.length; i++) {
            if (! data.categories[i].equals( category )) {
                newList[j] = data.categories[i];
                j++;
            } 
        }
        
        data.dirty = true;
        data.categories = newList;
        
        resetBox();
    }

    private void resetBox() {
        box.clear();
        loadData( box );
    }

    private void loadData(ListBox box) {
        for ( int i = 0; i < data.categories.length; i++ ) {
            box.addItem( data.categories[i] );
        }
    }
    
    


    /** Handles the OK click on the selector popup */
    private void doOKClick() {
//        final CategorySelector sel = new CategorySelector();
//        sel.ok.addClickListener( new ClickListener() {
//            public void onClick(Widget w) {                
//                addToCategory(sel.selectedPath);   
//                sel.hide();
//            }            
//        });
//        sel.setPopupPosition( this.getAbsoluteLeft(), this.getAbsoluteTop() );
//        sel.show();
        CategorySelector sel = new CategorySelector();
        sel.setPopupPosition( getAbsoluteLeft(), getAbsoluteTop() );
        sel.show();  
    }




    /**
     * Appy the change (selected path to be added).
     */
    public void addToCategory(String selectedPath) {

        
        //ignore already selected ones.
        for ( int i = 0; i < data.categories.length; i++ ) {
            if (data.categories[i].equals( selectedPath )) {
                return;
            }
        }
        
        String[] newList = new String[data.categories.length + 1];
        for ( int i = 0; i < data.categories.length; i++ ) {
                newList[i] = data.categories[i];
        }
        newList[data.categories.length] = selectedPath;
        
        data.categories = newList;
        data.dirty = true;
        
        resetBox();
    }





    /**
     * This is a popup that allows you to select a category to add to the asset.
     */
    class CategorySelector extends PopupPanel {
        
        public Button ok = new Button("OK");
        private CategoryExplorerWidget selector;  
        public String selectedPath;
       
        public CategorySelector() {
            super(true);
            VerticalPanel vert = new VerticalPanel();
        
            selector = new CategoryExplorerWidget(new CategorySelectHandler() {
                public void selected(String sel) {
                    selectedPath = sel;
                }
                
            }, false);
            
            this.setStyleName( "ks-popups-Popup" );
            
            vert.add( selector );
            vert.add( ok );
            
            add( vert );
            
            ok.addClickListener( new ClickListener() {
                public void onClick(Widget w) { 
                    if (selectedPath != null &&  !"".equals(selectedPath)) {
                        addToCategory(selectedPath);
                    }
                    hide();
                }            
            });
          
        }      

    }
}
