package org.drools.resource;

/**
 * The repository elements that can be referenced via URL will typically 
 * have several attributes including a unique URL, resource type, name, version
 * and of course content. This meta-data object just describes what the URL 
 * resource is. Single Rules, DRLs, functions, DSLs and spreadsheets all need
 * to be supported.
 * 
 * @author James Williams (james.williams@redhat.com)
 *
 */
public class RepositoryBean {

    private String version = "-1";
    private String name;
    private ResourceType    resourceType;
    private String resourceContent;

    public String getResourceContent() {
        return resourceContent;
    }

    public void setResourceContent(String resourceContent) {
        this.resourceContent = resourceContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getVersion() {
        return version;
    }

    public long getVersionInLong() {
        return Long.valueOf( this.version ).longValue();
    }

    public void setVersion(long version) {
        this.version = String.valueOf( version );
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
