package org.drools.repository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The ruleset definition contains a grouping of rules for editing/release. The
 * workingVersionNumber drives what version of rules will be included in this
 * ruleset. Changing this number will mean that different versions of ruledefs
 * are loaded etc.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RuleSetDef extends Persistent
    implements
    Comparable {
    private static final long serialVersionUID = 608068118653708104L;

    private String            name;
    private MetaData          metaData;
    private Set               rules;
    private Set               tags;
    private long              workingVersionNumber;
    private Set               versionHistory;
    private Set               attachments;
    private Set               imports;
    private Set               applicationData;
    private Set               functions;

    public RuleSetDef(String name,
                      MetaData meta) {
        this.name = name;
        this.metaData = meta;
        this.tags = new HashSet();
        this.rules = new HashSet();
        this.attachments = new HashSet();
        this.versionHistory = new HashSet();
        this.functions = new HashSet();
        this.applicationData = new HashSet();
        this.imports = new HashSet();
        this.workingVersionNumber = 1;
    }

    /**
     * This is not for public consumption. Use the proper constructor instead.
     */
    RuleSetDef() {
    }

    public Set getVersionHistory() {
        return versionHistory;
    }

    void setVersionHistory(Set versionHistory) {
        this.versionHistory = versionHistory;
    }

    public RuleSetDef addRule(RuleDef rule) {
        return addAssetToSet( rule,
                              this.rules );
    }

    public RuleSetDef addAttachment(RuleSetAttachment attachmentFile) {
        return addAssetToSet( attachmentFile,
                              this.attachments );
    }

    public RuleSetDef addImport(ImportDef importDef) {
        return addAssetToSet( importDef,
                              this.imports );
    }

    public RuleSetDef addApplicationData(ApplicationDataDef appData) {
        return addAssetToSet( appData,
                              this.applicationData );
    }
    
    /** 
     * Removes a rule from the current ruleset. This
     * DOES NOT delete the rule, and DOES NOT effect any other versions
     * of the ruleset. 
     */
    public void removeRule(RuleDef rule) {
        //rule.setVersionNumber(-1);
        this.rules.remove(rule);
    }
    
    public void removeFunction(FunctionDef function) {
        this.functions.remove(function);
    }
    
    public void removeApplicationData(ApplicationDataDef appData) {
        this.applicationData.remove(appData);
    }
    
    public void removeImport(ImportDef imp) {
        this.imports.remove(imp);
    }
    
    public void removeAttachment(RuleSetAttachment attachment) {
        this.attachments.remove(attachment);
    }

    public RuleSetDef addFunction(FunctionDef function) {
        return addAssetToSet( function,
                              this.functions );
    }

    /**
     * This adds a versionable asset to the specified set.
     * 
     * Copy/versus linking: If the asset already has an Id, and it is a
     * different version number, then it will be copied for the set. If it has
     * the same version number, then it will be shared. Sharing is generally not
     * recommended, but can be useful.
     */
    RuleSetDef addAssetToSet(IVersionable asset,
                             Set set) {
        if ( asset.getId() == null ) {
            asset.setVersionNumber( this.workingVersionNumber );
            asset.setVersionComment( "New" );
            set.add( asset );
        }
        else if ( asset.getVersionNumber() == this.workingVersionNumber ) {
            set.add( asset );
        }
        else {
            IVersionable copy = asset.copy();
            copy.setVersionNumber( this.workingVersionNumber );
            copy.setVersionComment( "Copied for this version." );
            set.add( copy );
        }
        return this;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    /** The list of rules that are currently loaded for this ruleset */
    public Set getRules() {
        return rules;
    }

    private void setRules(Set rules) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Set getTags() {
        return tags;
    }

    private void setTags(Set tags) {
        this.tags = tags;
    }

    public RuleSetDef addTag(String tag) {
        this.tags.add( new Tag( tag ) );
        return this;
    }

    public long getWorkingVersionNumber() {
        return workingVersionNumber;
    }

    /**
     * This will only be set when loading the RuleSet from the repository. When
     * you load a ruleset, a version number is specified. This property is not
     * persistent, as multiple people could be working on different versions at
     * the same time.
     * 
     * DO NOT set this property MANUALLY !!!
     * 
     * @param workingVersionNumber
     */
    public void setWorkingVersionNumber(long workingVersionNumber) {
        this.workingVersionNumber = workingVersionNumber;
    }

    /**
     * This method increments the working version of the ruleset, creating a
     * brand new version. This records the event in the version history.
     * 
     * Typically you would call this method when you want to make a stable
     * version of a rule set (lock in all the related assets) and then move on
     * to an "editing" version. You can always switch back to a previous version
     * of a rulebase.
     * 
     * All rules and ruleset-attachments etc that are connected to this version
     * of the ruleset are cloned with the new workingVersionNumber.
     * 
     * This means that the previous state of the RuleSet is kept in tact (for
     * instance, as a release of rules). Rules can then be edited, removed and
     * so on without effecting any previous versions of rules and the ruleset.
     * 
     * Previous rules can be retrieved by changing the value of
     * workingVersionNumber.
     * 
     * Note that further to this, rules themselves will be versioned on save
     * (think of that versioning as "minor" versions, and this sort of ruleset
     * versions as major versions).
     * 
     * Ideally once a new version is created, the RuleSet should be stored and
     * then loaded fresh, which will hide the non working versions of the rules.
     * 
     */
    public void createNewVersion(String comment,
                                 String newStatus) {

        this.workingVersionNumber++;
        addNewVersionHistory( newStatus );

        createAndAddNewVersions( this.rules,
                                 comment,
                                 this.workingVersionNumber );

        createAndAddNewVersions( this.attachments,
                                 comment,
                                 this.workingVersionNumber );

        createAndAddNewVersions( this.functions,
                                 comment,
                                 this.workingVersionNumber );

        createAndAddNewVersions( this.applicationData,
                                 comment,
                                 this.workingVersionNumber );
        
        createAndAddNewVersions( this.imports,
                                 comment,
                                 this.workingVersionNumber );


    }

    private void addNewVersionHistory(String newStatus) {
        RuleSetVersionInfo newVersion = new RuleSetVersionInfo();
        newVersion.setStatus( newStatus );
        newVersion.setVersionNumber( this.workingVersionNumber );
        this.versionHistory.add( newVersion );
    }

    /**
     * This will work on any set of <code>IVersionable</code> objects. They
     * are copied, and then added to the original set (with null Ids). The
     * comment is added, as is the new version number.
     */
    private void createAndAddNewVersions(Set assets,
                                         String comment,
                                         long newVersionNumber) {
        // as the Ids are null, copied objects
        // will get a new identity, and have the new workingVersionNumber
        Set newVersions = new HashSet();
        for ( Iterator iter = assets.iterator(); iter.hasNext(); ) {
            IVersionable old = (IVersionable) iter.next();
            if ( old.getVersionNumber() == newVersionNumber - 1 ) {
                // we only want to clone rules that are for the version being
                // cloned
                IVersionable clone = (IVersionable) old.copy();
                clone.setVersionComment( comment );
                clone.setVersionNumber( newVersionNumber );
                newVersions.add( clone );
            }
        }
        assets.addAll( newVersions );
    }

    public String toString() {
        return "{ name=" + this.name + " , workingVersionNumber=" + this.workingVersionNumber + " ruleCount:" 
                        + this.rules.size() + " }";
    }

    /** The name provides the natural ordering */
    public int compareTo(Object arg) {
        if ( arg instanceof RuleSetDef ) {
            return ((RuleSetDef) arg).name.compareTo( this.name );
        }
        return 0;
    }

    public Set getAttachments() {
        return attachments;
    }

    private void setAttachments(Set attachments) {
        this.attachments = attachments;
    }

    public Set getApplicationData() {
        return applicationData;
    }

    private void setApplicationData(Set applicationData) {
        this.applicationData = applicationData;
    }

    public Set getFunctions() {
        return functions;
    }

    private void setFunctions(Set functions) {
        this.functions = functions;
    }

    public Set getImports() {
        return imports;
    }

    private void setImports(Set imports) {
        this.imports = imports;
    }

}
