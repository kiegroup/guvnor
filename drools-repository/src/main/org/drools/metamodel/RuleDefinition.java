package org.drools.metamodel;

public class RuleDefinition
{
    private String ruleName;
    private String fragment;

    
    public String getFragment()
    {
        return fragment;
    }

    public RuleDefinition(String ruleName,
                          String fragment){
        super( );
        // TODO Auto-generated constructor stub
        this.ruleName = ruleName;
        this.fragment = fragment;

    }

    public void setFragment(String fragment)
    {
        this.fragment = fragment;
    }




    public String getRuleName()
    {
        return ruleName;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }
    
    public RuleDefinition() {}


    
}
