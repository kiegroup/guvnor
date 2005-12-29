package org.drools.repository.db;

import org.hsqldb.Server;

import junit.framework.TestCase;

public class HibernateTester extends TestCase {
    
    private static Server server;
    
    public static void startServer() {
        if (server == null) {
            System.out.println("Starting HSQLDB for tests.");
            server = new Server();
            server.start();
            try {
                Thread.sleep(3000);
            }
            catch ( InterruptedException e ) {}
        }
    }
    
    public static void stopServer() {
        System.out.println("Starting HSQLDB for tests.");        
        server.stop();
    }

}
