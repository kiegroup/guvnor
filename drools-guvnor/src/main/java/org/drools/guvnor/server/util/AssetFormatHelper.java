package org.drools.guvnor.server.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.repository.RulesRepositoryException;

/**
 * Needed to list what registered format types there are.
 * @author Michael Neale
 */
public class AssetFormatHelper {

	public static String[] listRegisteredTypes() {
		try {
			Field[] flds = AssetFormats.class.getFields();
			List<String> r = new ArrayList<String>();
			for (int i = 0; i < flds.length; i++) {
				Object val =  flds[i].get(AssetFormats.class);
				if (val instanceof String) {
					r.add((String) val);
				}

			}
			return r.toArray(new String[r.size()]);
		} catch (Exception e) {
			throw new RulesRepositoryException(e);
		}
	}

}
