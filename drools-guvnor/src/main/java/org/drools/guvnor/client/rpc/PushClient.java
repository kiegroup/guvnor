package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.core.client.GWT;

import java.util.List;
import java.util.ArrayList;

/**
 * This manages "subscriptions" for when messages are pushed from the server.
 * @author Michael Neale
 */
public class PushClient {


    private static final PushClient INSTANCE = new PushClient();

    public static PushClient instance() {
        return INSTANCE;
    }
    
    private PushClient() {
    }

    private List<ServerPushNotification> callbacks = new ArrayList<ServerPushNotification>();
    private boolean connected = false;

    public void subscribe(ServerPushNotification pushed) {
        if (!connected) connect();
        callbacks.add(pushed);
    }

    private void connect() {
        connected = true;
        System.err.println("Connecting" + System.currentTimeMillis());
        RepositoryServiceFactory.getService().subscribe(new AsyncCallback<List<PushResponse>>() {
            public void onFailure(Throwable caught) {
                System.err.println("FAIL" + System.currentTimeMillis());
                connect();
            }

            public void onSuccess(List<PushResponse> result) {
                System.err.println("Got response !" + System.currentTimeMillis());
                processResult(result);
                connect();
            }
        });

    }

    private void processResult(List<PushResponse> result) {
        if (result == null) {
            System.err.println("NULL result :(");
            return;
        }
        for (PushResponse msg: result) {
            System.err.println("Processing pushed message:" + msg.messageType + "," + msg.message);
            for (ServerPushNotification pn : callbacks) {
                System.err.println("performing callback...");
                pn.messageReceived(msg);
            }
        }
    }

    public void unsubscribe(ServerPushNotification pushed) {
        callbacks.remove(pushed);
    }

}
