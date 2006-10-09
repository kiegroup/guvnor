package org.drools.brms.client.breditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListUtil {

    public static List filter(List source,
                             String filterVal) {
        if (filterVal == null || "".equals( filterVal.trim() )) {
            return source;
        }
        ArrayList filteredList = new ArrayList();
        for ( Iterator iter = source.iterator(); iter.hasNext(); ) {
            String item = (String) iter.next();
            if (item.startsWith( filterVal )) {
                filteredList.add( item );
            }
        }
        return filteredList;
    }

}
