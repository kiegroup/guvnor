package org.jboss.drools.guvnor.importgenerator.example.domain;

import java.math.BigDecimal;

public class ValueVO {
  private BigDecimal value;
  private String text;
  public BigDecimal getValue() {
    return value;
  }
  public void setValue(BigDecimal value) {
    this.value = value;
  }
  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }
  
}
