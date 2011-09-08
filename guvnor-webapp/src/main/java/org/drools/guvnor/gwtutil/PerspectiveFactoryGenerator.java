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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.drools.guvnor.client.common.DefaultContentUploadEditor;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.AbstractModuleEditor;
import org.drools.guvnor.client.perspectives.Perspective;
import org.drools.guvnor.client.perspectives.author.AuthorPerspective;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.server.util.AssetEditorConfiguration;
import org.drools.guvnor.server.util.AssetEditorConfigurationParser;
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
        composerFactory.addImport( Images.class.getCanonicalName() );
        composerFactory.addImport( ImageResource.class.getCanonicalName() );
        composerFactory.addImport( RuleAsset.class.getCanonicalName() );
        composerFactory.addImport( RuleViewer.class.getCanonicalName() );
        composerFactory.addImport( DefaultContentUploadEditor.class.getCanonicalName() );
        composerFactory.addImport( Widget.class.getCanonicalName() );
        composerFactory.addImport( GWT.class.getCanonicalName() );
        composerFactory.addImport( ClientFactory.class.getCanonicalName() );
        composerFactory.addImport( EventBus.class.getCanonicalName() );
        composerFactory.addImport( PackageConfigData.class.getCanonicalName() );
        composerFactory.addImport( Command.class.getCanonicalName() );
        composerFactory.addImport( AbstractModuleEditor.class.getCanonicalName() );
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
            sourceWriter.commit( logger );
        }
        return implPackageName + "." + implTypeName;
    }

    private void generateAttributes( SourceWriter sourceWriter ) {
        sourceWriter.indent();
        sourceWriter.println( "private static Images images = GWT.create(Images.class);" );
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
        sourceWriter.println( "String[] formats = new String[] {\"author\", \"runtime\", \"soaservice\"};" );
        //sourceWriter.println( "String[] formats = new String[] {\"author\", \"runtime\"};" );
        
        sourceWriter.println( "return formats;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }
    
    private void generateGetModuleEditorMethod( SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors ) {
        sourceWriter.println( "public AbstractModuleEditor getModuleEditor(PackageConfigData module, ClientFactory clientFactory, EventBus eventBus, boolean isHistoryReadOnly, Command refreshCommand) {" );
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
    
    //TODO: Generate from configuration file. 
    private void generateGetPerspectiveMethod( SourceWriter sourceWriter, Map<String, List<ModuleEditorConfiguration>> registeredEditors ) {
        sourceWriter.println( "public Perspective getPerspective(String perspectiveType) {" );
        sourceWriter.indent();
        sourceWriter.println( "if(\"author\".equals(perspectiveType)) {");
        sourceWriter.indent();
        sourceWriter.println( "return new org.drools.guvnor.client.perspectives.author.AuthorPerspective();");
        sourceWriter.outdent();
        sourceWriter.println( "}");
        
        sourceWriter.println( "if(\"soaservice\".equals(perspectiveType)) {");
        sourceWriter.indent();
        sourceWriter.println( "return new org.drools.guvnor.client.perspectives.soa.SOAPerspective();");
        sourceWriter.outdent();
        sourceWriter.println( "}");

        sourceWriter.println( "if(\"runtime\".equals(perspectiveType)) {");
        sourceWriter.indent();        
        sourceWriter.println( "return new org.drools.guvnor.client.perspectives.runtime.RunTimePerspective();");
        sourceWriter.outdent();
        sourceWriter.println( "}");

        sourceWriter.println( "return null;" );
        sourceWriter.outdent();
        sourceWriter.println( "}" );
    }

    public static Map<String, List<ModuleEditorConfiguration>> loadModuleEditorMetaData() {
        Map<String, List<ModuleEditorConfiguration>> moduleEditorConfigurations = new HashMap<String, List<ModuleEditorConfiguration>>();
        String[] registeredPerspectiveTypes = getRegisteredPerspectiveTypes();
        for(String perspectiveType : registeredPerspectiveTypes) {
            Perspective p = getPerspective(perspectiveType);
            InputStream in = p.getClass().getResourceAsStream("perspective.xml");
            //REVISIT: can a perspective have no perspective configuration file, eg, the runtime perspective?
            if(in != null) {
                PerspectiveConfigurationParser parser = new PerspectiveConfigurationParser(in);
                List<ModuleEditorConfiguration> moduleEditors = parser.getModuleEditors();
                moduleEditorConfigurations.put(perspectiveType, moduleEditors);
            }
        }
        
    	return moduleEditorConfigurations;
    }
    
    private static String[] getRegisteredPerspectiveTypes() {
        return new String[] {"author", "runtime", "soaservice"};                          
    }

    private static Perspective getPerspective(String perspectiveType) {
        if ("author".equals(perspectiveType)) {
            return new org.drools.guvnor.client.perspectives.author.AuthorPerspective();
        }
        if ("soaservice".equals(perspectiveType)) {
            return new org.drools.guvnor.client.perspectives.soa.SOAPerspective();
        }
        if ("runtime".equals(perspectiveType)) {
            return new org.drools.guvnor.client.perspectives.runtime.RunTimePerspective();
        }
        return null;
    }
}