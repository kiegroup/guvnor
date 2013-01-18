package org.kie.guvnor.commons.ui.service;

import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service definition to accompany EnumDropDown widget that can populate itself from MVEL expressions evaluated on the server
 */
@Remote
public interface EnumDropDownService {

    /**
     * @param valuePairs key=value pairs to be interpolated into the expression.
     * @param expression The expression, which will then be eval'ed to generate a
     * String[]
     */
    String[] loadDropDownExpression( final String[] valuePairs,
                                     final String expression );

}
