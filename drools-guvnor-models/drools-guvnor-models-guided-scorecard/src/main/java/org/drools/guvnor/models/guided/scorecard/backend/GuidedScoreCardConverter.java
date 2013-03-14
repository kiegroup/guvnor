package org.drools.guvnor.models.guided.scorecard.backend;

import org.drools.guvnor.models.commons.backend.BaseConverter;
import org.drools.guvnor.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.builder.impl.FormatConversionResult;
import org.kie.builder.impl.FormatConverter;

public class GuidedScoreCardConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert( String name,
                                           byte[] input ) {
        String xml = new String( input );
        ScoreCardModel model = GuidedScoreCardXMLPersistence.getInstance().unmarshall( xml );

        String drl = new StringBuilder()
                .append( getPackageDeclaration( name ) )
                .append( model.getImports().toString() ).append( "\n" )
                .append( GuidedScoreCardDRLPersistence.marshal( model ) ).toString();

        return new FormatConversionResult( getDestinationName( name ), drl.getBytes() );
    }
}
