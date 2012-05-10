package org.drools.guvnor.client.editor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.DatePickerTextBox;
import org.drools.guvnor.client.common.DecoratedDisclosurePanel;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.shared.MetaDataQuery;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.gwtplatform.mvp.client.ViewImpl;


public class QueryView extends ViewImpl implements QueryPresenter.MyView {
    private static Images          images    = (Images) GWT.create( Images.class );
    private Constants              constants = ((Constants) GWT.create( Constants.class ));
  

    private VerticalPanel       layout;
    private Button textSearchButton = new Button();
    final TextBox textSearchTextBox = new TextBox();  
    
    public QueryView() {
        //render();
        // setWidth( "100%" );
    }

    @Override
    public Widget asWidget() {
        //return layout;
        return new Label("sssss");
    }

    private void render() {
        layout = new VerticalPanel();
        doQuickFind();
        doTextSearch();
        doMetaSearch();
        layout.setWidth( "100%" );
/*        initWidget( layout );
        setWidth( "100%" );*/
    }

    private void doQuickFind() {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( constants.NameSearch() );
        advancedDisclosure.ensureDebugId( "cwDisclosurePanel" );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setContent( new QuickFindWidget() );
        advancedDisclosure.setOpen( true );

        layout.add( advancedDisclosure );
    }

    private void doTextSearch() {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( constants.TextSearch() );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( true );

        VerticalPanel container = new VerticalPanel();
        VerticalPanel criteria = new VerticalPanel();

        FormStyleLayout ts = new FormStyleLayout();
        ts.addAttribute( constants.SearchFor(),
                         textSearchTextBox );

        final CheckBox archiveBox = new CheckBox();
        archiveBox.setValue( false );
        ts.addAttribute( constants.IncludeArchivedAssetsInResults(),
                         archiveBox );

        textSearchButton.setText( constants.Search1() );
        ts.addAttribute( "",
                         textSearchButton );
        ts.setWidth( "100%" );

        final SimplePanel resultsP = new SimplePanel();
        final ClickHandler cl = new ClickHandler() {

            public void onClick(ClickEvent arg0) {
/*                if ( tx.getText().equals( "" ) ) {
                    Window.alert( constants.PleaseEnterSomeSearchText() );
                    return;
                }
                resultsP.clear();
                QueryPagedTable table = new QueryPagedTable(
                                                             tx.getText(),
                                                             archiveBox.getValue(),
                                                             clientFactory );
                resultsP.add( table );*/
            }

        };

        textSearchButton.addClickHandler( cl );
        textSearchTextBox.addKeyPressHandler( new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if ( event.getCharCode() == KeyCodes.KEY_ENTER ) {
                    cl.onClick( null );
                }
            }
        } );

        criteria.add( ts );
        container.add( criteria );
        container.add( resultsP );
        advancedDisclosure.setContent( container );
        layout.add( advancedDisclosure );
    }

    private void doMetaSearch() {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( constants.AttributeSearch() );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( true );

        VerticalPanel container = new VerticalPanel();
        VerticalPanel criteria = new VerticalPanel();

        final Map<String, MetaDataQuery> atts = new HashMap<String, MetaDataQuery>() {
            private static final long serialVersionUID = 510l;

            {
                put( constants.CreatedBy(),
                        new MetaDataQuery( "drools:creator" ) ); // NON-NLS
                put( constants.Format1(),
                        new MetaDataQuery( "drools:format" ) ); // NON-NLS
                put( constants.Subject(),
                        new MetaDataQuery( "drools:subject" ) ); // NON-NLS
                put( constants.Type1(),
                        new MetaDataQuery( "drools:type" ) ); // NON-NLS
                put( constants.ExternalLink(),
                        new MetaDataQuery( "drools:relation" ) ); // NON-NLS
                put( constants.Source(),
                        new MetaDataQuery( "drools:source" ) ); // NON-NLS
                put( constants.Description1(),
                        new MetaDataQuery( "drools:description" ) ); // NON-NLS
                put( constants.LastModifiedBy(),
                        new MetaDataQuery( "drools:lastContributor" ) ); // NON-NLS
                put( constants.CheckinComment(),
                        new MetaDataQuery( "drools:checkinComment" ) ); // NON-NLS
            }
        };

        FormStyleLayout fm = new FormStyleLayout();
        for ( String fieldName : atts.keySet() ) {
            final MetaDataQuery q = (MetaDataQuery) atts.get( fieldName );
            final TextBox box = new TextBox();
            box.setTitle( constants.WildCardsSearchTip() );
            fm.addAttribute( fieldName
                                     + ":",
                             box );
            box.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent arg0) {
                    q.valueList = box.getText();
                }
            } );
        }

        HorizontalPanel created = new HorizontalPanel();
        created.add( new SmallLabel( constants.AfterColon() ) );
        final DatePickerTextBox createdAfter = new DatePickerTextBox( "" );
        created.add( createdAfter );

        created.add( new SmallLabel( "&nbsp;" ) ); // NON-NLS

        created.add( new SmallLabel( constants.BeforeColon() ) );
        final DatePickerTextBox createdBefore = new DatePickerTextBox( "" );
        created.add( createdBefore );

        fm.addAttribute( constants.DateCreated1(),
                         created );

        HorizontalPanel lastMod = new HorizontalPanel();
        lastMod.add( new SmallLabel( constants.AfterColon() ) );
        final DatePickerTextBox lastModAfter = new DatePickerTextBox( "" );
        lastMod.add( lastModAfter );

        lastMod.add( new SmallLabel( "&nbsp;" ) ); // NON-NLS

        lastMod.add( new SmallLabel( constants.BeforeColon() ) );
        final DatePickerTextBox lastModBefore = new DatePickerTextBox( "" );
        lastMod.add( lastModBefore );

        fm.addAttribute( constants.LastModified1(),
                         lastMod );

        final SimplePanel resultsP = new SimplePanel();
        Button search = new Button( constants.Search() );
        fm.addAttribute( "",
                         search );
        search.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
/*                resultsP.clear();
                try {
                    List<MetaDataQuery> metadata = new ArrayList<MetaDataQuery>();
                    metadata.addAll( atts.values() );
                    QueryPagedTable table = new QueryPagedTable( metadata,
                                                                 getDate( createdAfter ),
                                                                 getDate( createdBefore ),
                                                                 getDate( lastModAfter ),
                                                                 getDate( lastModBefore ),
                                                                 false,
                                                                 clientFactory );
                    resultsP.add( table );
                } catch ( IllegalArgumentException e ) {
                    ErrorPopup.showMessage( constants.BadDateFormatPleaseTryAgainTryTheFormatOf0(
                            ApplicationPreferences.getDroolsDateFormat() ) );
                }*/
            }

            private Date getDate(final DatePickerTextBox datePicker) {
                try {
                    return datePicker.getDate();
                } catch ( IllegalArgumentException e ) {
                    datePicker.clear();
                    throw e;
                }
            }
        } );

        criteria.add( fm );
        container.add( criteria );
        container.add( resultsP );
        advancedDisclosure.setContent( container );

        layout.add( advancedDisclosure );
    }

    public String getSearchText() {
        return textSearchTextBox.getText();
    }

    public Button getTextSearchButton() {
        return textSearchButton;
    }

}