package org.jboss.drools.guvnor.importgenerator.example.domain;

import java.io.Serializable;

public class RuleResultVO implements Serializable {
  private String value;
  
  public RuleResultVO(String value){
    this.value=value;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }

}
