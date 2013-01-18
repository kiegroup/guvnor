package org.kie.guvnor.commons.ui.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.commons.ui.service.EnumDropDownService;

/**
 * Default implementation of EnumDropDownService
 */
@Service
@ApplicationScoped
public class EnumDropDownServiceImpl implements EnumDropDownService {

    @Override
    public String[] loadDropDownExpression( final String[] valuePairs,
                                            final String expression ) {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
