package org.drools.guvnor.client.ruleeditor;
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



import java.util.Date;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This displays the metadata for a versionable asset.
 * It also captures edits, but it does not load or save anything itself.
 * @author Michael Neale
 */
public class MetaDataWidget extends PrettyFormLayout {


    private MetaData data;
    private boolean readOnly;
    private String uuid;
    private Command refreshView;
    AssetCategoryEditor ed;

	public MetaDataWidget(final MetaData d, boolean readOnly, String uuid, Command refreshView) {

		super();
//        layout = new Form(new FormConfig() {
//        	{
//        		setWidth(250);
//        		//setHeader(d.name);
//        		setLabelWidth(75);
//        		setSurroundWithBox(true);
//        	}
//        });

        if (!readOnly) {
            Image edit = new ImageButton("images/edit.gif", "Rename this asset");
            edit.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    showRenameAsset(w);
                }
            });
            addHeader( "images/meta_data.png", d.name, edit );
        } else {
            addHeader( "images/asset_version.png", d.name, null );
        }

        this.uuid = uuid;
        this.data = d;
        this.readOnly = readOnly;
        this.refreshView = refreshView;

        loadData(d);



	}





	private void loadData(MetaData d) {
        this.data = d;
        startSection();
        addAttribute("Categories:", categories());
        endSection();

        startSection();
        addAttribute("Modified on:", readOnlyDate(data.lastModifiedDate));
        addAttribute("by:", readOnlyText(data.lastContributor));
        addAttribute("Note:", readOnlyText( data.checkinComment ));
        addAttribute("Version:", getVersionNumberLabel());

        if (!readOnly) {
            addAttribute("Created on:", readOnlyDate( data.createdDate ));
        }
        addAttribute("Created by:", readOnlyText(data.creator));
        addAttribute("Format:", new HTML( "<b>" + data.format + "</b>" ));

        endSection();

        startSection();

        addAttribute("Package:", packageEditor(data.packageName));
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

        endSection();
        startSection();

        if (!readOnly) {
            addRow( new VersionBrowser(this.uuid, this.data, refreshView) );
        }

        endSection();
    }



	private Widget packageEditor(final String packageName) {
        if (this.readOnly) {
            return readOnlyText( packageName );
        } else {
            HorizontalPanel horiz = new HorizontalPanel();
            horiz.setStyleName( "metadata-Widget" );
            horiz.add( readOnlyText( packageName ) );
            Image editPackage = new ImageButton("images/edit.gif");
            editPackage.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    showEditPackage(packageName, w);
                }
            });
            horiz.add( editPackage );
            return horiz;
        }
    }

    private void showRenameAsset(Widget source) {
        final FormStylePopup pop = new FormStylePopup("images/package_large.png", "Rename this item");
        final TextBox box = new TextBox();
        pop.addAttribute( "New name", box );
        Button ok = new Button("Rename item");
        pop.addAttribute( "", ok );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                RepositoryServiceFactory.getService().renameAsset( uuid, box.getText(), new GenericCallback() {
                    public void onSuccess(Object data) {
                        refreshView.execute();
                        Window.alert( "Item has been renamed" );
                        pop.hide();
                    }
                });
            }
        } );

        pop.show();
    }


    private void showEditPackage(final String pkg, Widget source) {
        final FormStylePopup pop = new FormStylePopup("images/package_large.png", "Move this item to another package");
        pop.addAttribute( "Current package:", new Label(pkg) );
        final RulePackageSelector sel = new RulePackageSelector();
        pop.addAttribute( "New package:", sel );
        Button ok = new Button("Change package");
        pop.addAttribute( "", ok );
        ok.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                if (sel.getSelectedPackage().equals( pkg )) {
                    Window.alert( "You need to pick a different package to move this to." );
                    return;
                }
                RepositoryServiceFactory.getService().changeAssetPackage( uuid,
                                                                          sel.getSelectedPackage(),
                                                                          "Moved from : " + pkg,
                                                                          new GenericCallback() {
                                                                              public void onSuccess(Object data) {
                                                                                  refreshView.execute();
                                                                                  pop.hide();
                                                                              }

                                                                          } );


            }

        });

        pop.show();
    }

    private Widget getVersionNumberLabel() {
        if (data.versionNumber == 0 ) {
            return new HTML("<i>Not checked in yet</i>");
        } else {
            return readOnlyText(Long.toString( data.versionNumber) );
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
        ed = new AssetCategoryEditor(this.data, this.readOnly);
        return ed;
    }


    /** This binds a field, and returns a text editor for it */
    private Widget editableText(final FieldBinding bind, String toolTip) {
        if (!readOnly) {
            final TextBox box = new TextBox();
            box.setTitle( toolTip );
            box.setText( bind.getValue() );
            box.setVisibleLength(10);
            ChangeListener listener = new ChangeListener() {
                public void onChange(Widget w) {
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