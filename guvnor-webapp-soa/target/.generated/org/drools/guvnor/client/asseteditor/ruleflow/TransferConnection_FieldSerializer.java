package org.drools.guvnor.client.asseteditor.ruleflow;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TransferConnection_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getFromId(org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection::fromId;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setFromId(org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection instance, long value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection::fromId = value;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getToId(org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection::toId;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setToId(org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection instance, long value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection::toId = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection instance) throws SerializationException {
    setFromId(instance, streamReader.readLong());
    setToId(instance, streamReader.readLong());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection instance) throws SerializationException {
    streamWriter.writeLong(getFromId(instance));
    streamWriter.writeLong(getToId(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.ruleflow.TransferConnection)object);
  }
  
}
