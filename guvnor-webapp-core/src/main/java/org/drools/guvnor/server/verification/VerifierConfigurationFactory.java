package org.drools.guvnor.server.verification;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.builder.ResourceType;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.server.factconstraints.factory.ConstraintsFactory;
import org.drools.io.ResourceFactory;
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierConfigurationImpl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class VerifierConfigurationFactory {

    public static VerifierConfiguration getDefaultConfigurationWithWorkingSetConstraints(RuleAsset[] workingSets) throws SerializationException {
        return addWorkingSetConstraints(
                getConstraintRulesFromWorkingSets(workingSets),
                new DefaultVerifierConfiguration());
    }

    public static VerifierConfiguration getPlainWorkingSetVerifierConfiguration(RuleAsset[] workingSets) throws SerializationException {
        return addWorkingSetConstraints(
                getConstraintRulesFromWorkingSets(workingSets),
                new VerifierConfigurationImpl());
    }


    private static VerifierConfiguration addWorkingSetConstraints(Collection<String> additionalVerifierRules,
                                                                  VerifierConfiguration configuration) {
        if (additionalVerifierRules != null) {
            for (String rule : additionalVerifierRules) {
                configuration.getVerifyingResources().put(
                        ResourceFactory.newByteArrayResource(rule.getBytes()),
                        ResourceType.DRL);
            }
        }
        return configuration;
    }

    private static List<String> getConstraintRulesFromWorkingSets(RuleAsset[] workingSets) {
        List<String> constraintRules = new LinkedList<String>();

        if (workingSets != null) {
            for (RuleAsset workingSet : workingSets) {
                WorkingSetConfigData wsConfig = (WorkingSetConfigData) workingSet.content;
                if (wsConfig.constraints != null) {
                    for (ConstraintConfiguration config : wsConfig.constraints) {
                        constraintRules.add(ConstraintsFactory.getInstance().getVerifierRule(config));
                    }
                }
            }
        }

        return constraintRules;
    }
}
