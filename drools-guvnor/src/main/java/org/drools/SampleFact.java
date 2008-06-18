package org.drools;

import java.util.Date;

public class SampleFact {

    private String name;
    private boolean isTrue;
    private int number;
    private Date dateOccurred;

    public Date getDateOccurred() {
        return dateOccurred;
    }
    public void setDateOccurred(Date dateOccurred) {
        this.dateOccurred = dateOccurred;
    }
    public boolean isTrue() {
        return isTrue;
    }
    public void setTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }



}
