package org.jboss.drools.guvnor.importgenerator;


/**
 * Represents an individual rule within a drl package file found in the file system
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */
public class Rule{
	private String ruleName;
	private String content;
	
	/**
	 * Std constructor used within the PackageFile parser
	 * 
	 * @param ruleName
	 * @param content
	 */
	public Rule(String ruleName, String content){
		this.ruleName=ruleName;
		this.content=content;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}