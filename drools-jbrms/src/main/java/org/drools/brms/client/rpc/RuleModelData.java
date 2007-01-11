package org.drools.brms.client.rpc;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.RuleModel;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This wraps the rule model data with the suggestion completion engine for loading.
 * @author Michael Neale
 *
 */
public class RuleModelData
    implements
    IsSerializable {
    
    public RuleModel model;
    public SuggestionCompletionEngine completionEngine;

}
