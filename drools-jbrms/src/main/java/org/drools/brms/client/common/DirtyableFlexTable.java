package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Composite;

public abstract class DirtyableFlexTable extends Composite implements IDirtyable  {
    private boolean dirtyflag = false;
    
    /* (non-Javadoc)
     * @see org.drools.brms.client.common.isDirtable#isDirty()
     */
    public boolean isDirty() {
        return this.dirtyflag;
    } 
    /* (non-Javadoc)
     * @see org.drools.brms.client.common.isDirtable#resetDirty()
     */
    public void resetDirty(){
        this.dirtyflag = false;
    }
    /* (non-Javadoc)
     * @see org.drools.brms.client.common.isDirtable#makeDirty()
     */
    public void makeDirty(){
        this.dirtyflag = true;
    }
}
