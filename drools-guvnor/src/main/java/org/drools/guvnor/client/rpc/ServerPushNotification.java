package org.drools.guvnor.client.rpc;

/**
 * @author Michael Neale
 */
public interface ServerPushNotification {
    public void messageReceived(PushResponse response);
}
