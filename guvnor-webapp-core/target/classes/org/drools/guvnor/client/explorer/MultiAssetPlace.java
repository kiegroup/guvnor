package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import org.drools.guvnor.client.asseteditor.MultiViewRow;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        MultiAssetPlace that = (MultiAssetPlace) o;


        if ( multiViewRows != null ) {
            Object[] thisObjects = multiViewRows.toArray();
            Object[] thatObjects = that.getMultiViewRows().toArray();

            Arrays.sort( thisObjects );
            Arrays.sort( thatObjects );

            if ( !Arrays.equals( thisObjects, thatObjects ) )
                return false;
        } else {
            if ( that.multiViewRows != null )
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if ( multiViewRows != null ) {
            Object[] thisObjects = multiViewRows.toArray();
            Arrays.sort( thisObjects );

            return Arrays.hashCode( thisObjects );
        } else {
            return 0;
        }
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
