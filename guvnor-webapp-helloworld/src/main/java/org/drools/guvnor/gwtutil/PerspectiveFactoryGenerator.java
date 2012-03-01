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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import org.drools.guvnor.client.asseteditor.DefaultContentUploadEditor;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.common.StackItemHeader;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.AbstractModuleEditor;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.util.Util;
import org.drools.guvnor.server.util.ModuleEditorConfiguration;
import org.drools.guvnor.server.util.PerspectiveConfigurationParser;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This generates {@link PerspectiveFactory} class during GWT compile time as we can not use
 * Java reflection on GWT client side.
 */
public class PerspectiveFactoryGenerator extends Generator {
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
        composerFactory.addImport( Module.class.getCanonicalName() );
        composerFactory.addImport( Command.class.getCanonicalName() );
        composerFactory.addImport( AbstractModuleEditor.class.getCanonicalName() );
        composerFactory.addImport( StackItemHeaderViewImpl.class.getCanonicalName() );      
        composerFactory.addImport( StackItemHeader.class.getCanonicalName() );      
        composerFactory.addImport( Util.class.getCanonicalName() );  
        composerFactory.addImport( SafeHtml.class.getCanonicalName() );  
        composerFactory.addImport( IsWidget.class.getCanonicalName() );  
        composerFactory.addImplementedInterface( objectType
                .getQualifiedSourceName() );

