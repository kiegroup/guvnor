package org.jboss.drools.guvnor.importgenerator;

/**
 * Constants specifying the template files for generating the output
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 *
 */
public interface Constants {
	static final String TEMPLATES_FOLDER = "templates";
	static final String TEMPLATES_PARENT = "template_parent.xml";
	static final String TEMPLATES_PACKAGE = "template_package.xml";
	static final String TEMPLATES_SNAPSHOT = "template_snapshot.xml";
	static final String TEMPLATES_RULE = "template_rule_{0}.xml";
	static final String TEMPLATES_SNAPSHOT_RULE = "template_snapshot_rule_{0}.xml";
	static final String DEFAULT_CREATOR="generated";
	
  static final String TEMPLATES_KAGENT_PARENT_INIT="template_change_set_parent.xml";
  static final String TEMPLATES_KAGENT_CHILD_INIT="template_change_set_child.xml";

}
