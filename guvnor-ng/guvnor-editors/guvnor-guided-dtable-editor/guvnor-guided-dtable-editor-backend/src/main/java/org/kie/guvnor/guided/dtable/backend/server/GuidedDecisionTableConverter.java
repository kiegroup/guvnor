package org.kie.guvnor.guided.dtable.backend.server;

import org.kie.builder.impl.FormatConversionResult;
import org.kie.builder.impl.FormatConverter;
import org.kie.guvnor.commons.service.source.BaseConverter;
import org.kie.guvnor.guided.dtable.backend.server.util.GuidedDTDRLPersistence;
import org.kie.guvnor.guided.dtable.backend.server.util.GuidedDTXMLPersistence;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;

import static org.kie.guvnor.guided.dtable.backend.server.GuidedDecisionTableSourceService.hasDSLSentences;

public class GuidedDecisionTableConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert(String name, byte[] input) {
        String xml = new String(input);
        GuidedDecisionTable52 model = GuidedDTXMLPersistence.getInstance().unmarshal( xml );

        String drl = new StringBuilder()
                .append( getPackageDeclaration(name) )
                .append( model.getImports().toString() ).append( "\n" )
                .append( GuidedDTDRLPersistence.getInstance().marshal(model) ).toString();

        return new FormatConversionResult( getDestinationName(name, hasDSLSentences(model)), drl.getBytes() );
    }

}
