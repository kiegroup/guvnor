package org.kie.guvnor.globals.client.editor;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.globals.client.resources.i18n.GlobalsEditorConstants;
import org.kie.guvnor.globals.model.Global;

/**
 * The GlobalsEditorPresenter's view implementation
 */
public class GlobalsEditorView extends Composite implements GlobalsEditorPresenter.View {

    private GlobalsEditorPresenter presenter;

    private final ListDataProvider<Global> dataProvider = new ListDataProvider<Global>();
    private final CellTable<Global> table = new CellTable<Global>();
    private final VerticalPanel container = new VerticalPanel();

    private List<Global> globals = new ArrayList<Global>();

    private boolean isDirty = false;

    public GlobalsEditorView() {
        //Setup container
        container.setWidth( "100%" );

        //Setup table
        table.setStriped( true );
        table.setCondensed( true );
        table.setBordered( true );
        //table.setWidth( "100%" );
        table.setEmptyTableWidget( new Label( GlobalsEditorConstants.INSTANCE.noGlobalsDefined() ) );

        //Columns
        final TextColumn<Global> aliasColumn = new TextColumn<Global>() {

            @Override
            public String getValue( final Global global ) {
                return global.getAlias();
            }
        };

        final TextColumn<Global> classNameColumn = new TextColumn<Global>() {

            @Override
            public String getValue( final Global global ) {
                return global.getClassName();
            }
        };

        final ButtonCell deleteGlobalButton = new ButtonCell();
        deleteGlobalButton.setType( ButtonType.DANGER );
        deleteGlobalButton.setIcon( IconType.MINUS_SIGN );
        final Column<Global, String> deleteGlobalColumn = new Column<Global, String>( deleteGlobalButton ) {
            @Override
            public String getValue( final Global global ) {
                return GlobalsEditorConstants.INSTANCE.remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<Global, String>() {
            public void update( final int index,
                                final Global object,
                                final String value ) {
                isDirty = true;
                dataProvider.getList().remove( index );
            }
        } );

        table.addColumn( aliasColumn,
                         new TextHeader( GlobalsEditorConstants.INSTANCE.alias() ) );
        table.addColumn( classNameColumn,
                         new TextHeader( GlobalsEditorConstants.INSTANCE.className() ) );
        table.addColumn( deleteGlobalColumn,
                         GlobalsEditorConstants.INSTANCE.remove() );

        //Link data
        dataProvider.addDataDisplay( table );
        dataProvider.setList( globals );

        //Setup screen
        final Button addGlobal = new Button( GlobalsEditorConstants.INSTANCE.add() );
        addGlobal.setType( ButtonType.PRIMARY );
        addGlobal.setIcon( IconType.PLUS_SIGN );
        addGlobal.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                isDirty = true;
                dataProvider.getList().add( new Global( "a",
                                                        "a.b.c." ) );
            }
        } );
        container.add( addGlobal );
        container.add( table );

        initWidget( container );
    }

    @Override
    public void init( final GlobalsEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setContent( final List<Global> content,
                            final DataModelOracle oracle ) {
        this.globals = content;
        this.dataProvider.setList( globals );
        setNotDirty();
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setNotDirty() {
        isDirty = false;
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

}
