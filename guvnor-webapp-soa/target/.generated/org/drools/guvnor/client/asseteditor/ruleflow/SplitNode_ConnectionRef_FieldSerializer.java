package org.drools.guvnor.client.asseteditor.ruleflow;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SplitNode_ConnectionRef_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getNodeId(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$ConnectionRef::nodeId;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setNodeId(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef instance, long value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$ConnectionRef::nodeId = value;
  }-*/;
  
  private static native java.lang.String getToType(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$ConnectionRef::toType;
  }-*/;
  
  private static native void setToType(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$ConnectionRef::toType = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef instance) throws SerializationException {
    setNodeId(instance, streamReader.readLong());
    setToType(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef instance) throws SerializationException {
    streamWriter.writeLong(getNodeId(instance));
    streamWriter.writeString(getToType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.ruleflow.SplitNode_ConnectionRef_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.SplitNode_ConnectionRef_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.SplitNode_ConnectionRef_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.ConnectionRef)object);
  }
  
}
