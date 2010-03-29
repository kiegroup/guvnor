package org.drools.factconstraints.client;

/**
 * Default implementation for constraints that creates more than one rule
 * in its {@link #getVerifierRule()} method.
 * This class contains an internal counter to avoid multiple declaration of
 * package, imports and globals.
 * The counter must be handled by subclasses.
 * @author esteban.aliverti@gmail.com
 */
public class DefaultMultiRulesConstraintImpl extends DefaultConstraintImpl {

    private int ruleCount;

    protected void resetRuleCount(){
        this.ruleCount = 0;
    }

    protected void incrementRuleCount(){
        this.ruleCount++;
    }

    @Override
    protected String getVerifierGlobalsPrefixTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierGlobalsPrefixTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierGlobalsSufixTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierGlobalsSufixTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierGlobalsTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierGlobalsTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierImportsPrefixTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierImportsPrefixTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierImportsSufixTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierImportsSufixTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierImportsTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierImportsTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierPackagePrefixTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierPackagePrefixTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierPackageSufixTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierPackageSufixTemplate();
        }
        return "";
    }

    @Override
    protected String getVerifierPackageTemplate() {
        if (ruleCount == 0) {
            return super.getVerifierPackageTemplate();
        }
        return "";
    }
}
