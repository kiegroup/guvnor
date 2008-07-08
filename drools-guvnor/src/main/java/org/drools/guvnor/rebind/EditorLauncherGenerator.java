package org.drools.guvnor.rebind;
/*
 * Copyright 2008 JBoss Inc
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


import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * EditorLauncherGenerator generates the EditorLauncher implementation in order to
 * simplify implementation of new content editors
 */
public class EditorLauncherGenerator extends Generator {

    private static final String CLASS_SUFFIX = "Impl";
    public static final String EDITORS_LIST = "guvnor-editors.properties";

    SourceWriter sourceWriter;
    List<EditorConfiguration> contentEditors;

    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        String generatedClassQualifiedName = createClass(logger, context, typeName);
        if (generatedClassQualifiedName == null) {
            throw new UnableToCompleteException();
        }
        return generatedClassQualifiedName;
    }

    private String createClass(TreeLogger logger, GeneratorContext context, String typeName) {
        try {
            TypeOracle typeOracle = context.getTypeOracle();
            JClassType originalType = typeOracle.getType(typeName);
            String packageName = originalType.getPackage().getName();
            String originalClassName = originalType.getSimpleSourceName();
            String generatedClassName = originalClassName + CLASS_SUFFIX;

            contentEditors = loadContentEditorsConfig();

            ClassSourceFileComposerFactory classFactory = new ClassSourceFileComposerFactory(packageName, generatedClassName);

            addImports(contentEditors, classFactory);

            if (sourceWriter == null) {
                sourceWriter = getSourceWriter(logger, context, packageName, generatedClassName, classFactory);
            }

            if (sourceWriter != null) {
                writeClass(logger, classFactory, originalType, sourceWriter);
            }

            return originalType.getParameterizedQualifiedSourceName() + CLASS_SUFFIX;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeClass(TreeLogger logger, ClassSourceFileComposerFactory classFactory,
                            JClassType originalType, SourceWriter sourceWriter) throws IOException {

        classFactory.addImplementedInterface(originalType.getName());
        initializeImagesMap(contentEditors, sourceWriter);
        writeGetEditorMethod(contentEditors, sourceWriter);

        sourceWriter.println(
                "public String getAssetFormatIcon(String format) {\n" +
                        "    String result = (String) TYPE_IMAGES.get( format );\n" +
                        "    if (result == null) {\n" +
                        "        return \"rule_asset.gif\";\n" +
                        "    } else {\n" +
                        "        return result;\n" +
                        "    }\n" +
                        "}"
        );

        sourceWriter.commit(logger);
    }

    private void addImports(List<EditorConfiguration> contentEditors, ClassSourceFileComposerFactory classFactory) {
        //common imports
        classFactory.addImport("java.util.HashMap");
        classFactory.addImport("java.util.Map");
        classFactory.addImport("com.google.gwt.user.client.ui.Widget");
        classFactory.addImport("org.drools.guvnor.client.rpc.RuleAsset");
        classFactory.addImport("org.drools.guvnor.client.rpc.RuleContentText");
        classFactory.addImport("org.drools.guvnor.client.common.AssetFormats");

        //collect the uniq class names from editors configuration
        Set<String> classesToImport = new HashSet<String>();
        for (EditorConfiguration editorConfiguration : contentEditors) {
            classesToImport.add(editorConfiguration.wrapper);
            classesToImport.add(editorConfiguration.widget);
        }

        for (String className : classesToImport) {
            if (!"".equals(className)) {
                classFactory.addImport(className);
            }
        }
    }

    private void writeGetEditorMethod(List<EditorConfiguration> contentEditors, SourceWriter sourceWriter) {
        //TODO: public Widget getEditorViewer(RuleAsset asset, RuleViewer viewer)

        sourceWriter.println("public Widget getEditorViewer(RuleAsset asset, RuleViewer viewer) {");
        for (EditorConfiguration editorConfiguration : contentEditors) {
            sourceWriter.println(" if ( asset.metaData.format.equals(\"" + editorConfiguration.format + "\") ){");

            boolean hasWrapper = editorConfiguration.wrapper != null && !"".equals(editorConfiguration.wrapper);
            String retWidget = "new " + editorConfiguration.widget + "(asset, viewer)";
            String retWrapped = "new " + editorConfiguration.wrapper + "(" + retWidget + ", asset)";

            sourceWriter.println("   return " + (hasWrapper ? retWrapped : retWidget) + ";");
            sourceWriter.println(" }");
        }

        sourceWriter.println("  return new DefaultContentUploadEditor( asset, viewer );");
        sourceWriter.println("}");

        /*sourceWriter.println(
                "public Widget getEditorViewer2(RuleAsset asset, RuleViewer viewer) {\n" +
                        "        if ( asset.metaData.format.equals( AssetFormats.BUSINESS_RULE ) ) {\n" +
                        "            return new RuleValidatorWrapper( new RuleModeller( asset  ), asset);\n" +
                        "        } else if ( asset.metaData.format.equals( AssetFormats.DSL_TEMPLATE_RULE ) ) {\n" +
                        "            return new RuleValidatorWrapper(new DSLRuleEditor( asset ), asset);\n" +
                        "        } else if ( asset.metaData.format.equals( AssetFormats.MODEL ) ) {\n" +
                        "            return new ModelAttachmentFileWidget( asset, viewer );\n" +
                        "        } else if (asset.metaData.format.equals( AssetFormats.DECISION_SPREADSHEET_XLS )){\n" +
                        "            return new RuleValidatorWrapper(new DecisionTableXLSWidget( asset, viewer ), asset);\n" +
                        "        } else if (asset.metaData.format.equals( AssetFormats.RULE_FLOW_RF )) {\n" +
                        "            return new RuleFlowUploadWidget(asset, viewer);\n" +
                        "        } else if (asset.metaData.format.equals( AssetFormats.DRL )) {\n" +
                        "            return new RuleValidatorWrapper(new DefaultRuleContentWidget( asset ), asset);\n" +
                        "        } else if (asset.metaData.format.equals( AssetFormats.ENUMERATION )) {\n" +
                        "            return new RuleValidatorWrapper(new DefaultRuleContentWidget( asset ), asset);\n" +
                        "        } else if (asset.metaData.format.equals(AssetFormats.TEST_SCENARIO)) {\n" +
                        "            return new ScenarioWidget(asset);\n" +
                        "        } else if (asset.metaData.format.equals(AssetFormats.DECISION_TABLE_GUIDED)) {\n" +
                        "            return new RuleValidatorWrapper(new GuidedDecisionTableWidget(asset), asset);\n" +
                        "        } else if (asset.metaData.format.equals(AssetFormats.DRL_MODEL)) {\n" +
                        "            return new RuleValidatorWrapper(new FactModelWidget(asset), asset);\n" +
                        "        } else {\n" +
                        "            return new DefaultContentUploadEditor( asset, viewer );\n" +
                        "        }\n" +
                        "}");*/
    }

    private void initializeImagesMap(List<EditorConfiguration> contentEditors, SourceWriter sourceWriter) {
        sourceWriter.println("public final Map TYPE_IMAGES = new HashMap(){");
        sourceWriter.indent();
        sourceWriter.println("{");
        sourceWriter.indent();
        for (EditorConfiguration editorConfiguration : contentEditors) {
            sourceWriter.println("put(\"" + editorConfiguration.format +
                    "\",\"" + editorConfiguration.icon + "\");");
        }
        sourceWriter.outdent();
        sourceWriter.println("}");
        sourceWriter.outdent();
        sourceWriter.println("};");
    }

    private List<EditorConfiguration> loadContentEditorsConfig() throws IOException {
        Properties editors = new Properties();
        editors.load(new FileInputStream(EDITORS_LIST));
        List<EditorConfiguration> editorConfigs = new ArrayList<EditorConfiguration>();
        for (Object o : editors.keySet()) {
            String key = (String) o;
            String value = editors.getProperty(key);
            String[] values = value.split(";");
            editorConfigs.add(new EditorConfiguration(key, values[0], values[1], values[2]));
        }
        return editorConfigs;
    }

    private SourceWriter getSourceWriter(TreeLogger logger,
                                         GeneratorContext context,
                                         String packageName,
                                         String generatedClassName,
                                         ClassSourceFileComposerFactory classFactory) {
        PrintWriter printWriter = context.tryCreate(logger, packageName, generatedClassName);
        return classFactory.createSourceWriter(context, printWriter);
    }

    class EditorConfiguration {
        String format;
        String widget;
        String wrapper;
        String icon;

        EditorConfiguration(String format, String widget, String wrapper, String icon) {
            this.format = format;
            this.widget = widget;
            this.wrapper = wrapper;
            this.icon = icon;
        }
    }

}