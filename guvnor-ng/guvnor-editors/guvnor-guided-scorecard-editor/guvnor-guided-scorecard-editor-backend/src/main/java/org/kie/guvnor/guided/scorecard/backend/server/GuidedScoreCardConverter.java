package org.kie.guvnor.guided.scorecard.backend.server;

import org.kie.builder.impl.FormatConversionResult;
import org.kie.builder.impl.FormatConverter;
import org.drools.guvnor.models.commons.BaseConverter;
import org.kie.guvnor.guided.scorecard.backend.server.util.ScoreCardsXMLPersistence;
import org.kie.guvnor.guided.scorecard.model.ScoreCardModel;

import static org.kie.guvnor.guided.scorecard.backend.server.GuidedScoreCardSourceService.getDRLBody;

public class GuidedScoreCardConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert(String name, byte[] input) {
        String xml = new String(input);
        ScoreCardModel model = ScoreCardsXMLPersistence.getInstance().unmarshall( xml );

        String drl = new StringBuilder()
                .append( getPackageDeclaration(name) )
                .append( model.getImports().toString() ).append( "\n" )
                .append( getDRLBody(model) ).toString();

        return new FormatConversionResult( getDestinationName(name), drl.getBytes() );
    }
}
