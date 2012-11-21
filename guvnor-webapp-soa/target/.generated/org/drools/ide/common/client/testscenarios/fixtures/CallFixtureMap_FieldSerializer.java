package org.drools.ide.common.client.testscenarios.fixtures;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CallFixtureMap_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.util.HashMap_CustomFieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.util.HashMap_CustomFieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap_FieldSerializer.serialize(writer, (org.drools.ide.common.client.testscenarios.fixtures.CallFixtureMap)object);
  }
  
}
