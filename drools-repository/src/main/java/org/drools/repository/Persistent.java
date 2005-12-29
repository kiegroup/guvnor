package org.drools.repository;

import java.io.Serializable;

public class Persistent implements Serializable {
    
    private Long id;

    public Long getId(){
        return id;
    }

    private void setId(Long id){
        this.id = id;
    }
    

}
