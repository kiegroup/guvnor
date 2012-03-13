package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface DroolsServiceAsync {
    /**
     * Creates a brand new Guided Decision Table rule with the initial category.
     * Return the UUID of the item created. This will not check in the rule, but
     * just leave it as saved in the repo.
     */
    void createNewRule(NewGuidedDecisionTableAssetConfiguration configuration, AsyncCallback<String> async);

    /**
     * Validate module configuration
     *
     * @return A ValidatedReponse, with any errors to be reported. No payload is
     *         in the response. If there are any errors, the user should be
     *         given the option to review them, and correct them if needed (but
     *         a save will not be prevented this way - as its not an exception).
     */
    void validateModule(Module data, AsyncCallback<ValidatedResponse> async);
}
