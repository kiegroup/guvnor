/**
 * 
 */
package org.jboss.bpm.console.client.history;

import java.util.List;

import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.AbstractRESTAction;
import org.jboss.bpm.console.client.common.DataDriven;
import org.jboss.bpm.console.client.model.HistoryProcessInstanceRef;
import org.jboss.bpm.console.client.model.JSOParser;
import org.jboss.bpm.console.client.util.ConsoleLog;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.mvc4g.client.Controller;

/**
 * @author Jeff Yu
 * @date Mar 17, 2011
 */
public class LoadProcessHistoryAction extends AbstractRESTAction {
	
	public static final String ID = LoadProcessHistoryAction.class.getName();

    protected LoadProcessHistoryAction(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
	public String getId() {
		return ID;
	}
	
	
	@Override
	protected DataDriven getDataDriven(Controller controller) {
		return (ProcessHistoryInstanceListView)controller.getView(ProcessHistoryInstanceListView.ID);
	}

	@Override
	public String getUrl(Object event) {
		ProcessSearchEvent searchEvent = (ProcessSearchEvent) event;
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append("status=");
		sbuffer.append(searchEvent.getStatus());
		sbuffer.append("&starttime=");
		sbuffer.append(searchEvent.getStartTime());
		sbuffer.append("&endtime=");
		sbuffer.append(searchEvent.getEndTime());
		if (searchEvent.getKey() != null && !("".equals(searchEvent.getKey()))) {
			sbuffer.append("&correlationkey=");
			sbuffer.append(URL.encode(searchEvent.getKey().replace("=", "~")));
		}
		return URLBuilder.getInstance().getProcessHistoryURL(searchEvent.getDefinitionKey(), sbuffer.toString());
	}

	
	@Override
	public Method getRequestMethod() {
		return RequestBuilder.GET;
	}

	/* (non-Javadoc)
	 * @see org.jboss.bpm.console.client.common.AbstractRESTAction#handleSuccessfulResponse(com.mvc4g.client.Controller, java.lang.Object, com.google.gwt.http.client.Response)
	 */
	@Override
	public void handleSuccessfulResponse(Controller controller, Object event, Response response) {
		ProcessHistoryInstanceListView view = (ProcessHistoryInstanceListView) controller.getView(ProcessHistoryInstanceListView.ID);
		List<HistoryProcessInstanceRef> ref = JSOParser.parseProcessDefinitionHistory(response.getText());
		view.update(ref);
		
		ConsoleLog.debug("Loaded " + ref.size() + " process instance(s) : " + response.getText());
	}

}
