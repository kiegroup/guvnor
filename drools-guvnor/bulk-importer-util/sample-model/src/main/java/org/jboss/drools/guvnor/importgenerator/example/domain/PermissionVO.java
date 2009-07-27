package org.jboss.drools.guvnor.importgenerator.example.domain;

import java.io.Serializable;

public class PermissionVO implements Serializable {
  String name;

  public PermissionVO(String name){
    this.name=name;
  }
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
