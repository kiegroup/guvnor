package org.jboss.bpm.console.server.util;

import org.jboss.bpm.console.server.InfoFacade;

import java.io.*;

/**
 * User: Jeff Yu
 * Date: 31/03/11
 */
public class RsDocGenerator {

    private File output;

    private String projectName;

    private static final String contextPath = "/gwt-console-server/rs";

    private RsDocBuilder builder;

    public RsDocGenerator(String outputDir) throws Exception {
       try {
            this.output = new File(outputDir);
            if(!this.output.exists())
                this.output.mkdirs();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.builder = new RsDocBuilder(contextPath, InfoFacade.getRSResources());
    }


    public void generate(String project, String type) {
        String filename = this.output.getAbsolutePath() + "/" + project + "_restful_service." + type;

        String result = null;

        if ("html".equalsIgnoreCase(type)) {
            result = builder.build2HTML(project).toString();
        } else if ("xml".equalsIgnoreCase(type)) {
            result = builder.build2Docbook(project).toString();
        }

        Writer out = null;
        try{
           out = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
           out.write(result);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
           if (out != null) {
               try {
                   out.close();
               } catch (IOException ie) {
                   throw new RuntimeException("Error in closing IO.", ie);
               }
           }

        }

    }


    public static void main(String[] args) throws Exception{

      String dir = args[0];

      RsDocGenerator generator = new RsDocGenerator(dir);
      generator.generate("riftsaw", "html");
      generator.generate("riftsaw", "xml");

      generator.generate("jbpm", "html");
      generator.generate("jbpm", "xml");
    }

}
