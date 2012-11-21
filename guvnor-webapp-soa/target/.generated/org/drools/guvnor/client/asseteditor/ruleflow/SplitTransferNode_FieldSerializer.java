package org.drools.guvnor.client.asseteditor.ruleflow;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SplitTransferNode_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Map getConstraints(org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode::constraints;
  }-*/;
  
  private static native void setConstraints(org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode instance, java.util.Map value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode::constraints = value;
  }-*/;
  
  private static native org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode.Type getSplitType(org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode::splitType;
  }-*/;
  
  private static native void setSplitType(org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode instance, org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode.Type value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode::splitType = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode instance) throws SerializationException {
    setConstraints(instance, (java.util.Map) streamReader.readObject());
    setSplitType(instance, (org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode.Type) streamReader.readObject());
    
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode instance) throws SerializationException {
    streamWriter.writeObject(getConstraints(instance));
    streamWriter.writeObject(getSplitType(instance));
    
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.ruleflow.SplitTransferNode)object);
  }
  
}