        PrintWriter printWriter = context.tryCreate( logger, implPackageName,
                implTypeName );
        if ( printWriter != null ) {
            SourceWriter sourceWriter = composerFactory.createSourceWriter(
                    context, printWriter );

            Map<String, List<ModuleEditorConfiguration>> registeredEditors = loadModuleEditorMetaData();
            generateAttributes( sourceWriter );
            generateGetRegisteredAssetEditorFormatsMethod( sourceWriter, registeredEditors );
            generateGetRegisteredModuleEditorFormatsMethod( sourceWriter, registeredEditors );
            generateGetRegisteredPerspectiveTypesMethod( sourceWriter, registeredEditors );
            generateGetModuleEditorMethod( sourceWriter, registeredEditors );
            generateGetPerspectiveMethod( sourceWriter, registeredEditors );
            
            generateGetModulesHeaderViewMethod( sourceWriter, registeredEditors );
            generateGetModulesTreeRootNodeHeaderMethod( sourceWriter, registeredEditors );
            generateGetModulesNewAssetMenuMethod( sourceWriter, registeredEditors );            
            generateGetModuleEditorActionToolbarMethod( sourceWriter, registeredEditors );            
            generateGetAssetEditorActionToolbarMethod( sourceWriter, registeredEditors );
  
            sourceWriter.commit( logger );
        }
        return implPackageName + "." + implTypeName;
    }

    private void generateAttributes( SourceWriter sourceWriter ) {
        sourceWriter.indent();
        sourceWriter.println( "private static ImagesCore images = GWT.create(ImagesCore.class);" );
        sourceWriter.println( "private static Constants constants = GWT.create(Constants.class);" );
    }

    private void generateGetRegisteredAssetEditorFormatsMethod( SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors ) {
        sourceWriter.println( "public String[] getRegisteredAssetEditorFormats(String moduleType) {" );
        sourceWriter.indent();
        sourceWriter.println( "String formats = \"\";" );
        
        for(List<ModuleEditorConfiguration> moduleEditors : registeredEditors.values()) {   
            for (ModuleEditorConfiguration moduleEditorConfiguration : moduleEditors) {
                sourceWriter.println("if(\"" + moduleEditorConfiguration.getFormat()
                        + "\".equals(moduleType)) {");
                sourceWriter.indent();
                sourceWriter.println( "formats = \"" + moduleEditorConfiguration.getAssetEditorFormats() + "\";");
                sourceWriter.outdent();
                sourceWriter.println( "}");
            }         
        }

        sourceWriter.println( "String[] results = formats.split(\",\");");

        sourceWriter.println( "return results;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
    
    private void generateGetRegisteredModuleEditorFormatsMethod( SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors ) {
        sourceWriter.println( "public String[] getRegisteredModuleEditorFormats(String perspectiveType) {" );
        sourceWriter.indent();
        
        for(String perspectiveType : registeredEditors.keySet()) {
            List<ModuleEditorConfiguration> moduleEditors = registeredEditors.get(perspectiveType);
            sourceWriter.println( "if(\"" + perspectiveType + "\".equals(perspectiveType)) {");
            sourceWriter.indent();
            sourceWriter.println( "String[] formats = new String[] {" );
            int i = 0;
            for (ModuleEditorConfiguration a : moduleEditors) {
                String format = a.getFormat();
                sourceWriter.print( "\"" + format + "\"" );
                if ( i < moduleEditors.size() - 1 ) {
                    sourceWriter.print( ", " );
                }
                i++;
            }
 
            sourceWriter.println( "};");
            sourceWriter.println( "return formats;" );
            sourceWriter.outdent();
            sourceWriter.println( "}" );           
        }
        
        sourceWriter.println( "return null;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
    
    //TODO: Generate from configuration file. 
    private void generateGetRegisteredPerspectiveTypesMethod( SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors ) {
        sourceWriter.println( "public String[] getRegisteredPerspectiveTypes() {" );
        sourceWriter.indent();
        sourceWriter.println( "String[] formats = new String[] {\"helloworld\"};" );
        
        sourceWriter.println( "return formats;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
    
    private void generateGetModuleEditorMethod( SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors ) {
        sourceWriter.println( "public AbstractModuleEditor getModuleEditor(Module module, ClientFactory clientFactory, EventBus eventBus, boolean isHistoryReadOnly, Command refreshCommand) {" );
        sourceWriter.indent();

        for (List<ModuleEditorConfiguration> moduleEditorConfigurations : registeredEditors.values()) {
            for (ModuleEditorConfiguration moduleEditorConfiguration : moduleEditorConfigurations) {
                String format = moduleEditorConfiguration.getFormat();
                String editorClassName = moduleEditorConfiguration.getEditorClass();
                sourceWriter.println("if(module.getFormat().equals(\"" + format + "\")) {");
                sourceWriter.indent();
                sourceWriter.println("return new "
                                + editorClassName
                                + "(module, clientFactory, eventBus, isHistoryReadOnly, refreshCommand);");
                sourceWriter.outdent();
                sourceWriter.println("}");
            }
        }
        sourceWriter.println("return null;");
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
    
    //TODO: Generate from perspective.xml 
    private void generateGetPerspectiveMethod( SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors ) {
        sourceWriter.println( "public Workspace getPerspective(String perspectiveType) {" );
//        sourceWriter.indent();
//        sourceWriter.println( "if(\"author\".equals(perspectiveType)) {");
//        sourceWriter.indent();
//        sourceWriter.println( "return new org.drools.guvnor.client.perspective.author.AuthorPerspective();");
//        sourceWriter.outdent();
//        sourceWriter.println( "}");
//
//        sourceWriter.println( "if(\"runtime\".equals(perspectiveType)) {");
//        sourceWriter.indent();
//        sourceWriter.println( "return new org.drools.guvnor.client.perspective.runtime.RunTimePerspective();");
//        sourceWriter.outdent();
//        sourceWriter.println( "}");

        sourceWriter.println( "return null;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
    
    //TODO: Generate from perspective.xml 
    private void generateGetModulesHeaderViewMethod(SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors) {
        sourceWriter.println( "public IsWidget  getModulesHeaderView(String perspectiveType) {" );
//        sourceWriter.indent();
//        sourceWriter.println( "String title;");
//        sourceWriter.println( "ImageResource image;");
//        sourceWriter.println( "if(\"author\".equals(perspectiveType)) {");
//        sourceWriter.indent();
//        sourceWriter.println( "title = constants.KnowledgeBases();");
//        sourceWriter.println( "image = images.packages();");
//        sourceWriter.outdent();
//        sourceWriter.println( "}");
//
//        sourceWriter.println( "title = constants.KnowledgeBases();");
//        sourceWriter.println( "image = images.packages();");

//        sourceWriter.println( "StackItemHeaderViewImpl view = new StackItemHeaderViewImpl();");
//        sourceWriter.println( "StackItemHeader header = new StackItemHeader( view );");
//        sourceWriter.println( "header.setName( title );");
//        sourceWriter.println( "header.setImageResource( image );");
//        sourceWriter.println( "return view;" );
        sourceWriter.println( "return null;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );   	
    }
    
    //TODO: Generate from perspective.xml 
    private void generateGetModulesTreeRootNodeHeaderMethod(SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors) {
        sourceWriter.println( "public SafeHtml getModulesTreeRootNodeHeader(String perspectiveType) {" );
        sourceWriter.indent();
//        sourceWriter.println( "String title;");
//        sourceWriter.println( "ImageResource image;");
//        sourceWriter.println( "if(\"author\".equals(perspectiveType)) {");
//        sourceWriter.indent();
//        sourceWriter.println( "title = constants.Packages();");
//        sourceWriter.println( "image = images.packages();");
//        sourceWriter.outdent();
//        sourceWriter.println( "}");
//
//        sourceWriter.println( "title = constants.Packages();");
//        sourceWriter.println( "image = images.packages();");

//        sourceWriter.println( "return Util.getHeader( image, title );" );
        sourceWriter.println( "return null" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );       
    }
    
    //TODO: Generate from perspective.xml 
    private void generateGetModulesNewAssetMenuMethod(SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors) {
        sourceWriter.println( "public Widget getModulesNewAssetMenu(String perspectiveType, ClientFactory clientFactory, EventBus eventBus) {" );
        sourceWriter.indent();
    
        sourceWriter.println( "if(\"author\".equals(perspectiveType)) {");
        sourceWriter.indent();
        sourceWriter.println( "return (new org.drools.guvnor.client.asseteditor.drools.PackagesNewAssetMenu( clientFactory, eventBus )).asWidget();");
        sourceWriter.outdent();
        sourceWriter.println( "}");

        sourceWriter.println( "return (new org.drools.guvnor.client.asseteditor.drools.PackagesNewAssetMenu( clientFactory, eventBus )).asWidget();");
        sourceWriter.println( "return (new org.drools.guvnor.client.asseteditor.drools.PackagesNewAssetMenu( clientFactory, eventBus )).asWidget();");

        sourceWriter.outdent();
        sourceWriter.println( "}" );           
    }
    
    //TODO: Generate from perspective.xml 
    private void generateGetModuleEditorActionToolbarMethod(SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors) {
        sourceWriter.println( "public Widget getModuleEditorActionToolbar(Module data,  ClientFactory clientFactory, EventBus eventBus, boolean readOnly, Command refreshCommand) {" );
//        sourceWriter.indent();
//
//        sourceWriter.println( "if(\"package\".equals(data.getFormat())) {");
//        sourceWriter.indent();
//        sourceWriter.println( "return new org.drools.guvnor.client.widgets.drools.toolbar.PackageEditorActionToolbar(data,  clientFactory, eventBus, readOnly, refreshCommand);");
//        sourceWriter.outdent();
//        sourceWriter.println( "}");
//
//        sourceWriter.println( "return new org.drools.guvnor.client.widgets.drools.toolbar.PackageEditorActionToolbar(data,  clientFactory, eventBus, readOnly, refreshCommand);");
//
//        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
    
    //TODO: Generate from perspective.xml 
    private void generateGetAssetEditorActionToolbarMethod(SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors) {
        sourceWriter.println( "public Widget getAssetEditorActionToolbar(String perspectiveType, Asset asset, Widget editor, ClientFactory clientFactory, EventBus eventBus, boolean readOnly) {" );
//        sourceWriter.indent();
//
//        sourceWriter.println( "if(\"author\".equals(perspectiveType)) {");
//        sourceWriter.indent();
//        sourceWriter.println( "return new org.drools.guvnor.client.widgets.drools.toolbar.AssetEditorActionToolbar(asset, editor, clientFactory, eventBus, readOnly);");
//        sourceWriter.outdent();
//        sourceWriter.println( "}");
//
//
//        sourceWriter.println( "return new org.drools.guvnor.client.widgets.drools.toolbar.AssetEditorActionToolbar(asset, editor, clientFactory, eventBus, readOnly);");
//
//        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
        
    public static Map<String, List<ModuleEditorConfiguration>> loadModuleEditorMetaData() {
        Map<String, List<ModuleEditorConfiguration>> moduleEditorConfigurations = new HashMap<String, List<ModuleEditorConfiguration>>();
        String[] registeredPerspectiveTypes = getRegisteredPerspectiveTypes();
        for(String perspectiveType : registeredPerspectiveTypes) {
            try {
                Class perspectiveClass = Class.forName(getPerspectiveClassName(perspectiveType));

                InputStream in = perspectiveClass.getResourceAsStream("perspective.xml");
                // REVISIT: can a perspective have no perspective configuration file, eg, the runtime perspective?
                if (in != null) {
                    PerspectiveConfigurationParser parser = new PerspectiveConfigurationParser(in);
                    List<ModuleEditorConfiguration> moduleEditors = parser.getModuleEditors();
                    moduleEditorConfigurations.put(perspectiveType, moduleEditors);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
    	return moduleEditorConfigurations;
    }
    
    private static String[] getRegisteredPerspectiveTypes() {
        return new String[] {"author", "runtime"};                          
    }

    private static String getPerspectiveClassName(String perspectiveType) {
       return "org.drools.guvnor.client.perspective.helloworld.HelloWorldWorkspace";
    }
}