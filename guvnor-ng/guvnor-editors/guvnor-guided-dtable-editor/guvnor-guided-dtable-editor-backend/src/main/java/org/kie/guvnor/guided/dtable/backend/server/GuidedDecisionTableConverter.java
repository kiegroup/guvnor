package org.kie.guvnor.guided.dtable.backend.server;

import org.kie.builder.impl.FormatConversionResult;
import org.kie.builder.impl.FormatConverter;
import org.kie.guvnor.guided.dtable.backend.server.util.GuidedDTDRLPersistence;
import org.kie.guvnor.guided.dtable.backend.server.util.GuidedDTXMLPersistence;
import org.kie.guvnor.guided.dtable.model.GuidedDecisionTable52;

import static org.kie.guvnor.guided.dtable.backend.server.GuidedDecisionTableSourceService.hasDSLSentences;

public class GuidedDecisionTableConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert(String name, byte[] input) {
        String xml = new String(input);
        GuidedDecisionTable52 model = GuidedDTXMLPersistence.getInstance().unmarshal( xml );

        String drl = new StringBuilder()
                .append(model.getImports().toString()).append("\n")
                .append(GuidedDTDRLPersistence.getInstance().marshal(model)).toString();

        String destinationPath = name.substring(0, name.lastIndexOf('.')) + (hasDSLSentences(model) ? ".dslr" : ".drl");

        return new FormatConversionResult(destinationPath, drl.getBytes());
    }
}
