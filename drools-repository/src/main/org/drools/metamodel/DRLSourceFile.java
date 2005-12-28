package org.drools.metamodel;

/**
 * This represents the content for a ruleset which is managed as one unit.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class DRLSourceFile 
{
    
    private String content;
    private String name;
    
    public DRLSourceFile(String content, String name) {
        this.name = name;
        this.content = content;
        
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    

}
