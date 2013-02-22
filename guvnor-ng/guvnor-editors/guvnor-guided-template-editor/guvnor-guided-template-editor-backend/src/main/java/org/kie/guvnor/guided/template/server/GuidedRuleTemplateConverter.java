package org.kie.guvnor.guided.template.server;

import org.drools.guvnor.models.commons.backend.BaseConverter;
import org.kie.builder.impl.FormatConversionResult;
import org.kie.builder.impl.FormatConverter;
import org.kie.guvnor.guided.template.model.TemplateModel;
import org.kie.guvnor.guided.template.server.util.BRDRTPersistence;
import org.kie.guvnor.guided.template.server.util.BRDRTXMLPersistence;

public class GuidedRuleTemplateConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert(String name, byte[] input) {
        String xml = new String(input);
        TemplateModel model = (TemplateModel) BRDRTXMLPersistence.getInstance().unmarshal( xml );

        String drl = new StringBuilder()
                .append( getPackageDeclaration(name) )
                .append( model.getImports().toString() ).append( "\n" )
                .append( BRDRTPersistence.getInstance().marshal(model) ).toString();

        return new FormatConversionResult( getDestinationName(name, model.hasDSLSentences()), drl.getBytes() );
    }
}