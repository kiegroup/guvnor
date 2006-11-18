package org.drools.resource;

public class ResourceType {
    public static final ResourceType DRL_FILE = new ResourceType( 1 );
    public static final ResourceType RULE     = new ResourceType( 2 );
    public static final ResourceType FUNCTION = new ResourceType( 3 );
    public static final ResourceType DSL_FILE = new ResourceType( 4 );
    public static final ResourceType XLS_FILE = new ResourceType( 5 );
    
    
    private int type;
    
    private ResourceType(int type) {
        this.type = type;
    }
    
    public int getType()  {
        return this.type;
    }
    
    private Object readResolve() throws java.io.ObjectStreamException {
        switch ( this.type ) {
            case 1:
                return DRL_FILE;
            case 2:
                return RULE;
            case 3:
                return FUNCTION;
            case 4:
                return DSL_FILE;
            case 5:
                return XLS_FILE;
                default:
        }
        throw new RuntimeException( "unable to determine ResourceType for value [" + this.type + "]" );
    }    
}
