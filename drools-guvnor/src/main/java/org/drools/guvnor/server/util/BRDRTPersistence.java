package org.drools.guvnor.server.util;

import java.io.ByteArrayInputStream;

import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.dt.TemplateModel;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class persists a {@link TemplateModel} to DRL template 
 * 
 * @author baunax
 */
public class BRDRTPersistence extends BRDRLPersistence {
	private static final Logger log = LoggerFactory.getLogger(BRDRTPersistence.class);
    private static final BRLPersistence INSTANCE = new BRDRTPersistence();
    private static final String PACKAGE_DECLARATION = "\npackage __template_dummy_package__\n";

    private BRDRTPersistence() {
    	super();
    }

    public static BRLPersistence getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String marshal(RuleModel model) {
    	TemplateModel tplModel = (TemplateModel) model;
    	String ruleTemplate = super.marshalRule(model);
    	log.debug("ruleTemplate:\n{}", ruleTemplate);
    	DataProviderCompiler tplCompiler = new DataProviderCompiler();
    	DataProvider dataProvider; 
    	if (tplModel.getRowsCount() > 0) {
    		dataProvider = new ArrayDataProvider(tplModel.getTableAsArray());
    	} else {
    		dataProvider = new ArrayDataProvider(generateEmptyIterator(tplModel));
    	}
    	
    	String generatedDRl = tplCompiler.compile(dataProvider, new ByteArrayInputStream(ruleTemplate.getBytes()))
    		.substring(PACKAGE_DECLARATION.length()).trim();
    	
    	log.debug("generated drl:\n{}", generatedDRl);
		return generatedDRl;
    }
    
	private String[][] generateEmptyIterator(TemplateModel tplModel) {
		String[][] rows = new String[1][];

		String[] interpolationVariables = tplModel.getInterpolationVariablesList();
		if (interpolationVariables == null || interpolationVariables.length == 0) {
			rows[0] = new String[] { "" };
		} else {
			for (int i = 0; i < interpolationVariables.length; i++) {
				interpolationVariables[i] += "_na";
			}
			rows[0] = interpolationVariables;
		}
		return rows;
	}

	@Override
    protected void marshalHeader(RuleModel model, StringBuilder buf) {
    	TemplateModel tplModel = (TemplateModel) model;
    	buf.append("template header\n");
    	
    	String[] interpolationVariables = tplModel.getInterpolationVariablesList();
		if (interpolationVariables.length == 0) {
			buf.append("test_var").append('\n');
		} else {
			for (String var : interpolationVariables) {
				buf.append(var).append('\n');
			}
		}
		buf.append(PACKAGE_DECLARATION)
    		.append("\ntemplate \"" + super.marshalRuleName(tplModel) + "\"\n\n");
    	super.marshalHeader(model, buf);
    }
    
    @Override
    protected String marshalRuleName(RuleModel model) {
    	return super.marshalRuleName(model) + "_@{row.rowNumber}";
    }
    
    @Override
    protected void marshalFooter(StringBuilder buf) {
    	super.marshalFooter(buf);
    	buf.append("\nend template");
    }
    
}
