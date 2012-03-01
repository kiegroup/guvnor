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

package org.drools.guvnor.gwtutil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.util.AssetEditorConfiguration;
import org.drools.guvnor.server.util.AssetEditorConfigurationParser;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * This generates {@link AssetEditorFactory} class during GWT compile time as we can not use
 * Java reflection on GWT client side.
 */
public class AssetEditorFactoryGenerator extends Generator {
    public String generate( TreeLogger logger, GeneratorContext context,
                            String requestedClass ) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();

        JClassType objectType = typeOracle.findType( requestedClass );
        if ( objectType == null ) {
            logger.log( TreeLogger.ERROR, "Could not find type: "
                    + requestedClass );
            throw new UnableToCompleteException();
        }

        String implTypeName = objectType.getSimpleSourceName() + "Impl";
        String implPackageName = objectType.getPackage().getName();

        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
                implPackageName, implTypeName );

        composerFactory.addImport( Map.class.getCanonicalName() );
        composerFactory.addImport( List.class.getCanonicalName() );
        composerFactory.addImport( Constants.class.getCanonicalName() );
        composerFactory.addImport( ImagesCore.class.getCanonicalName() );
        composerFactory.addImport( ImageResource.class.getCanonicalName() );
        composerFactory.addImport( Asset.class.getCanonicalName() );
        composerFactory.addImport( RuleViewer.class.getCanonicalName() );
        composerFactory.addImport( DefaultContentUploadEditor.class.getCanonicalName() );
        composerFactory.addImport( Widget.class.getCanonicalName() );
        composerFactory.addImport( GWT.class.getCanonicalName() );
        composerFactory.addImport( ClientFactory.class.getCanonicalName() );
        composerFactory.addImport( EventBus.class.getCanonicalName() );
        composerFactory.addImplementedInterface( objectType
                .getQualifiedSourceName() );

        PrintWriter printWriter = context.tryCreate( logger, implPackageName,
                implTypeName );
        if ( printWriter != null ) {
            SourceWriter sourceWriter = composerFactory.createSourceWriter(
                    context, printWriter );

            List<AssetEditorConfiguration> registeredEditors = loadAssetEditorMetaData();
            generateAttributes( sourceWriter );
            generateGetRegisteredAssetEditorFormatsMethod( sourceWriter, registeredEditors );
            generateGetAssetEditorMethod( sourceWriter, registeredEditors );
            generateGetAssetEditorIcon( sourceWriter, registeredEditors );
            generateGetAssetEditorTitle( sourceWriter, registeredEditors );
            sourceWriter.commit( logger );
        }
        return implPackageName + "." + implTypeName;
    }

    private void generateAttributes( SourceWriter sourceWriter ) {
        sourceWriter.indent();
        sourceWriter.println( "private static Images images = GWT.create(Images.class);" );
        sourceWriter.println( "private static Constants constants = GWT.create(Constants.class);" );
    }

    private void generateGetRegisteredAssetEditorFormatsMethod( SourceWriter sourceWriter, List<AssetEditorConfiguration> registeredEditors ) {
        sourceWriter.println( "public String[] getRegisteredAssetEditorFormats() {" );
        sourceWriter.indent();
        sourceWriter.println( "String[] formats = new String[] {" );
        int i = 0;
        for (AssetEditorConfiguration a : registeredEditors) {
            String format = a.getFormat();
            sourceWriter.print( "\"" + format + "\"" );
            if ( i < registeredEditors.size() - 1 ) {
                sourceWriter.print( ", " );
            }
            i++;
        }

        sourceWriter.println( "};" );
        sourceWriter.println( "return formats;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }

    private void generateGetAssetEditorMethod( SourceWriter sourceWriter, List<AssetEditorConfiguration> registeredEditors ) {
        sourceWriter.println( "public Widget getAssetEditor(Asset asset, RuleViewer viewer, ClientFactory clientFactory, EventBus eventBus) {" );
        sourceWriter.indent();

        for (AssetEditorConfiguration a : registeredEditors) {
            String format = a.getFormat();
            String assetEditorClassName = a.getEditorClass();
            sourceWriter.println( "if(asset.getFormat().equals(\"" + format + "\")) {" );
            sourceWriter.indent();
            sourceWriter.println( "return new " + assetEditorClassName + "(asset, viewer, clientFactory, eventBus);" );
            sourceWriter.outdent();
            sourceWriter.println( "}" );
        }
        sourceWriter.println( "return new DefaultContentUploadEditor(asset, viewer, clientFactory, eventBus);" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }

    private void generateGetAssetEditorIcon( SourceWriter sourceWriter, List<AssetEditorConfiguration> registeredEditors ) {
        sourceWriter.println( "public ImageResource getAssetEditorIcon(String format) {" );
        sourceWriter.indent();

        for (AssetEditorConfiguration a : registeredEditors) {
            String format = a.getFormat();
            String iconName = a.getIcon();
            sourceWriter.println( "if(format.equals(\"" + format + "\")) {" );
            sourceWriter.indent();
            sourceWriter.println( "return " + iconName + ";" );
            sourceWriter.outdent();
            sourceWriter.println( "}" );
        }
        sourceWriter.println( "return images.ruleAsset();" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }

    private void generateGetAssetEditorTitle( SourceWriter sourceWriter, List<AssetEditorConfiguration> registeredEditors ) {
        sourceWriter.println( "public String getAssetEditorTitle(String format) {" );
        sourceWriter.indent();

        for (AssetEditorConfiguration a : registeredEditors) {
            String format = a.getFormat();
            String title = a.getTitle();
            sourceWriter.println( "if(format.equals(\"" + format + "\")) {" );
            sourceWriter.indent();
            sourceWriter.println( "return " + title + ";" );
            sourceWriter.outdent();
            sourceWriter.println( "}" );
        }
        sourceWriter.println( "return \"\";" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }

    public static List<AssetEditorConfiguration> loadAssetEditorMetaData() {
    	AssetEditorConfigurationParser parser = new AssetEditorConfigurationParser();
    	List<AssetEditorConfiguration> assetEditors = parser.getAssetEditors();
    	return assetEditors;
    }
}