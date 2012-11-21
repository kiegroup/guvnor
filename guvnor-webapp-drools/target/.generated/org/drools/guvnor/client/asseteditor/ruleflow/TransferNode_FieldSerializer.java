package org.drools.guvnor.client.asseteditor.ruleflow;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TransferNode_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getId(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::id;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setId(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance, long value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::id = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::name = value;
  }-*/;
  
  private static native org.drools.guvnor.client.asseteditor.ruleflow.TransferNode.Type getType(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::type;
  }-*/;
  
  private static native void setType(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance, org.drools.guvnor.client.asseteditor.ruleflow.TransferNode.Type value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::type = value;
  }-*/;
  
  private static native int getX(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::x;
  }-*/;
  
  private static native void setX(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::x = value;
  }-*/;
  
  private static native int getY(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::y;
  }-*/;
  
  private static native void setY(org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.TransferNode::y = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance) throws SerializationException {
    instance.height = streamReader.readInt();
    setId(instance, streamReader.readLong());
    setName(instance, streamReader.readString());
    setType(instance, (org.drools.guvnor.client.asseteditor.ruleflow.TransferNode.Type) streamReader.readObject());
    instance.width = streamReader.readInt();
    setX(instance, streamReader.readInt());
    setY(instance, streamReader.readInt());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.ruleflow.TransferNode();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.ruleflow.TransferNode instance) throws SerializationException {
    streamWriter.writeInt(instance.height);
    streamWriter.writeLong(getId(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getType(instance));
    streamWriter.writeInt(instance.width);
    streamWriter.writeInt(getX(instance));
    streamWriter.writeInt(getY(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.ruleflow.TransferNode)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.ruleflow.TransferNode)object);
  }
  
}
