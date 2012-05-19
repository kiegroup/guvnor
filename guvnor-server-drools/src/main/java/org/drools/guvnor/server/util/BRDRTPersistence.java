/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.util;

import java.io.ByteArrayInputStream;

import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.templates.InterpolationVariable;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;
import org.drools.ide.common.server.util.BRDRLPersistence;
import org.drools.ide.common.server.util.BRLPersistence;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class persists a {@link TemplateModel} to DRL template
 */
public class BRDRTPersistence extends BRDRLPersistence {
    private static final Logger log = LoggerFactory.getLogger(BRDRTPersistence.class);
    private static final BRLPersistence INSTANCE = new BRDRTPersistence();
    private static final String PACKAGE_DECLARATION = "\npackage __template_dummy_package__\n";

    private BRDRTPersistence() {
        super();
    }

    public static BRLPersistence getInstance() {
        return INSTANCE;
    }

    @Override
    public String marshal(RuleModel model) {

        String ruleTemplate = super.marshalRule(model);
        log.debug("ruleTemplate:\n{}",
                ruleTemplate);

        DataProvider dataProvider = chooseDataProvider(model);
        DataProviderCompiler tplCompiler = new DataProviderCompiler();
        String generatedDRl = tplCompiler.compile(dataProvider,
                new ByteArrayInputStream(ruleTemplate.getBytes())).substring(PACKAGE_DECLARATION.length()).trim();
        log.debug("generated drl:\n{}",
                generatedDRl);
        return generatedDRl;
    }

    private DataProvider chooseDataProvider(RuleModel model) {
        DataProvider dataProvider;
        TemplateModel tplModel = (TemplateModel) model;
        if (tplModel.getRowsCount() > 0) {
            dataProvider = new ArrayDataProvider(tplModel.getTableAsArray());
        } else {
            dataProvider = new ArrayDataProvider(generateEmptyIterator(tplModel));
        }
        return dataProvider;
    }

    private String[][] generateEmptyIterator(TemplateModel templateModel) {
        String[][] rows = new String[1][];

        InterpolationVariable[] interpolationVariables = templateModel.getInterpolationVariablesList();
        if (interpolationVariables == null || interpolationVariables.length == 0) {
            rows[0] = new String[]{""};
        } else {
            rows[0] = new String[interpolationVariables.length];
            for (int i = 0; i < interpolationVariables.length; i++) {
                rows[0][i] = interpolationVariables[i].getVarName() + "_na";
            }
        }
        return rows;
    }

    @Override
    protected void marshalHeader(RuleModel model,
                                 StringBuilder buf) {
        TemplateModel templateModel = (TemplateModel) model;
        buf.append("template header\n");

        InterpolationVariable[] interpolationVariables = templateModel.getInterpolationVariablesList();
        if (interpolationVariables.length == 0) {
            buf.append("test_var").append('\n');
        } else {
            for (InterpolationVariable var : interpolationVariables) {
                buf.append(var.getVarName()).append('\n');
            }
        }
        buf.append(PACKAGE_DECLARATION).append("\ntemplate \"").append(super.marshalRuleName(templateModel)).append("\"\n\n");
        super.marshalHeader(model,
                buf);
    }

    @Override
    protected String marshalRuleName(RuleModel model) {
        return super.marshalRuleName(model) + "_@{row.rowNumber}";
    }

    @Override
    protected void marshalFooter(StringBuilder buf) {
        super.marshalFooter(buf);
        buf.append("\nend template");
    }

}
