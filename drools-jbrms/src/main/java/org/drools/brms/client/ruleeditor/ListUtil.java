package org.drools.brms.client.ruleeditor;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.modeldriven.brxml.DSLSentence;

public class ListUtil {

    public static List filter(DSLSentence[] source,
                             String filterVal) {

        ArrayList filteredList = new ArrayList();
        for ( int i = 0; i < source.length; i++ ) {
            DSLSentence item = source[i];
            if (filterVal.equals( "" ) || item.sentence.startsWith( filterVal )) {
                filteredList.add( item );
            } 
        }
        return filteredList;
    }

}
