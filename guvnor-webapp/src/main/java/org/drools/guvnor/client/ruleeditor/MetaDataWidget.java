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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;

import java.util.Date;

/**
 * This displays the metadata for a versionable artifact. It also captures
 * edits, but it does not load or save anything itself.
 */
public class MetaDataWidget extends Composite {
    private Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private final Artifact artifact;
    private final boolean readOnly;
    private final String uuid;
    private VerticalPanel layout = new VerticalPanel();
    AssetCategoryEditor ed;
    private FormStyleLayout currentSection;
    private String currentSectionName;
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public MetaDataWidget(ClientFactory clientFactory,
                          EventBus eventBus,
                          final Artifact artifact,
                          boolean readOnly,
                          final String uuid) {
        super();

        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.uuid = uuid;
        this.artifact = artifact;
        this.readOnly = readOnly;

        layout.setWidth( "100%" );
        initWidget( layout );
        render();
    }

    private void render() {
        layout.clear();
        //layout.add( new SmallLabel( constants.Title() + ": [<b>" + data.name + "</b>]" ) );
        startSection( constants.Metadata() );
        addHeader( images.assetVersion(),
                artifact.getName(),
                null );

        loadData();
    }

    private void addHeader(ImageResource img,
                           String name,
                           Image edit) {

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( new SmallLabel( "<b>" + name + "</b>" ) );
        if ( edit != null ) hp.add( edit );
        currentSection.addAttribute( constants.Title(),
                hp );
    }

