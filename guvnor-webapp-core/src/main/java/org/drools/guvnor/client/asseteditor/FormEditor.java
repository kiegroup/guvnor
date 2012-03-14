package org.drools.guvnor.client.asseteditor;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.FormContentModel;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.UserSecurityContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;

public class FormEditor extends DirtyableComposite implements
SaveEventListener,
EditorWidget {

    private String    modelUUID;
    private Asset asset;
    private Frame     frame;
    
    private String[] username = new String[] { null }; 

    private final RepositoryServiceAsync repoService;
    
    public FormEditor(Asset asset, RuleViewer viewer, ClientFactory clientFactory, EventBus bus) {
        this.asset = asset;
        modelUUID = asset.getUuid();
        SecurityServiceAsync securityService = GWT.create(SecurityService.class);
        securityService.getCurrentUser(new AsyncCallback<UserSecurityContext>() {
            public void onSuccess(UserSecurityContext result) {
                username[0] = result.getUserName();
            }
            public void onFailure(Throwable caught) { }
        });
        this.repoService = clientFactory.getRepositoryService();
        initWidgets();
    }
    
    private void initWidgets() {
        String name;

        /**
         EditorLauncher.HOSTED_MODE = Boolean.TRUE; // HACK to set it to HOSTED MODE
         if ( EditorLauncher.HOSTED_MODE.booleanValue() ) {
             name = "http://localhost:8080/jbpm-form-builder/embed";
         } else {
             name = "/jbpm-form-builder/embed";
         } **/

        name = "/" + ApplicationPreferences.getFormBuilderContext() + "/embed?uuid=" + 
                modelUUID + "&profile=" + ApplicationPreferences.getFormBuilderProfile();
        if (username[0] != null) {
            name += "&username=" + username[0];
        }
        frame = new Frame( name );
        frame.getElement().setAttribute( "domain", Document.get().getDomain() );
        frame.setWidth( "100%" );
        frame.setHeight( "580px" );
        initWidget( frame );
        setWidth( "100%" );
        setHeight( "580px" );
    }

    private final native String callSave(IFrameElement iframe) /*-{
        var exportForm = null;
        if (typeof(iframe.contentWindow) != 'undefined' && typeof(iframe.contentWindow.document) != 'undefined') {
            if (typeof(iframe.contentWindow.document.clientExportForm) != 'undefined') {
                exportForm = iframe.contentWindow.document.clientExportForm;
            }
        } 
        if ((exportForm == null || exportForm == "") && typeof(iframe.contentDocument) != 'undefined') {
            if (typeof(iframe.contentDocument.clientExportForm) != 'undefined') {
                exportForm = iframe.contentDocument.clientExportForm;
            }
        }
        return exportForm;
    }-*/;

    public void onSave() {
        try {
            String json = callSave( (IFrameElement) ((com.google.gwt.dom.client.Element) frame.getElement()) );
            if (json == null || "".equals(json.trim())) {
                Window.alert("Warning: form is empty from guvnor perspective.");
            }
            if ( asset.getContent() == null ) {
                asset.setContent( new FormContentModel() );
            }
            ((FormContentModel) asset.getContent()).setJson( json );
        } catch ( Exception e ) {
            GWT.log( "JSNI method callSave() threw an exception:",
                     e );
            Window.alert( "JSNI method callSave() threw an exception: " + e );
        }
    }

    public void onAfterSave() {
        String json  = ((FormContentModel) asset.getContent()).getJson();
        exportFormToFtl(json);
    }

    private void exportFormToFtl(String jsonForm) {
        String url = ApplicationPreferences.getFormBuilderURL() + "/" + 
                ApplicationPreferences.getFormBuilderContext() + 
                "/exportTemplate?uuid=" + modelUUID + "&profile=jbpm";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setCallback(new RequestCallback() {
            public void onResponseReceived(Request request, Response response) {
                if (response.getStatusCode() == 500) {
                    Window.alert( "there was a problem with the template creation. Please check the server logs");
                }
            }
            public void onError(Request request, Throwable exception) {
                GWT.log( "template creation threw an exception:", exception );
                Window.alert( "template creation threw an exception: " + exception );
            }
        });
        try {
            builder.send();
        } catch (RequestException e) {
            GWT.log( "template creator invoke threw an exception:", e );
            Window.alert( "template creator invoke threw an exception: " + e );
        }
    }
}
