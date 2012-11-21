package org.drools.guvnor.client.asseteditor.ruleflow;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ElementContainerTransferNode_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.guvnor.client.rpc.RuleFlowContentModel getContentModel(org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode::contentModel;
  }-*/;
  
  private static native void setContentModel(org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode instance, org.drools.guvnor.client.rpc.RuleFlowContentModel value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode::contentModel = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode instance) throws SerializationException {
    setContentModel(instance, (org.drools.guvnor.client.rpc.RuleFlowContentModel) streamReader.readObject());
    
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode instance) throws SerializationException {
    streamWriter.writeObject(getContentModel(instance));
    
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.ruleflow.ElementContainerTransferNode)object);
  }
  
}
