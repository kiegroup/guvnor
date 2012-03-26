package org.jboss.drools.guvnor.importgenerator;

import java.io.File;

public class Model {
  public File file;
  public String content;
  
  public Model(File file, String content){
    this.file=file;
    this.content=content;
  }
  
  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
