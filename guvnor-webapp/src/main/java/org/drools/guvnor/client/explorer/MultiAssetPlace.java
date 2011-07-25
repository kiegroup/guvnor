package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultiAssetPlace extends Place {

    private final Collection<MultiViewRow> multiViewRows;

    public MultiAssetPlace(Collection<MultiViewRow> multiViewRows) {
        this.multiViewRows = multiViewRows;
    }

    public Collection<MultiViewRow> getMultiViewRows() {
        return multiViewRows;
    }

    public static class Tokenizer implements PlaceTokenizer<MultiAssetPlace> {

        public MultiAssetPlace getPlace(String token) {
            return new MultiAssetPlace( stripMultiViewRows( token ) );
        }

        public String getToken(MultiAssetPlace place) {
            String token = "";
            boolean first = true;
            for (MultiViewRow multiViewRow : place.getMultiViewRows()) {
                if ( first ) {
                    token += formMultiViewRowToken( multiViewRow );
                    first = false;
                } else {
                    token += ",";
                    token += formMultiViewRowToken( multiViewRow );
                }
            }
            return token;
        }

        private String formMultiViewRowToken(MultiViewRow multiViewRow) {
            return "[" + multiViewRow.getUuid() + "|" + multiViewRow.getName() + "|" + multiViewRow.getFormat() + "]";
        }

        private List<MultiViewRow> stripMultiViewRows(String token) {
            List<MultiViewRow> rows = new ArrayList<MultiViewRow>();
            for (String multiRowToken : token.split( "," )) {
                rows.add( stripRow( multiRowToken ) );
            }
            return rows;
        }

        private MultiViewRow stripRow(String multiRowToken) {
            String[] fields = multiRowToken.replace( "[", "" ).replace( "]", "" ).split( "|" );
            return new MultiViewRow( fields[0], fields[1], fields[2] );
        }
    }
}
