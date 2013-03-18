/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.examples.mortgage.client;

import javax.swing.JOptionPane;

import org.kie.internal.KnowledgeBase;
import org.kie.internal.agent.KnowledgeAgent;
import org.kie.internal.agent.KnowledgeAgentFactory;
import org.kie.api.definition.type.FactType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.Resource;

public class MortgageClientExample {

    /**
     * Entry point demonstrating use of KnowledgeAgent and changesets retrieving
     * a rule package from a running instance of Guvnor.
     * @param args
     */
    public static void main(String[] args) {

        JOptionPane.showMessageDialog(null, "This example makes a few assumptions:\n" +
                "- Start the app server on localhost on port 8080.\n" +
                "- Rename the guvnor war to guvnor.war and deploy it to the app server.\n" +
                "- Surf to Guvnor and log in.\n" +
                "- In the menu Administration, Import/Export, import /exported-repositories/mortgage-sample-repository.xml\n" +
                "- Refresh the browser, open menu Package, click Rebuild all packages.\n" +
                "Click OK when this is done.", "Preparation", JOptionPane.INFORMATION_MESSAGE);

        StatefulKnowledgeSession ksession = null;
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase();

            // Dynamic fact creation as the model was declared in the DRL
            FactType appType = kbase.getFactType("mortgages", "LoanApplication");
            Object application = appType.newInstance();
            appType.set(application, "amount", 25000);
            appType.set(application, "deposit", 1500);
            appType.set(application, "lengthYears", 20);

            FactType incomeType = kbase.getFactType("mortgages", "IncomeSource");
            Object income = incomeType.newInstance();
            incomeType.set(income, "type", "Job");
            incomeType.set(income, "amount", 65000);

            // Invoke the magic
            ksession = kbase.newStatefulKnowledgeSession();
            ksession.insert(application);
            ksession.insert(income);
            ksession.fireAllRules();

            // Voila!
            String message = "The loan application is now:\n" + application;
            System.out.println(message);
            JOptionPane.showMessageDialog(null, message, "Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    /**
     * Load KnowledgeBase using KnowledgeAgent configured with accompanying changeset xml
     * @return A KnowledgeBase
     */
    private static KnowledgeBase readKnowledgeBase() {
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("MortgageAgent");
        Resource changeset = ResourceFactory.newClassPathResource(
                "org/drools/guvnor/examples/mortgage/mortgage-changeset.xml");
        kagent.applyChangeSet(changeset);
        KnowledgeBase kbase = kagent.getKnowledgeBase();
        kagent.dispose();
        return kbase;
    }

}
