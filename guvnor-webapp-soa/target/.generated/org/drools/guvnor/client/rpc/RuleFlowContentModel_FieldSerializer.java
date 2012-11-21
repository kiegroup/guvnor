package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RuleFlowContentModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Collection getConnections(org.drools.guvnor.client.rpc.RuleFlowContentModel instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::connections;
  }-*/;
  
  private static native void setConnections(org.drools.guvnor.client.rpc.RuleFlowContentModel instance, java.util.Collection value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::connections = value;
  }-*/;
  
  private static native java.lang.String getJson(org.drools.guvnor.client.rpc.RuleFlowContentModel instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::json;
  }-*/;
  
  private static native void setJson(org.drools.guvnor.client.rpc.RuleFlowContentModel instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::json = value;
  }-*/;
  
  private static native java.util.List getNodes(org.drools.guvnor.client.rpc.RuleFlowContentModel instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::nodes;
  }-*/;
  
  private static native void setNodes(org.drools.guvnor.client.rpc.RuleFlowContentModel instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::nodes = value;
  }-*/;
  
  private static native java.lang.String getPreprocessingdata(org.drools.guvnor.client.rpc.RuleFlowContentModel instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::preprocessingdata;
  }-*/;
  
  private static native void setPreprocessingdata(org.drools.guvnor.client.rpc.RuleFlowContentModel instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::preprocessingdata = value;
  }-*/;
  
  private static native java.lang.String getXml(org.drools.guvnor.client.rpc.RuleFlowContentModel instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::xml;
  }-*/;
  
  private static native void setXml(org.drools.guvnor.client.rpc.RuleFlowContentModel instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.RuleFlowContentModel::xml = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.RuleFlowContentModel instance) throws SerializationException {
    setConnections(instance, (java.util.Collection) streamReader.readObject());
    setJson(instance, streamReader.readString());
    setNodes(instance, (java.util.List) streamReader.readObject());
    setPreprocessingdata(instance, streamReader.readString());
    setXml(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.RuleFlowContentModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.RuleFlowContentModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.RuleFlowContentModel instance) throws SerializationException {
    streamWriter.writeObject(getConnections(instance));
    streamWriter.writeString(getJson(instance));
    streamWriter.writeObject(getNodes(instance));
    streamWriter.writeString(getPreprocessingdata(instance));
    streamWriter.writeString(getXml(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.RuleFlowContentModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.RuleFlowContentModel_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.RuleFlowContentModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.RuleFlowContentModel_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.RuleFlowContentModel)object);
  }
  
}
