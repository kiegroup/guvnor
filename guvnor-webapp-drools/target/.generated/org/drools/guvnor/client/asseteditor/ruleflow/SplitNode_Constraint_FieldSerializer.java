package org.drools.guvnor.client.asseteditor.ruleflow;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SplitNode_Constraint_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getConstraint(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::constraint;
  }-*/;
  
  private static native void setConstraint(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::constraint = value;
  }-*/;
  
  private static native java.lang.String getDialect(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::dialect;
  }-*/;
  
  private static native void setDialect(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::dialect = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::name = value;
  }-*/;
  
  private static native int getPriority(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::priority;
  }-*/;
  
  private static native void setPriority(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::priority = value;
  }-*/;
  
  private static native java.lang.String getType(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::type;
  }-*/;
  
  private static native void setType(org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.ruleflow.SplitNode$Constraint::type = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance) throws SerializationException {
    setConstraint(instance, streamReader.readString());
    setDialect(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setPriority(instance, streamReader.readInt());
    setType(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint instance) throws SerializationException {
    streamWriter.writeString(getConstraint(instance));
    streamWriter.writeString(getDialect(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeInt(getPriority(instance));
    streamWriter.writeString(getType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.ruleflow.SplitNode_Constraint_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.SplitNode_Constraint_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.ruleflow.SplitNode_Constraint_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.ruleflow.SplitNode.Constraint)object);
  }
  
}
