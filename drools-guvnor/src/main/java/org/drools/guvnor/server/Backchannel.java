package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.PushResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * This is the backchannel to send "push" messages to the browser.
 * Here be dragons. Would like to convert this to using actors one day.
 * TODO: convert to executor architecture. Only one instance needed.
 * @author Michael Neale
 */
public class Backchannel {
    final List<CountDownLatch> waiting = Collections.synchronizedList(new ArrayList<CountDownLatch>());
    final Map<String, List<PushResponse>> mailbox = Collections.synchronizedMap(new HashMap<String, List<PushResponse>>());
    
    private Timer timer;


    public Backchannel() {

        //using a timer to make sure awaiting subs are flushed every now and then, otherwise web threads could be consumed.
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
               unlatchAllWaiting();
            }
        }, 20000, 30000);
    }




    public List<PushResponse> await(String userName) throws InterruptedException {
        List<PushResponse> messages = fetchMessageForUser(userName);
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
            return fetchMessageForUser(userName);
        }
    }

    /**
     * Fetch the list of messages waiting, if there are some, replace it with an empty list.
     */
    private List<PushResponse> fetchMessageForUser(String userName) {
        List<PushResponse> msgs = mailbox.get(userName);
        mailbox.put(userName, new ArrayList<PushResponse>());
        return msgs;
    }


    /** Push out a message to the specific client */
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

        unlatchAllWaiting();

    }

    /**
     * Push out a message to all currently connected clients
     */
    public synchronized void publish(PushResponse message) {
        for(Map.Entry<String, List<PushResponse>> e : mailbox.entrySet()) {
            if (e.getValue() == null) e.setValue(new ArrayList<PushResponse>());
            e.getValue().add(message);
        }
        unlatchAllWaiting();
    }

    private void unlatchAllWaiting() {
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
