package org.drools.repository.db;

import java.io.Serializable;

/** The layer supertype for repository persistable classes. */ 
public class Asset implements Serializable {
    
    private Long id;

    public Long getId(){
        return id;
    }

    private void setId(Long id){
        this.id = id;
    }
    

}
