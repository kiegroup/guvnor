package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.PushResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * This is the backchannel to send "push" messages to the browser.
 * Here be dragons. Would like to convert this to using actors one day. 
 * @author Michael Neale
 */
public class Backchannel {

    final List<CountDownLatch> waiting = Collections.synchronizedList(new ArrayList<CountDownLatch>());
    final Map<String, List<PushResponse>> mailbox = Collections.synchronizedMap(new HashMap<String, List<PushResponse>>());

    public List<PushResponse> await(String userName) throws InterruptedException {
        List<PushResponse> messages = mailbox.remove(userName);
        if (messages != null && messages.size() > 0) {
            return messages;
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            waiting.add(latch);

            /**
             * Now PAUSE here for a while....
             */
            latch.await();


            /** In the meantime... response has been set, and then it will be unlatched, and message sent back... */
            return mailbox.remove(userName);
        }
    }


    public synchronized void push(String userName, PushResponse message) {
        //need to queue this up in the users mailbox, and then wake it all up
        List<PushResponse> resp = mailbox.get(userName);
        if (resp == null) {
            resp = new ArrayList<PushResponse>();
            resp.add(message);
            mailbox.put(userName,resp);
        } else {
            resp.add(message);            
        }

        synchronized (waiting) {
            Iterator<CountDownLatch> it = waiting.iterator();
            while (it.hasNext()) {
                CountDownLatch l = it.next();
                l.countDown();
                it.remove();
            }
        }

    }




}
