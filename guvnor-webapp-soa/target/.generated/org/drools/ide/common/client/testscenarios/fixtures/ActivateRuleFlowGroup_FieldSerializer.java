package org.drools.ide.common.client.testscenarios.fixtures;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActivateRuleFlowGroup_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getName(org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup::name = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup instance) throws SerializationException {
    setName(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup instance) throws SerializationException {
    streamWriter.writeString(getName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup_FieldSerializer.serialize(writer, (org.drools.ide.common.client.testscenarios.fixtures.ActivateRuleFlowGroup)object);
  }
  
}
