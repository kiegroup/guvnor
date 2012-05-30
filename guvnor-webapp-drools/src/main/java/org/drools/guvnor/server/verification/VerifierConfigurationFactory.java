package org.drools.guvnor.server.verification;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.drools.builder.ResourceType;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.ide.common.client.factconstraints.ConstraintConfiguration;
import org.drools.ide.common.server.factconstraints.factory.ConstraintsFactory;
import org.drools.io.ResourceFactory;
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.EmptyVerifierConfiguration;
import org.drools.verifier.VerifierConfiguration;

import com.google.gwt.user.client.rpc.SerializationException;

public class VerifierConfigurationFactory {

    public static VerifierConfiguration getDefaultConfigurationWithWorkingSetConstraints(Asset[] workingSets) throws SerializationException {
        return addWorkingSetConstraints(
                getConstraintRulesFromWorkingSets(workingSets),
                new DefaultVerifierConfiguration());
    }

    public static VerifierConfiguration getPlainWorkingSetVerifierConfiguration(Asset[] workingSets) throws SerializationException {
        return addWorkingSetConstraints(
                getConstraintRulesFromWorkingSets(workingSets),
                new EmptyVerifierConfiguration());
    }
    
    public static VerifierConfiguration getPlainWorkingSetVerifierConfiguration(Set<WorkingSetConfigData> workingSets) throws SerializationException {
        return addWorkingSetConstraints(
                getConstraintRulesFromWorkingSets(workingSets),
                new EmptyVerifierConfiguration());
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

    private static List<String> getConstraintRulesFromWorkingSets(Asset[] workingSets) {
        List<String> constraintRules = new LinkedList<String>();

        if (workingSets != null) {
            for (Asset workingSet : workingSets) {
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
    
    private static List<String> getConstraintRulesFromWorkingSets(Set<WorkingSetConfigData> workingSets) {
        List<String> constraintRules = new LinkedList<String>();

        if (workingSets != null) {
            for (WorkingSetConfigData workingSet : workingSets) {
                if (workingSet.constraints != null) {
                    for (ConstraintConfiguration config : workingSet.constraints) {
                        constraintRules.add(ConstraintsFactory.getInstance().getVerifierRule(config));
                    }
                }
            }
        }

        return constraintRules;
    }
}