    private void loadData() {
        if ( artifact instanceof RuleAsset ) {
            addAttribute( constants.CategoriesMetaData(),
                    categories() );
        }

        addAttribute( constants.LastModified(),
                readOnlyDate( artifact.getLastModified() ) );
        addAttribute( constants.ModifiedByMetaData(),
                readOnlyText( artifact.getLastContributor() ) );
        addAttribute( constants.NoteMetaData(),
                readOnlyText( artifact.getCheckinComment() ) );

        if ( !readOnly ) {
            addAttribute( constants.CreatedOnMetaData(),
                    readOnlyDate( artifact.getDateCreated() ) );
        }

        if ( artifact instanceof RuleAsset ) {
            addAttribute( constants.CreatedByMetaData(),
                    readOnlyText( ((RuleAsset) artifact).getMetaData().getCreator() ) );

            addAttribute( constants.PackageMetaData(),
                    packageEditor( ((RuleAsset) artifact).getMetaData().getPackageName() ) );

            addAttribute( constants.IsDisabledMetaData(),
                    editableBoolean( new FieldBooleanBinding() {
                        public boolean getValue() {
                            return ((RuleAsset) artifact).getMetaData().isDisabled();
                        }

                        public void setValue(boolean val) {
                            ((RuleAsset) artifact).getMetaData().setDisabled( val );
                        }
                    },
                            constants.DisableTip() ) );
        }
        
        addAttribute( constants.FormatMetaData(),
                readOnlyText(artifact.getFormat()));
        addAttribute( "UUID:",
                readOnlyText( uuid ) );

        endSection( false );

        if ( artifact instanceof RuleAsset ) {

            final MetaData data = ((RuleAsset) artifact).getMetaData();
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
        }

        startSection(constants.VersionHistory());

        Image image = new Image(images.feed());
        image.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                Window.open(getVersionFeed(artifact), "_blank", null);

            }
        });
        addAttribute(constants.VersionFeed(), image);

        addAttribute( constants.CurrentVersionNumber(),
                getVersionNumberLabel() );

        if ( !readOnly ) {
            addRow( new VersionBrowser( clientFactory,
                    eventBus,
                    this.uuid,
                    !(artifact instanceof RuleAsset) ) );
        }

        endSection( true );
    }

    private void addRow(Widget widget) {
        this.currentSection.addRow( widget );
    }

    private void addAttribute(String string,
                              Widget widget) {
        this.currentSection.addAttribute( string,
                widget );
    }

    private void endSection(boolean collapsed) {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( currentSectionName );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( !collapsed );
        advancedDisclosure.setContent( this.currentSection );
        layout.add( advancedDisclosure );
    }

    private void startSection(String name) {
        currentSection = new FormStyleLayout();
        currentSectionName = name;
    }

    private Widget packageEditor(final String packageName) {
        if ( this.readOnly || !UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_KNOWLEDGE_BASES_VIEW ) ) {
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
                        constants.MovedFromPackage( pkg ),
                        new GenericCallback<java.lang.Void>() {
                            public void onSuccess(Void v) {
                                //Refresh wont work here. We have to close and reopen
                                //otherwise SuggestionEngine may not be initialized for
                                //the target package.
                                closeAndReopen( uuid );
                                pop.hide();
                            }

                        } );

            }

        } );

        pop.show();
    }

    private void closeAndReopen(String newAssetUUID) {
        eventBus.fireEvent( new ClosePlaceEvent( new AssetEditorPlace( uuid ) ) );
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( newAssetUUID ) );
    }

    private Widget getVersionNumberLabel() {
        if ( artifact.getVersionNumber() == 0 ) {
            return new SmallLabel( constants.NotCheckedInYet() );
        } else {
            return readOnlyText( Long.toString( artifact.getVersionNumber() ) );
        }
    }

    private Widget readOnlyDate(Date lastModifiedDate) {
        if ( lastModifiedDate == null ) {
            return null;
        } else {
            return new SmallLabel( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( lastModifiedDate ) );
        }
    }

    private Label readOnlyText(String text) {
        SmallLabel lbl = new SmallLabel( text );
        lbl.setWidth( "100%" );
        return lbl;
    }

    private Widget categories() {
        ed = new AssetCategoryEditor( ((RuleAsset) this.artifact).getMetaData(),
                this.readOnly );
        return ed;
    }

    /**
     * This binds a field, and returns a check box editor for it.
     *
     * @param bind    Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableBoolean(final FieldBooleanBinding bind,
                                   String toolTip) {
        if ( !readOnly ) {
            final CheckBox box = new CheckBox();
            box.setTitle( toolTip );
            box.setValue( bind.getValue() );
            ClickHandler listener = new ClickHandler() {
                public void onClick(ClickEvent w) {
                    boolean b = box.getValue();
                    bind.setValue( b );
                }
            };
            box.addClickHandler( listener );
            return box;
        } else {
            final CheckBox box = new CheckBox();

            box.setValue( bind.getValue() );
            box.setEnabled( false );

            return box;
        }
    }

    /**
     * This binds a field, and returns a TextBox editor for it.
     *
     * @param bind    Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableText(final FieldBinding bind,
                                String toolTip) {
        if ( !readOnly ) {
            final TextBox tbox = new TextBox();
            tbox.setTitle( toolTip );
            tbox.setText( bind.getValue() );
            tbox.setVisibleLength( 10 );
            ChangeHandler listener = new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    String txt = tbox.getText();
                    bind.setValue( txt );
                }

            };
            tbox.addChangeHandler( listener );
            return tbox;
        } else {
            return new Label( bind.getValue() );
        }
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBinding {
        void setValue(String val);

        String getValue();
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBooleanBinding {
        void setValue(boolean val);

        boolean getValue();
    }

    /**
     * Return the data if it is to be saved.
     */
    public Artifact getData() {
        return artifact;
    }

    public void refresh() {
        render();
    }

    static String getVersionFeed(Artifact artifact) {
        if ( artifact instanceof PackageConfigData ) {
            String hurl = getRESTBaseURL() + "packages/" + artifact.getName() + "/versions";
            return hurl;
        } else {
            String hurl = getRESTBaseURL() + "packages/" + ((RuleAsset) artifact).getMetaData().getPackageName()
                    + "/assets/" + artifact.getName() + "/versions";
            return hurl;
        }
    }

    static String getRESTBaseURL() {
        String url = GWT.getModuleBaseURL();
        return url.replaceFirst( "org.drools.guvnor.Guvnor",
                "rest" );
    }

}
