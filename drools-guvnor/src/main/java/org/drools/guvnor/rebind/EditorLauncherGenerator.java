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



import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * EditorLauncherGenerator generates the EditorLauncher implementation in order to
 * simplify implementation of new content editors
 */
public class EditorLauncherGenerator extends Generator {

    private static final String CLASS_SUFFIX = "Gen";
    public static final String EDITORS_LIST = "guvnor-editors.properties";

    SourceWriter sourceWriter;

    /**
     * runs generation process
     *
     * @param logger
     * @param context
     * @param typeName
     * @return
     * @throws UnableToCompleteException
     */
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

            System.err.println(packageName);

            String originalClassName = originalType.getSimpleSourceName();
            String generatedClassName = originalClassName + CLASS_SUFFIX;

            if (sourceWriter == null) {
                sourceWriter = getSourceWriter(logger, context,
                        originalType, packageName, generatedClassName);
            }

            if (sourceWriter != null) {
                writeClass(logger, originalType, sourceWriter);
            }


            return originalType.getParameterizedQualifiedSourceName() + CLASS_SUFFIX;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //TODO: read a configuration file with the registered content editors and generate the correct code
    private void writeClass(TreeLogger logger, JClassType originalType, SourceWriter sourceWriter) {
        JMethod[] methods = originalType.getMethods();
        sourceWriter.indent();

        sourceWriter.println(
                "\n" +
                "    public final Map TYPE_IMAGES = new HashMap(){\n" +
                "        {\n" +
                "          put( AssetFormats.DRL, \"technical_rule_assets.gif\" );\n" +
                "          put( AssetFormats.DSL, \"dsl.gif\" );\n" +
                "          put( AssetFormats.FUNCTION, \"function_assets.gif\" );\n" +
                "          put( AssetFormats.MODEL, \"model_asset.gif\" );\n" +
                "          put( AssetFormats.DECISION_SPREADSHEET_XLS, \"spreadsheet_small.gif\" );\n" +
                "          put( AssetFormats.BUSINESS_RULE, \"business_rule.gif\" );\n" +
                "          put( AssetFormats.DSL_TEMPLATE_RULE, \"business_rule.gif\" );\n" +
                "          put( AssetFormats.RULE_FLOW_RF, \"ruleflow_small.gif\" );\n" +
                "          put( AssetFormats.TEST_SCENARIO, \"test_manager.gif\");\n" +
                "          put( AssetFormats.ENUMERATION, \"enumeration.gif\");\n" +
                "          put( AssetFormats.DECISION_TABLE_GUIDED, \"gdst.gif\");\n" +
                "        }\n" +
                "    };" +
                "\n" +
                "public Widget getEditorViewer(RuleAsset asset, RuleViewer viewer) {\n" +
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
                "}" +
                "\n" +
                "public String getAssetFormatIcon(String format) {\n" +
                "    String result = (String) TYPE_IMAGES.get( format );\n" +
                "    if (result == null) {\n" +
                "        return \"rule_asset.gif\";\n" +
                "    } else {\n" +
                "        return result;\n" +
                "    }\n" +
                "}"
                );

        /*for (int i = 0; i < methods.length; i++) {
            JMethod method = methods[i];
            JType returnType = method.getReturnType();
            JParameter[] parameters = method.getParameters();
            List ps = Arrays.asList(parameters);

            sourceWriter.println();
            sourceWriter.println("public " + returnType.getQualifiedSourceName() + " " +
                    method.getName() + "(" + ps.toString().substring(1, ps.toString().length() - 1) + ") {");
            sourceWriter.indent();
            sourceWriter.println("return null;");
            sourceWriter.println("}");
            sourceWriter.outdent();
        }*/
        sourceWriter.commit(logger);
    }

    private SourceWriter getSourceWriter(TreeLogger logger,
                                         GeneratorContext context,
                                         JClassType originalType,
                                         String packageName,
                                         String generatedClassName) {
        
        ClassSourceFileComposerFactory classFactory =
                new ClassSourceFileComposerFactory(packageName, generatedClassName);

        classFactory.addImport("org.drools.guvnor.client.common.AssetFormats");
        classFactory.addImport("org.drools.guvnor.client.common.DefaultContentUploadEditor");
        classFactory.addImport("org.drools.guvnor.client.decisiontable.DecisionTableXLSWidget");
        classFactory.addImport("org.drools.guvnor.client.decisiontable.GuidedDecisionTableWidget");
        classFactory.addImport("org.drools.guvnor.client.factmodel.FactModelWidget");
        classFactory.addImport("org.drools.guvnor.client.modeldriven.ui.RuleModeller");
        classFactory.addImport("org.drools.guvnor.client.packages.ModelAttachmentFileWidget");
        classFactory.addImport("org.drools.guvnor.client.qa.ScenarioWidget");
        classFactory.addImport("org.drools.guvnor.client.rpc.RuleAsset");
        classFactory.addImport("org.drools.guvnor.client.rpc.RuleContentText");
        classFactory.addImport("com.google.gwt.user.client.ui.Widget");
        classFactory.addImport("java.util.HashMap");
        classFactory.addImport("java.util.Map");

        classFactory.addImplementedInterface(originalType.getName());
        PrintWriter printWriter = context.tryCreate(logger, packageName, generatedClassName);
        return classFactory.createSourceWriter(context, printWriter);
    }


}