package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Cause_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getCause(org.drools.guvnor.client.rpc.Cause instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Cause::cause;
  }-*/;
  
  private static native void setCause(org.drools.guvnor.client.rpc.Cause instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Cause::cause = value;
  }-*/;
  
  private static native org.drools.guvnor.client.rpc.Cause[] getCauses(org.drools.guvnor.client.rpc.Cause instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.Cause::causes;
  }-*/;
  
  private static native void setCauses(org.drools.guvnor.client.rpc.Cause instance, org.drools.guvnor.client.rpc.Cause[] value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.Cause::causes = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.Cause instance) throws SerializationException {
    setCause(instance, streamReader.readString());
    setCauses(instance, (org.drools.guvnor.client.rpc.Cause[]) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.rpc.Cause instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.Cause();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.Cause instance) throws SerializationException {
    streamWriter.writeString(getCause(instance));
    streamWriter.writeObject(getCauses(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.Cause_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Cause_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.Cause)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.Cause_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.Cause)object);
  }
  
}
