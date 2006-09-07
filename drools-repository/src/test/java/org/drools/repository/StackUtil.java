package org.drools.repository;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class StackUtil {

    /**
     * Return the name of the routine that called getCurrentMethodName
     *
     * @author Johan Känngård, http://dev.kanngard.net
     * (found on the net in 2000, donŽt remember where...)
     */
    public static String getCurrentMethodName() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        (new Throwable()).printStackTrace(pw);
        pw.flush();
        String stackTrace = baos.toString();
        pw.close();

        StringTokenizer tok = new StringTokenizer(stackTrace, "\n");
        String l = tok.nextToken(); // 'java.lang.Throwable'
        l = tok.nextToken(); // 'at ...getCurrentMethodName'
        l = tok.nextToken(); // 'at ...<caller to getCurrentRoutine>'
        // Parse line 3
        tok = new StringTokenizer(l.trim(), " <(");
        String t = tok.nextToken(); // 'at'
        t = tok.nextToken(); // '...<caller to getCurrentRoutine>'
        return t;
    }    
    
}
