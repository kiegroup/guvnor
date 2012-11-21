package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RuleModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getIsNegated(org.drools.ide.common.client.modeldriven.brl.RuleModel instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.RuleModel::isNegated;
  }-*/;
  
  private static native void setIsNegated(org.drools.ide.common.client.modeldriven.brl.RuleModel instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.RuleModel::isNegated = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.RuleModel instance) throws SerializationException {
    instance.attributes = (org.drools.ide.common.client.modeldriven.brl.RuleAttribute[]) streamReader.readObject();
    setIsNegated(instance, streamReader.readBoolean());
    instance.lhs = (org.drools.ide.common.client.modeldriven.brl.IPattern[]) streamReader.readObject();
    instance.metadataList = (org.drools.ide.common.client.modeldriven.brl.RuleMetadata[]) streamReader.readObject();
    instance.modelVersion = streamReader.readString();
    instance.name = streamReader.readString();
    instance.parentName = streamReader.readString();
    instance.rhs = (org.drools.ide.common.client.modeldriven.brl.IAction[]) streamReader.readObject();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.RuleModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.RuleModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.RuleModel instance) throws SerializationException {
    streamWriter.writeObject(instance.attributes);
    streamWriter.writeBoolean(getIsNegated(instance));
    streamWriter.writeObject(instance.lhs);
    streamWriter.writeObject(instance.metadataList);
    streamWriter.writeString(instance.modelVersion);
    streamWriter.writeString(instance.name);
    streamWriter.writeString(instance.parentName);
    streamWriter.writeObject(instance.rhs);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.RuleModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.RuleModel_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.RuleModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.RuleModel_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.RuleModel)object);
  }
  
}
