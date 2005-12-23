package org.drools.metamodel;

import java.io.Serializable;

/**
 * This is the superclass for all metamodel repository classes.
 * They are all assets. Some just have more assets then others.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class Asset implements Serializable
{
   /*
    * This is based on the <a href="http://dublincore.org/documents/dces/"> Dublin Core</a>
    * specification, plus a little bit more, and a little bit less. Not all of these fields will be used.
    */    
    public String name;
    public String creator;
    public String subject;
    public String description;
    public String publisher;
    public String contributor;
    public java.util.Date dateCreated;
    public String format;
    public String source;
    public String language;
    public String relation;
    public String coverage;
    public String rights;
    
    

}
