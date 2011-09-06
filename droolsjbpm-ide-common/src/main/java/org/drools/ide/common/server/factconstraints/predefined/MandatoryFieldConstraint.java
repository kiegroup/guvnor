/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.server.factconstraints.predefined;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.stringtemplate.StringTemplate;
import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.client.factconstraints.ValidationResult;
import org.drools.ide.common.server.factconstraints.Constraint;

public class MandatoryFieldConstraint implements Constraint, Serializable {
    private static final long serialVersionUID = 53l;
    
    public static final String NAME = "MandatoryFieldConstraint";

    private static transient String template;
    
    public MandatoryFieldConstraint(){
    
    }

    public List<String> getArgumentKeys() {
        return new ArrayList<String>();
    }

    @Deprecated
    public ValidationResult validate(Object value, ConstraintConfiguration config) {
        throw new UnsupportedOperationException("Not supported! Use getVeririferRule() instead!");
    }

    public String getVerifierRule(ConstraintConfiguration config) {
        StringTemplate ruleTemplate = new StringTemplate(this.getTemplate());

        ruleTemplate.setAttribute("uuid", UUID.randomUUID().toString());
        ruleTemplate.setAttribute("factType", config.getFactType());
        ruleTemplate.setAttribute("fieldName", config.getFieldName());
        
        return ruleTemplate.toString();
    }

    public String getConstraintName() {
        return "MandatoryFieldConstraint";
    }

    private synchronized  String getTemplate(){
        if (template == null){
            BufferedReader reader = null;
            InputStream resourceAsStream = null;
            try {
                resourceAsStream = MandatoryFieldConstraint.class.getClassLoader().getResourceAsStream("factconstraints/verifierRules/MandatoryFieldConstraint.drl");
                reader = new BufferedReader(new InputStreamReader(resourceAsStream));
                String line = null;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine())!= null){
                    builder.append(line);
                    builder.append("\n");
                }
                template = builder.toString();
            } catch (Exception ex) {
                throw new IllegalStateException("Error reading factconstraints/verifierRules/MandatoryFieldConstraint.drl", ex);
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (resourceAsStream != null) resourceAsStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(MandatoryFieldConstraint.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return template;
    }
}
