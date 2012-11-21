package org.drools.guvnor.client.asseteditor.ruleflow;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class WorkItemTransferNode_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode instance) throws SerializationException {
    instance.parameters = (java.util.Map) streamReader.readObject();
    instance.workName = streamReader.readString();
    
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode instance) throws SerializationException {
    streamWriter.writeObject(instance.parameters);
    streamWriter.writeString(instance.workName);
    
    org.drools.guvnor.client.asseteditor.ruleflow.TransferNode_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.ruleflow.WorkItemTransferNode)object);
  }
  
}
