/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.ruleeditor;

import java.util.Date;

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.security.CapabilitiesManager;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This displays the metadata for a versionable asset.
 * It also captures edits, but it does not load or save anything itself.
 */
public class MetaDataWidget extends Composite {

    private Constants       constants = GWT.create( Constants.class );
    private static Images   images    = GWT.create( Images.class );

    private MetaData        data;
    private boolean         readOnly;
    private String          uuid;
    private Command         metaDataRefreshView;
    private Command         fullRefreshView;
    private VerticalPanel   layout    = new VerticalPanel();
    AssetCategoryEditor     ed;
    private FormStyleLayout currentSection;
    private String          currentSectionName;

    public MetaDataWidget(final MetaData d,
                          final boolean readOnly,
                          final String uuid,
                          final Command metaDataRefreshView,
                          final Command fullRefreshView) {

        super();

        this.uuid = uuid;
        this.data = d;
        this.readOnly = readOnly;

        layout.setWidth( "100%" );
        this.metaDataRefreshView = metaDataRefreshView;
        this.fullRefreshView = fullRefreshView;

        initWidget( layout );
        render();
    }

    public void setMetaData(MetaData data) {
        this.data = data;
    }

    private void render() {
        layout.clear();
        layout.add( new SmallLabel( Format.format( "{0}: [<b>{1}</b>]",
                constants.Title(),
                data.name ) ) );
        if ( !readOnly ) {
            Image edit = new ImageButton( images.edit(),
                                          constants.RenameThisAsset() );
            edit.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent w) {
                    showRenameAsset( w );
                }
            } );
            addHeader( images.metadata(),
                       data.name,
                       edit );
        } else {
            addHeader( images.assetVersion(),
                       data.name,
                       null );
        }

        loadData();
    }

    private void addHeader(ImageResource img,
                           String name,
                           Image edit) {
        startSection( name );

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( new SmallLabel( "<b>" + name + "</b>" ) );
        if ( edit != null ) hp.add( edit );
        currentSection.addAttribute( constants.Title(),
                                     hp );
    }

    private void loadData() {
        addAttribute( constants.CategoriesMetaData(),
                      categories() );

        addAttribute( constants.ModifiedOnMetaData(),
                      readOnlyDate( data.lastModifiedDate ) );
        addAttribute( constants.ModifiedByMetaData(),
                      readOnlyText( data.lastContributor ) );
        addAttribute( constants.NoteMetaData(),
                      readOnlyText( data.checkinComment ) );

        if ( !readOnly ) {
            addAttribute( constants.CreatedOnMetaData(),
                          readOnlyDate( data.createdDate ) );
        }
        addAttribute( constants.CreatedByMetaData(),
                      readOnlyText( data.creator ) );
        addAttribute( constants.FormatMetaData(),
                      new SmallLabel( "<b>" + data.format + "</b>" ) );

        addAttribute( constants.PackageMetaData(),
                      packageEditor( data.packageName ) );

        addAttribute( constants.IsDisabledMetaData(),
                      editableBoolean( new FieldBooleanBinding() {
                                           public boolean getValue() {
                                               return data.disabled;
                                           }

                                           public void setValue(boolean val) {
                                               data.disabled = val;
                                           }
                                       },
                                       constants.DisableTip() ) );

        addAttribute( "UUID:",
                      readOnlyText( uuid ) );

        endSection(false);

        startSection( constants.OtherMetaData() );

        addAttribute( constants.SubjectMetaData(),
                      editableText( new FieldBinding() {
                                        public String getValue() {
                                            return data.subject;
                                        }

                                        public void setValue(String val) {
                                            data.subject = val;
                                        }
                                    },
                                    constants.AShortDescriptionOfTheSubjectMatter() ) );

        addAttribute( constants.TypeMetaData(),
                      editableText( new FieldBinding() {
                                        public String getValue() {
                                            return data.type;
                                        }

                                        public void setValue(String val) {
                                            data.type = val;
                                        }

                                    },
                                    constants.TypeTip() ) );

        addAttribute( constants.ExternalLinkMetaData(),
                      editableText( new FieldBinding() {
                                        public String getValue() {
                                            return data.externalRelation;
                                        }

                                        public void setValue(String val) {
                                            data.externalRelation = val;
                                        }

                                    },
                                    constants.ExternalLinkTip() ) );

        addAttribute( constants.SourceMetaData(),
                      editableText( new FieldBinding() {
                                        public String getValue() {
                                            return data.externalSource;
                                        }

                                        public void setValue(String val) {
                                            data.externalSource = val;
                                        }

                                    },
                                    constants.SourceMetaDataTip() ) );

        endSection( true );
        startSection( constants.VersionHistory() );
        addAttribute( constants.CurrentVersionNumber(),
                      getVersionNumberLabel() );

        if ( !readOnly ) {
            addRow( new VersionBrowser( this.uuid,
                                        this.data,
                                        fullRefreshView ) );
        }

        endSection( false );
    }

    private void addRow(VersionBrowser versionBrowser) {
        this.currentSection.addRow( versionBrowser );
    }

    private void addAttribute(String string,
                              Widget editable) {
        this.currentSection.addAttribute( string,
                                          editable );
    }

    private void endSection() {
        endSection( false );
    }

    private void endSection(boolean collapsed) {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( currentSectionName );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( collapsed );
        advancedDisclosure.setContent( this.currentSection );
        layout.add( advancedDisclosure );
    }

    private void startSection(String name) {
        currentSection = new FormStyleLayout();
        currentSectionName = name;
    }

    private Widget packageEditor(final String packageName) {
        if ( this.readOnly || !CapabilitiesManager.getInstance().shouldShow( Capabilities.SHOW_PACKAGE_VIEW ) ) {
            return readOnlyText( packageName );
        } else {
            HorizontalPanel horiz = new HorizontalPanel();
            horiz.setStyleName( "metadata-Widget" ); //NON-NLS
            horiz.add( readOnlyText( packageName ) );
            Image editPackage = new ImageButton( images.edit() );
            editPackage.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent w) {
                    showEditPackage( packageName,
                                     w );
                }
            } );
            horiz.add( editPackage );
            return horiz;
        }
    }

    private void showRenameAsset(ClickEvent source) {
        final FormStylePopup pop = new FormStylePopup( images.packageLarge(),
                                                       constants.RenameThisItem() );
        final TextBox box = new TextBox();
        box.setText( data.name );
        pop.addAttribute( constants.NewNameAsset(),
                          box );
        Button ok = new Button( constants.RenameItem() );
        pop.addAttribute( "",
                          ok );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                RepositoryServiceFactory.getAssetService().renameAsset( uuid,
                                                                   box.getText(),
                                                                   new GenericCallback<java.lang.String>() {
                                                                       public void onSuccess(String data) {
                                                                           metaDataRefreshView.execute();
                                                                           Window.alert( constants.ItemHasBeenRenamed() );
                                                                           pop.hide();
                                                                       }
                                                                   } );
            }
        } );

        pop.show();
    }

    private void showEditPackage(final String pkg,
                                 ClickEvent source) {
        final FormStylePopup pop = new FormStylePopup( images.packageLarge(),
                                                       constants.MoveThisItemToAnotherPackage() );
        pop.addAttribute( constants.CurrentPackage(),
                          new Label( pkg ) );
        final RulePackageSelector sel = new RulePackageSelector();
        pop.addAttribute( constants.NewPackage(),
                          sel );
        Button ok = new Button( constants.ChangePackage() );
        pop.addAttribute( "",
                          ok );
        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent w) {
                if ( sel.getSelectedPackage().equals( pkg ) ) {
                    Window.alert( constants.YouNeedToPickADifferentPackageToMoveThisTo() );
                    return;
                }
                RepositoryServiceFactory.getAssetService().changeAssetPackage( uuid,
                                                                          sel.getSelectedPackage(),
                                                                          Format.format( constants.MovedFromPackage(),
                                                                                         pkg ),
                                                                          new GenericCallback<java.lang.Void>() {
                                                                              public void onSuccess(Void v) {
                                                                                  metaDataRefreshView.execute();
                                                                                  pop.hide();
                                                                              }

                                                                          } );

            }

        } );

        pop.show();
    }

    private Widget getVersionNumberLabel() {
        if ( data.versionNumber == 0 ) {
            return new SmallLabel( constants.NotCheckedInYet() );
        } else {
            return readOnlyText( Long.toString( data.versionNumber ) );
        }

    }

    private Widget readOnlyDate(Date lastModifiedDate) {
        if ( lastModifiedDate == null ) {
            return null;
        } else {
            return new SmallLabel( DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(lastModifiedDate) );
        }
    }

    private Label readOnlyText(String text) {
        SmallLabel lbl = new SmallLabel( text );
        lbl.setWidth( "100%" );
        return lbl;
    }

    private Widget categories() {
        ed = new AssetCategoryEditor( this.data,
                                      this.readOnly );
        return ed;
    }

    /** This binds a field, and returns a text editor for it */
    private Widget editableText(final FieldBinding bind,
                                String toolTip) {
        if ( !readOnly ) {
            final TextBox box = new TextBox();
            box.setTitle( toolTip );
            box.setText( bind.getValue() );
            box.setVisibleLength( 10 );
            ChangeHandler listener = new ChangeHandler() {
                public void onChange(ChangeEvent w) {
                    bind.setValue( box.getText() );
                }
            };
            box.addChangeHandler( listener );
            return box;
        } else {
            return new Label( bind.getValue() );
        }
    }

    /**
     * This binds a field, and returns a check box editor for it.
     *
     * @param bind Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableBoolean(final FieldBooleanBinding bind,
                                   String toolTip) {
        if ( !readOnly ) {
            final CheckBox box = new CheckBox();
            box.setTitle( toolTip );
            box.setEnabled( bind.getValue() );
            ClickHandler listener = new ClickHandler() {
                public void onClick(ClickEvent w) {
                    boolean b = box.isEnabled();
                    bind.setValue( b );
                }
            };
            box.addClickHandler( listener );
            return box;
        } else {
            final CheckBox box = new CheckBox();

            box.setEnabled( bind.getValue() );
            box.setEnabled( false );

            return box;
        }
    }

    /** used to bind fields in the meta data DTO to the form */
    static interface FieldBinding {
        void setValue(String val);

        String getValue();
    }

    /** used to bind fields in the meta data DTO to the form */
    static interface FieldBooleanBinding {
        void setValue(boolean val);

        boolean getValue();
    }

    /**
     * Return the data if it is to be saved.
     */
    public MetaData getData() {
        return data;
    }

    public void refresh() {
        render();
    }

}
