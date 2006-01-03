package org.drools.repository;

import java.io.Serializable;

/**
 * This represents a users tag for a rule, ruleset.
 * This aids with classification of rules in an ad-hoc fashion.
 * 
 * A tag it its own entity as tags should be shared as much as possible.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class Tag implements Serializable {

    private String tag;
    private Long id;
    
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Tag(String tag) {
        this.tag = tag;
    }
    
    private Tag() {}

    public String getTag(){
        return tag;
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public String toString(){
        return tag;
    }

    public boolean equals(Object arg0){
        return tag.equals( arg0 );
    }

    public int hashCode(){
        return tag.hashCode();
    }
    
    
    
    
}
