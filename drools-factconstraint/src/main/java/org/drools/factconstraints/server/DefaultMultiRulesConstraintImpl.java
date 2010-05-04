package org.drools.factconstraints.server;

import java.util.Map;

import org.drools.factconstraints.client.ConstraintConfiguration;

/**
 * Default implementation for constraints that creates more than one rule
 * in its {@link #getVerifierRule()} method.
 * This class contains an internal counter to avoid multiple declaration of
 * package, imports and globals.
 * The counter must be handled by subclasses.
 * @author esteban.aliverti@gmail.com
 */
public abstract class DefaultMultiRulesConstraintImpl extends DefaultConstraintImpl {

	private static final long serialVersionUID = 501L;
	protected static final String RULE_COUNT = "ruleCount";
	
    protected void resetRuleCount(Map<String, Object> context){
        context.put(RULE_COUNT, 0);
    }

    protected void incrementRuleCount(Map<String, Object> context){
    	int rc = (Integer) context.get(RULE_COUNT);
        context.put(RULE_COUNT, ++rc);
    }

    protected int getRuleCount(Map<String, Object> context){
    	return (Integer) context.get(RULE_COUNT);
    }
    
    @Override
    protected String getVerifierGlobalsPrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierGlobalsPrefixTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierGlobalsSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierGlobalsSufixTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierGlobalsTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierGlobalsTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierImportsPrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierImportsPrefixTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierImportsSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierImportsSufixTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierImportsTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierImportsTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierPackagePrefixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierPackagePrefixTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierPackageSufixTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierPackageSufixTemplate(config, context);
        }
        return "";
    }

    @Override
    protected String getVerifierPackageTemplate(ConstraintConfiguration config, Map<String, Object> context) {
        if (getRuleCount(context) == 0) {
            return super.getVerifierPackageTemplate(config, context);
        }
        return "";
    }
}
